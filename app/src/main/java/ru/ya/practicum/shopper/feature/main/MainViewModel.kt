package ru.ya.practicum.shopper.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.core.util.Resource
import ru.ya.practicum.shopper.domain.model.ShopperList
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import java.io.IOException
import java.sql.SQLException

data class MainState(
    val isLoading: Boolean = true,
    val lists: List<ShoppingList> = emptyList(),
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val showIconPicker: Boolean = false,
    val selectedIconId: Int = 0,
    val newListName: String = "",
    val editingListId: Int? = null

)

sealed class MainEvent {
    data class CreateList(val name: String, val iconId: Int) : MainEvent()
    data class DeleteList(val listId: Int) : MainEvent()
    data class UpdateListName(val listId: Int, val newName: String) : MainEvent()
    object ShowAddDialog : MainEvent()
    object HideAddDialog : MainEvent()
    object ShowIconPicker : MainEvent()
    object HideIconPicker : MainEvent()
    data class SelectIcon(val iconId: Int) : MainEvent()
    data class UpdateNewListName(val name: String) : MainEvent()
    object LoadLists : MainEvent()
    data class UpdateListIcon(val listId: Int, val newIconId: Int) : MainEvent()
    data class ShowIconPickerForList(val listId: Int) : MainEvent()
}

@Suppress("TooManyFunctions", "UnusedPrivateProperty") // Подавлено
class MainViewModel(
    private val listRepository: ShopperListRepository,
    private val itemRepository: ShopperItemRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        loadLists()
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.CreateList -> createList(event.name, event.iconId)
            is MainEvent.DeleteList -> deleteList(event.listId)
            is MainEvent.UpdateListName -> updateListName(event.listId, event.newName)
            MainEvent.ShowAddDialog -> showAddDialog()
            MainEvent.HideAddDialog -> hideAddDialog()
            MainEvent.ShowIconPicker -> showIconPicker()
            MainEvent.HideIconPicker -> hideIconPicker()
            is MainEvent.SelectIcon -> selectIcon(event.iconId)
            is MainEvent.UpdateNewListName -> updateNewListName(event.name)
            MainEvent.LoadLists -> loadLists()
            is MainEvent.UpdateListIcon -> updateListIcon(event.listId, event.newIconId)
            is MainEvent.ShowIconPickerForList -> showIconPickerForList(event.listId)
        }
    }

    private fun showIconPickerForList(listId: Int) {
        _state.update { it.copy(showIconPicker = true, editingListId = listId) }
    }

    private fun updateListIcon(listId: Int, newIconId: Int) {
        viewModelScope.launch {
            try {
                val existingList = listRepository.getShopperListById(listId)
                if (existingList != null) {
                    val updatedList = existingList.copy(iconId = newIconId)
                    listRepository.updateShopperList(updatedList)
                }
                _state.update { it.copy(showIconPicker = false, editingListId = null) }
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния: ${e.message}") }
            }
        }
    }

    private fun loadLists() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            listRepository.getAllShopperLists()
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка загрузки списков: ${e.message}"
                        )
                    }
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val uiLists = resource.data?.map { it.toUiModel() } ?: emptyList()
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    lists = uiLists,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Ошибка загрузки списков"
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun createList(name: String, iconId: Int) {
        viewModelScope.launch {
            try {
                if (name.isBlank()) {
                    _state.update { it.copy(error = "Название не может быть пустым") }
                    return@launch
                }

                val newList = ShopperList(
                    id = 0,
                    name = name,
                    iconId = iconId,
                    createdAt = System.currentTimeMillis()
                )

                listRepository.addShopperList(newList)
                hideAddDialog()
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния: ${e.message}") }
            }
        }
    }

    private fun deleteList(listId: Int) {
        viewModelScope.launch {
            try {
                listRepository.deleteShopperListById(listId)
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных при удалении: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода при удалении: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния при удалении: ${e.message}") }
            }
        }
    }

    private fun updateListName(listId: Int, newName: String) {
        viewModelScope.launch {
            try {
                val existingList = listRepository.getShopperListById(listId)
                if (existingList != null) {
                    val updatedList = existingList.copy(name = newName)
                    listRepository.updateShopperList(updatedList)
                }
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных при обновлении: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода при обновлении: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния при обновлении: ${e.message}") }
            }
        }
    }

    private fun showAddDialog() {
        _state.update { it.copy(showAddDialog = true, showIconPicker = false, error = null) }
    }

    private fun hideAddDialog() {
        _state.update {
            it.copy(
                showAddDialog = false,
                showIconPicker = false,
                newListName = "",
                selectedIconId = 0
            )
        }
    }

    private fun showIconPicker() {
        _state.update { it.copy(showIconPicker = true) }
    }

    private fun hideIconPicker() {
        _state.update { it.copy(showIconPicker = false) }
    }

    private fun selectIcon(iconId: Int) {
        _state.update { it.copy(selectedIconId = iconId, showIconPicker = false) }
    }

    private fun updateNewListName(name: String) {
        _state.update { it.copy(newListName = name) }
    }

    private fun ShopperList.toUiModel(): ShoppingList {
        return ShoppingList(
            id = this.id.toInt(),
            name = this.name,
            iconResId = this.iconId
        )
    }
}
