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
import org.koin.java.KoinJavaComponent.inject
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.core.util.Resource
import ru.ya.practicum.shopper.domain.usecase.list.CreateListParams
import ru.ya.practicum.shopper.domain.usecase.list.CreateListUseCase
import ru.ya.practicum.shopper.domain.usecase.list.DeleteAllListsParams
import ru.ya.practicum.shopper.domain.usecase.list.DeleteAllListsUseCase
import ru.ya.practicum.shopper.domain.usecase.list.DeleteListParams
import ru.ya.practicum.shopper.domain.usecase.list.DeleteListUseCase
import ru.ya.practicum.shopper.domain.usecase.list.GetListsParams
import ru.ya.practicum.shopper.domain.usecase.list.GetListsUseCase
import ru.ya.practicum.shopper.domain.usecase.list.MapListsParams
import ru.ya.practicum.shopper.domain.usecase.list.MapListsUseCase
import ru.ya.practicum.shopper.domain.usecase.list.UpdateListIconParams
import ru.ya.practicum.shopper.domain.usecase.list.UpdateListIconUseCase
import ru.ya.practicum.shopper.domain.usecase.list.UpdateListNameParams
import ru.ya.practicum.shopper.domain.usecase.list.UpdateListNameUseCase
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

@Suppress("TooManyFunctions")
class MainViewModel(
    private val userId: String
) : ViewModel() {

    private val getListsUseCase: GetListsUseCase by inject(GetListsUseCase::class.java)
    private val createListUseCase: CreateListUseCase by inject(CreateListUseCase::class.java)
    private val deleteListUseCase: DeleteListUseCase by inject(DeleteListUseCase::class.java)
    private val updateListNameUseCase: UpdateListNameUseCase by inject(UpdateListNameUseCase::class.java)
    private val updateListIconUseCase: UpdateListIconUseCase by inject(UpdateListIconUseCase::class.java)
    private val deleteAllListsUseCase: DeleteAllListsUseCase by inject(DeleteAllListsUseCase::class.java)
    private val mapListsUseCase: MapListsUseCase by inject(MapListsUseCase::class.java)

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()
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
                val listIds = _state.value.lists.map { it.id }
                deleteAllListsUseCase(DeleteAllListsParams(listIds))
                _state.update { it.copy(showDeleteAllDialog = false) }
                loadLists()
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
                updateListIconUseCase(UpdateListIconParams(listId, newIconId))
                _state.update { it.copy(showIconPicker = false, editingListId = null) }
                loadLists()
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

            getListsUseCase(GetListsParams(userId))
                .catch { e -> handleLoadError(e) }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val uiLists = mapListsUseCase(
                                MapListsParams(resource.data)
                            )
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

    private fun handleLoadError(e: Throwable) {
        _state.update {
            it.copy(
                isLoading = false,
                error = "Ошибка загрузки списков: ${e.message}"
            )
        }
    }

    private fun createList(name: String, iconId: Int) {
        viewModelScope.launch {
            try {
                if (name.isBlank()) {
                    _state.update { it.copy(error = "Название не может быть пустым") }
                    return@launch
                }
                createListUseCase(CreateListParams(name, iconId, userId))
                loadLists()
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
                deleteListUseCase(DeleteListParams(listId))
                loadLists()
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
                updateListNameUseCase(UpdateListNameParams(listId, newName))
                loadLists()
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

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 500L
    }
}
