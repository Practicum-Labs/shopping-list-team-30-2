package ru.ya.practicum.shopper.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.ya.practicum.shopper.core.util.Resource
import ru.ya.practicum.shopper.domain.model.ShopperItem

interface ShopperItemRepository {
    suspend fun addItem(item: ShopperItem, listId: Int)
    suspend fun deleteItem(item: ShopperItem)
    suspend fun deleteItemById(id: Int)
    suspend fun updateItem(item: ShopperItem, listId: Int)
    suspend fun insertItems(items: List<ShopperItem>, listId: Int)
    fun getAllItems(listId: Int): Flow<Resource<List<ShopperItem>>>
}
