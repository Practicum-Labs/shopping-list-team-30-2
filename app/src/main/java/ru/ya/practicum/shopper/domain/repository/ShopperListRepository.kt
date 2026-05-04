package ru.ya.practicum.shopper.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.ya.practicum.shopper.core.util.Resource
import ru.ya.practicum.shopper.domain.model.ShopperList

interface ShopperListRepository {
    suspend fun addShopperList(shoppingList: ShopperList): Long
    suspend fun deleteShopperList(shoppingList: ShopperList)
    suspend fun deleteShopperListById(id: Int)
    suspend fun updateShopperList(shoppingList: ShopperList)
    suspend fun getShopperListById(id: Int): ShopperList?
    fun getAllShopperLists(): Flow<Resource<List<ShopperList>>>
}
