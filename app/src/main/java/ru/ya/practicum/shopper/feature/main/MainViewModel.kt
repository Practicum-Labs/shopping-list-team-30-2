package ru.ya.practicum.shopper.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.domain.api.ShoppingListItemInteractor
import ru.ya.practicum.shopper.domain.usecase.list.CreateListParams
import ru.ya.practicum.shopper.domain.usecase.list.DeleteAllListsParams
import ru.ya.practicum.shopper.domain.usecase.list.DeleteListParams
import ru.ya.practicum.shopper.domain.usecase.list.GetListsParams
import ru.ya.practicum.shopper.domain.usecase.list.MapListsParams
import ru.ya.practicum.shopper.domain.usecase.list.UpdateListIconParams
import ru.ya.practicum.shopper.domain.usecase.list.UpdateListNameParams
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

sealed class MainIntent {
    data class CreateList(val name: String, val iconId: Int) : MainIntent()
    data class DeleteList(val listId: Int) : MainIntent()
    data class UpdateListName(val listId: Int, val newName: String) : MainIntent()
    data class UpdateListIcon(val listId: Int, val newIconId: Int) : MainIntent()
    data class ShowIconPickerForList(val listId: Int) : MainIntent()
    data class UpdateSearchQuery(val query: String) : MainIntent()
    data class SelectIcon(val iconId: Int) : MainIntent()
    data class UpdateNewListName(val name: String) : MainIntent()
    data object ShowAddDialog : MainIntent()
    data object HideAddDialog : MainIntent()
    data object ShowIconPicker : MainIntent()
    data object HideIconPicker : MainIntent()
    data object LoadLists : MainIntent()
    data object ShowDeleteAllDialog : MainIntent()
    data object HideDeleteAllDialog : MainIntent()
    data object ConfirmDeleteAll : MainIntent()
    data object ToggleSearch : MainIntent()
    data object CloseSearch : MainIntent()
    data object PerformSearch : MainIntent()
}

sealed class MainResult {
    data class ListsLoaded(val lists: List<ShoppingList>) : MainResult()
    data class ListCreated(val lists: List<ShoppingList>) : MainResult()
    data class ListDeleted(val lists: List<ShoppingList>) : MainResult()
    data class ListNameUpdated(val lists: List<ShoppingList>) : MainResult()
    data class ListIconUpdated(val lists: List<ShoppingList>) : MainResult()
    data class ListsDeleted(val lists: List<ShoppingList>) : MainResult()
    data class Error(val message: String) : MainResult()
}

sealed class MainEffect {
    data class ShowError(val message: String) : MainEffect()
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

@Suppress("TooManyFunctions", "LargeClass")
class MainViewModel(
    private val userId: String,
    private val useCases: MainUseCases,
    private val shoppingListItemInteractor: ShoppingListItemInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _stateList = MutableStateFlow(ListState())
    val stateList: StateFlow<ListState> = _stateList.asStateFlow()

    private val _effect = Channel<MainEffect>()
    val effect: Flow<MainEffect> = _effect.receiveAsFlow()

    private val actions = MutableSharedFlow<MainIntent>()
    private val _searchQueryInput = MutableStateFlow("")

    init {
        setupSearchDebounce()
        processActions()
        onIntent(MainIntent.LoadLists)
    }

    fun onIntent(intent: MainIntent) {
        viewModelScope.launch {
            actions.emit(intent)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun processActions() {
        viewModelScope.launch {
            actions
                .onEach { _state.update { it.copy(isLoading = true, error = null) } }
                .flatMapConcat { intent -> toResult(intent) }
                .collect { result -> reduce(result) }
        }
    }

    private fun copyList(list: ShoppingList, newName: String) {
        viewModelScope.launch {
            val coreShoppingList = ru.ya.practicum.shopper.core.model.ShoppingList(
                id = list.id,
                name = list.name,
                iconResId = list.iconResId,
                userId = userId
            )
            shoppingListItemInteractor.copyShoppingList(coreShoppingList, newName)
            loadListsAfterAction()
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
            it.copy(showEditShoppingListDialog = false)
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
            it.copy(showDeleteListDialog = false)
        }
    }

    private fun deleteList() {
        viewModelScope.launch {
            val id = _stateList.value.list?.id
            if (id != null) {
                shoppingListItemInteractor.deleteShoppingList(id)
                loadListsAfterAction()
            }
        }
        _stateList.update { it.copy(showDeleteListDialog = false) }
    }

    private fun saveNewListName(newName: String) {
        viewModelScope.launch {
            val id = _stateList.value.list?.id
            if (id != null) {
                shoppingListItemInteractor.renameShoppingListItem(id, newName)
                loadListsAfterAction()
            }
        }
        _stateList.update { it.copy(showEditShoppingListDialog = false) }
    }

    private suspend fun loadListsAfterAction() {
        val shopperLists = useCases.getLists(GetListsParams(userId)).first()
        val uiLists = useCases.mapLists(MapListsParams(shopperLists))
        _state.update { it.copy(lists = uiLists) }
    }

    @Suppress("CyclomaticComplexMethod")
    private fun toResult(intent: MainIntent): Flow<MainResult> = flow {
        val result = when (intent) {
            is MainIntent.LoadLists -> loadLists()
            is MainIntent.CreateList -> createList(intent.name, intent.iconId)
            is MainIntent.DeleteList -> deleteList(intent.listId)
            is MainIntent.UpdateListName -> updateListName(intent.listId, intent.newName)
            is MainIntent.UpdateListIcon -> updateListIcon(intent.listId, intent.newIconId)
            is MainIntent.ConfirmDeleteAll -> deleteAllLists()
            is MainIntent.ShowAddDialog -> handleShowAddDialog()
            is MainIntent.HideAddDialog -> handleHideAddDialog()
            is MainIntent.ShowIconPicker -> handleShowIconPicker()
            is MainIntent.HideIconPicker -> handleHideIconPicker()
            is MainIntent.SelectIcon -> handleSelectIcon(intent.iconId)
            is MainIntent.UpdateNewListName -> handleUpdateNewListName(intent.name)
            is MainIntent.ShowIconPickerForList -> handleShowIconPickerForList(intent.listId)
            is MainIntent.ToggleSearch -> handleToggleSearch()
            is MainIntent.UpdateSearchQuery -> handleUpdateSearchQuery(intent.query)
            is MainIntent.CloseSearch -> handleCloseSearch()
            is MainIntent.PerformSearch -> handlePerformSearch()
            is MainIntent.ShowDeleteAllDialog -> handleShowDeleteAllDialog()
            is MainIntent.HideDeleteAllDialog -> handleHideDeleteAllDialog()
        }
        emit(result)
    }

    private suspend fun reduce(result: MainResult) {
        when (result) {
            is MainResult.ListsLoaded -> reduceListsLoaded(result)
            is MainResult.ListCreated -> reduceListCreated(result)
            is MainResult.ListDeleted -> reduceListDeleted(result)
            is MainResult.ListNameUpdated -> reduceListNameUpdated(result)
            is MainResult.ListIconUpdated -> reduceListIconUpdated(result)
            is MainResult.ListsDeleted -> reduceListsDeleted(result)
            is MainResult.Error -> reduceError(result)
        }
    }

    private suspend fun loadLists(): MainResult {
        return try {
            val shopperLists = useCases.getLists(GetListsParams(userId)).first()
            val uiLists = useCases.mapLists(MapListsParams(shopperLists))
            MainResult.ListsLoaded(uiLists)
        } catch (e: IOException) {
            MainResult.Error("Ошибка сети: ${e.message}")
        } catch (e: IllegalStateException) {
            MainResult.Error("Ошибка состояния: ${e.message}")
        }
    }

    private suspend fun createList(name: String, iconId: Int): MainResult {
        return try {
            if (name.isBlank()) {
                return MainResult.Error("Название не может быть пустым")
            }
            useCases.createList(CreateListParams(name, iconId, userId))
            val shopperLists = useCases.getLists(GetListsParams(userId)).first()
            val uiLists = useCases.mapLists(MapListsParams(shopperLists))
            MainResult.ListCreated(uiLists)
        } catch (e: SQLException) {
            MainResult.Error("Ошибка базы данных: ${e.message}")
        } catch (e: IOException) {
            MainResult.Error("Ошибка ввода-вывода: ${e.message}")
        } catch (e: IllegalStateException) {
            MainResult.Error("Ошибка состояния: ${e.message}")
        }
    }

    private suspend fun deleteList(listId: Int): MainResult {
        return try {
            useCases.deleteList(DeleteListParams(listId))
            val shopperLists = useCases.getLists(GetListsParams(userId)).first()
            val uiLists = useCases.mapLists(MapListsParams(shopperLists))
            MainResult.ListDeleted(uiLists)
        } catch (e: SQLException) {
            MainResult.Error("Ошибка базы данных при удалении: ${e.message}")
        } catch (e: IOException) {
            MainResult.Error("Ошибка ввода-вывода при удалении: ${e.message}")
        } catch (e: IllegalStateException) {
            MainResult.Error("Ошибка состояния при удалении: ${e.message}")
        }
    }

    private suspend fun updateListName(listId: Int, newName: String): MainResult {
        return try {
            useCases.updateListName(UpdateListNameParams(listId, newName))
            val shopperLists = useCases.getLists(GetListsParams(userId)).first()
            val uiLists = useCases.mapLists(MapListsParams(shopperLists))
            MainResult.ListNameUpdated(uiLists)
        } catch (e: SQLException) {
            MainResult.Error("Ошибка базы данных при обновлении: ${e.message}")
        } catch (e: IOException) {
            MainResult.Error("Ошибка ввода-вывода при обновлении: ${e.message}")
        } catch (e: IllegalStateException) {
            MainResult.Error("Ошибка состояния при обновлении: ${e.message}")
        }
    }

    private suspend fun updateListIcon(listId: Int, newIconId: Int): MainResult {
        return try {
            useCases.updateListIcon(UpdateListIconParams(listId, newIconId))
            val shopperLists = useCases.getLists(GetListsParams(userId)).first()
            val uiLists = useCases.mapLists(MapListsParams(shopperLists))
            MainResult.ListIconUpdated(uiLists)
        } catch (e: SQLException) {
            MainResult.Error("Ошибка базы данных: ${e.message}")
        } catch (e: IOException) {
            MainResult.Error("Ошибка ввода-вывода: ${e.message}")
        } catch (e: IllegalStateException) {
            MainResult.Error("Ошибка состояния: ${e.message}")
        }
    }

    private suspend fun deleteAllLists(): MainResult {
        return try {
            val listIds = _state.value.lists.map { it.id }
            useCases.deleteAllLists(DeleteAllListsParams(listIds))
            val shopperLists = useCases.getLists(GetListsParams(userId)).first()
            val uiLists = useCases.mapLists(MapListsParams(shopperLists))
            MainResult.ListsDeleted(uiLists)
        } catch (e: SQLException) {
            MainResult.Error("Ошибка при удалении: ${e.message}")
        } catch (e: IOException) {
            MainResult.Error("Ошибка ввода-вывода: ${e.message}")
        } catch (e: IllegalStateException) {
            MainResult.Error("Ошибка состояния: ${e.message}")
        }
    }

    private suspend fun handleShowAddDialog(): MainResult {
        _state.update { it.copy(showAddDialog = true, showIconPicker = false, error = null) }
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleHideAddDialog(): MainResult {
        _state.update {
            it.copy(
                showAddDialog = false,
                showIconPicker = false,
                newListName = "",
                selectedIconId = 0
            )
        }
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleShowIconPicker(): MainResult {
        _state.update { it.copy(showIconPicker = true) }
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleHideIconPicker(): MainResult {
        _state.update { it.copy(showIconPicker = false, editingListId = null) }
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleSelectIcon(iconId: Int): MainResult {
        _state.update { it.copy(selectedIconId = iconId, showIconPicker = false) }
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleUpdateNewListName(name: String): MainResult {
        _state.update { it.copy(newListName = name) }
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleShowIconPickerForList(listId: Int): MainResult {
        _state.update { it.copy(showIconPicker = true, editingListId = listId) }
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleToggleSearch(): MainResult {
        _state.update {
            it.copy(isSearchActive = !it.isSearchActive, searchQuery = "")
        }
        _searchQueryInput.value = ""
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleUpdateSearchQuery(query: String): MainResult {
        _state.update { it.copy(searchInput = query) }
        _searchQueryInput.value = query
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleCloseSearch(): MainResult {
        _state.update { it.copy(isSearchActive = false, searchQuery = "") }
        _searchQueryInput.value = ""
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handlePerformSearch(): MainResult {
        _state.update { it.copy(searchQuery = it.searchInput) }
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleShowDeleteAllDialog(): MainResult {
        _state.update { it.copy(showDeleteAllDialog = true) }
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun handleHideDeleteAllDialog(): MainResult {
        _state.update { it.copy(showDeleteAllDialog = false) }
        return MainResult.ListsLoaded(_state.value.lists)
    }

    private suspend fun reduceListsLoaded(result: MainResult.ListsLoaded) {
        _state.update {
            it.copy(
                isLoading = false,
                lists = result.lists,
                error = null
            )
        }
    }

    private suspend fun reduceListCreated(result: MainResult.ListCreated) {
        _state.update {
            it.copy(
                isLoading = false,
                lists = result.lists,
                error = null,
                showAddDialog = false,
                showIconPicker = false,
                newListName = "",
                selectedIconId = 0
            )
        }
    }

    private suspend fun reduceListDeleted(result: MainResult.ListDeleted) {
        _state.update {
            it.copy(
                isLoading = false,
                lists = result.lists,
                error = null
            )
        }
    }

    private suspend fun reduceListNameUpdated(result: MainResult.ListNameUpdated) {
        _state.update {
            it.copy(
                isLoading = false,
                lists = result.lists,
                error = null
            )
        }
    }

    private suspend fun reduceListIconUpdated(result: MainResult.ListIconUpdated) {
        _state.update {
            it.copy(
                isLoading = false,
                lists = result.lists,
                error = null,
                showIconPicker = false,
                editingListId = null
            )
        }
    }

    private suspend fun reduceListsDeleted(result: MainResult.ListsDeleted) {
        _state.update {
            it.copy(
                isLoading = false,
                lists = result.lists,
                error = null,
                showDeleteAllDialog = false
            )
        }
    }

    private suspend fun reduceError(result: MainResult.Error) {
        _effect.send(MainEffect.ShowError(result.message))
        _state.update {
            it.copy(
                isLoading = false,
                error = result.message
            )
        }
    }

    companion object {
        const val SEARCH_DEBOUNCE_MS = 500L
    }
}
