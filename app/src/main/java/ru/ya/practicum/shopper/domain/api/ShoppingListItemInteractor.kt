package ru.ya.practicum.shopper.domain.api

import ru.ya.practicum.shopper.core.model.ShoppingList

interface ShoppingListItemInteractor {
    suspend fun renameShoppingListItem(id: Int, newName: String)
    suspend fun copyShoppingList(originalList: ShoppingList, newName: String)
    suspend fun deleteShoppingList(id: Int)
}
