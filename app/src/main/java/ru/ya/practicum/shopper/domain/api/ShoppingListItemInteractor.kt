package ru.ya.practicum.shopper.domain.api

import ru.ya.practicum.shopper.domain.model.ShopperList

interface ShoppingListItemInteractor {
    suspend fun renameShoppingListItem(id: Int, newName: String)
    suspend fun copyShoppingList(originalList: ShopperList, newName: String)
    suspend fun deleteShoppingList(id: Int)
}