package ru.ya.practicum.shopper.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.core.util.Resource
import ru.ya.practicum.shopper.domain.api.ShoppingListItemInteractor
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
    val editingListId: Int? = null,
    val showDeleteAllDialog: Boolean = false,
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",
    val searchInput: String = ""
)

data class ListState(
    val showEditShoppingListDialog: Boolean = false,
    val list: ShoppingList? = null,
    val showDeleteListDialog: Boolean = false
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
    object ShowDeleteAllDialog : MainEvent()
    object HideDeleteAllDialog : MainEvent()
    object ConfirmDeleteAll : MainEvent()
    object ToggleSearch : MainEvent()
    data class UpdateSearchQuery(val query: String) : MainEvent()
    object CloseSearch : MainEvent()
    object PerformSearch : MainEvent()

}

sealed class ListEvents {
    object HideEditShoppingListDialog : ListEvents()
    data class ShowEditShoppingListDialog(val list: ShoppingList) : ListEvents()
    data class SaveNewListName(val newName: String) : ListEvents()
    data class ShowDeleteListDialog(val list: ShoppingList) : ListEvents()
    object HideDeleteListDialog : ListEvents()
    object DeleteList : ListEvents()
    data class CopyList(val list: ShoppingList, val newName: String) : ListEvents()
}

@Suppress("TooManyFunctions", "UnusedPrivateProperty") // Подавлено
class MainViewModel(
    private val listRepository: ShopperListRepository,
    private val itemRepository: ShopperItemRepository,
    private val userId: String,
    private val shoppingListItemInteractor: ShoppingListItemInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _stateList = MutableStateFlow(ListState())
    val stateList: StateFlow<ListState> = _stateList.asStateFlow()
    private val _searchQueryInput = MutableStateFlow("")

    init {
        loadLists()
        setupSearchDebounce()
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQueryInput
                .debounce(SEARCH_DEBOUNCE_MS)
                .collect { query ->
                    _state.update { it.copy(searchQuery = query) }
                }
        }
    }

    fun onListEvent(event: ListEvents) {
        when (event) {
            is ListEvents.HideEditShoppingListDialog -> hideEditShoppingListDialog()
            is ListEvents.ShowEditShoppingListDialog -> showEditShoppingListDialog(event.list)
            is ListEvents.SaveNewListName -> saveNewListName(event.newName)
            is ListEvents.ShowDeleteListDialog -> showDeleteListDialog(event.list)
            is ListEvents.HideDeleteListDialog -> hideDeleteListDialog()
            is ListEvents.DeleteList -> deleteList()
            is ListEvents.CopyList -> copyList(event.list, event.newName)
        }
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.CreateList -> createList(event.name, event.iconId)
            is MainEvent.DeleteList -> deleteList(event.listId)
            is MainEvent.UpdateListName -> updateListName(event.listId, event.newName)
            is MainEvent.SelectIcon -> selectIcon(event.iconId)
            is MainEvent.UpdateNewListName -> updateNewListName(event.name)
            is MainEvent.UpdateListIcon -> updateListIcon(event.listId, event.newIconId)
            is MainEvent.ShowIconPickerForList -> showIconPickerForList(event.listId)
            is MainEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            else -> onSimpleEvent(event)
        }
    }

    private fun onSimpleEvent(event: MainEvent) {
        when (event) {
            MainEvent.ShowAddDialog -> showAddDialog()
            MainEvent.HideAddDialog -> hideAddDialog()
            MainEvent.ShowIconPicker -> showIconPicker()
            MainEvent.HideIconPicker -> hideIconPicker()
            MainEvent.LoadLists -> loadLists()
            MainEvent.ShowDeleteAllDialog -> showDeleteAllDialog()
            MainEvent.HideDeleteAllDialog -> hideDeleteAllDialog()
            MainEvent.ConfirmDeleteAll -> confirmDeleteAll()
            MainEvent.ToggleSearch -> toggleSearch()
            MainEvent.CloseSearch -> closeSearch()
            MainEvent.PerformSearch -> performSearch()
            else -> Unit
        }
    }

    private fun copyList(list: ShoppingList, newName: String) {
        viewModelScope.launch {
            val modifiedList = list.copy(
                userId = userId
            )
            shoppingListItemInteractor.copyShoppingList(modifiedList, newName)
        }
    }

    private fun showEditShoppingListDialog(list: ShoppingList) {
        _stateList.update {
            it.copy(
                showEditShoppingListDialog = true,
                list = list
            )
        }
    }

    private fun hideEditShoppingListDialog() {
        _stateList.update {
            it.copy(
                showEditShoppingListDialog = false
            )
        }
    }

    private fun showDeleteListDialog(list: ShoppingList) {
        _stateList.update {
            it.copy(
                showDeleteListDialog = true,
                list = list
            )
        }
    }

    private fun hideDeleteListDialog() {
        _stateList.update {
            it.copy(
                showDeleteListDialog = false
            )
        }
    }

    private fun deleteList() {
        viewModelScope.launch {
            val id = _stateList.value.list?.id
            if (id != null) {
                shoppingListItemInteractor.deleteShoppingList(id)
            }
        }

        _stateList.update { it.copy(showDeleteListDialog = false) }
    }

    private fun saveNewListName(newName: String) {
        viewModelScope.launch {
            val id = _stateList.value.list?.id
            if (id != null) {
                shoppingListItemInteractor.renameShoppingListItem(id, newName)
            }
        }
        _stateList.update {
            it.copy(
                showEditShoppingListDialog = false
            )
        }
    }

    private fun performSearch() {
        _state.update { it.copy(searchQuery = it.searchInput) }
    }

    private fun showDeleteAllDialog() {
        _state.update { it.copy(showDeleteAllDialog = true) }
    }

    private fun hideDeleteAllDialog() {
        _state.update { it.copy(showDeleteAllDialog = false) }
    }

    private fun confirmDeleteAll() {
        viewModelScope.launch {
            try {
                val currentLists = _state.value.lists
                currentLists.forEach { list ->
                    listRepository.deleteShopperListById(list.id)
                }
                _state.update { it.copy(showDeleteAllDialog = false) }
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка при удалении: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния: ${e.message}") }
            }
        }
    }

    private fun toggleSearch() {
        _state.update {
            it.copy(isSearchActive = !it.isSearchActive, searchQuery = "")
        }
        _searchQueryInput.value = ""
    }

    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchInput = query) }
        _searchQueryInput.value = query
    }

    private fun closeSearch() {
        _state.update { it.copy(isSearchActive = false, searchQuery = "") }
        _searchQueryInput.value = ""
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

            listRepository.getAllShopperLists(userId)
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
                    createdAt = System.currentTimeMillis(),
                    userId = userId
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

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 2000L
    }
}
