package ru.ya.practicum.shopper.data.impl

import ru.ya.practicum.shopper.data.converter.ShopperListMapper
import ru.ya.practicum.shopper.data.local.dao.ShopperListsDao
import ru.ya.practicum.shopper.domain.model.ShopperList
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository

class ShopperListRepositoryImpl(
    private val dao: ShopperListsDao,
    private val mapper: ShopperListMapper
) : ShopperListRepository {

    override suspend fun addShopperList(shoppingList: ShopperList) {
        dao.insert(mapper.toEntity(shoppingList))
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
}
