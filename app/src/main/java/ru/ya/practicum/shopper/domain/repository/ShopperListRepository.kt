package ru.ya.practicum.shopper.domain.repository

import ru.ya.practicum.shopper.domain.model.ShopperList

interface ShopperListRepository {
    suspend fun addShopperList(shoppingList: ShopperList)

    suspend fun deleteShopperList(shoppingList: ShopperList)

    suspend fun deleteShopperListById(id: Int)

    suspend fun updateShopperList(shoppingList: ShopperList)
}
