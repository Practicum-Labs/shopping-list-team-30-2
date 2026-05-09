package ru.ya.practicum.shopper.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.ya.practicum.shopper.core.util.Resource
import ru.ya.practicum.shopper.domain.model.ShopperItem

interface ShopperItemRepository {
    suspend fun addItem(item: ShopperItem, listId: Int)
    suspend fun deleteItem(item: ShopperItem)
    suspend fun deleteItemById(id: Int)
    suspend fun updateItem(item: ShopperItem, listId: Int)
    fun getAllItems(listId: Int, orderByName: Boolean = false): Flow<Resource<List<ShopperItem>>>
    suspend fun deleteAllItemsByListId(listId: Int)
    suspend fun clearBoughtItems(listId: Int)
}
