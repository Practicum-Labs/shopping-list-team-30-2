package ru.ya.practicum.shopper.feature.main

import ru.ya.practicum.shopper.core.model.ShoppingList

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
