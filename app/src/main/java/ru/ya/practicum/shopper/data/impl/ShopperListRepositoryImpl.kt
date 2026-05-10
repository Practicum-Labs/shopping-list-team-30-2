package ru.ya.practicum.shopper.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.ya.practicum.shopper.core.util.Resource
import ru.ya.practicum.shopper.data.converter.ShopperListMapper
import ru.ya.practicum.shopper.data.local.dao.ShopperListsDao
import ru.ya.practicum.shopper.domain.model.ShopperList
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository

class ShopperListRepositoryImpl(
    private val dao: ShopperListsDao,
    private val mapper: ShopperListMapper
) : ShopperListRepository {

    override suspend fun addShopperList(shoppingList: ShopperList): Long {
        return dao.insert(mapper.toEntity(shoppingList))
    }

    override suspend fun deleteShopperList(shoppingList: ShopperList) {
        dao.delete(mapper.toEntity(shoppingList))
    }

    override suspend fun deleteShopperListById(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun updateShopperList(shoppingList: ShopperList) {
        dao.update(mapper.toEntity(shoppingList))
    }

    override suspend fun getShopperListById(id: Int): ShopperList? {
        val entity = dao.getListById(id)
        return entity?.let { mapper.toDomain(it) }
    }

    override fun getAllShopperLists(userId: String): Flow<Resource<List<ShopperList>>> {
        return dao.getAllLists(userId)
            .map { entities ->
                Resource.Success(entities.map { mapper.toDomain(it) })
            }
    }

    override suspend fun rename(id: Int, newName: String) {
        dao.rename(id, newName)
    }
}
