package ru.ya.practicum.shopper.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.ya.practicum.shopper.core.util.Resource
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

    override fun getAllItems(listId: Int): Flow<Resource<List<ShopperItem>>> = flow {
        dao.getItems(listId)
            .map { entities ->
                Resource.Success(entities.map { mapper.toDomain(it) })
            }
            .collect { emit(it) }

    }
}
