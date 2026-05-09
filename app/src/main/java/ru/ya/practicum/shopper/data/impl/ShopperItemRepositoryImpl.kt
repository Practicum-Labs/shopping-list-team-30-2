package ru.ya.practicum.shopper.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.ya.practicum.shopper.data.converter.ShopperItemMapper
import ru.ya.practicum.shopper.data.local.dao.ShopperItemDao
import ru.ya.practicum.shopper.domain.model.ShopperItem
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository

class ShopperItemRepositoryImpl(
    private val dao: ShopperItemDao,
    private val mapper: ShopperItemMapper
) : ShopperItemRepository {

    override suspend fun addItem(item: ShopperItem, listId: Int) {
        dao.insert(mapper.toEntity(item, listId))
    }

    override suspend fun deleteItem(item: ShopperItem) {
        dao.delete(mapper.toEntity(item, item.id))
    }

    override suspend fun deleteItemById(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun updateItem(item: ShopperItem, listId: Int) {
        dao.update(mapper.toEntity(item, listId))
    }

    override fun getAllItems(listId: Int, orderByName: Boolean): Flow<List<ShopperItem>> = flow {
        val queryResult =
            if (orderByName) dao.getItemsOrderedByName(listId) else dao.getItems(listId)

        queryResult.collect { entities ->
            emit(entities.map { mapper.toDomain(it) })
        }
    }

    override suspend fun deleteAllItemsByListId(listId: Int) {
        dao.deleteAllByListId(listId)
    }

    override suspend fun clearBoughtItems(listId: Int) {
        dao.clearBought(listId)
    }
}
