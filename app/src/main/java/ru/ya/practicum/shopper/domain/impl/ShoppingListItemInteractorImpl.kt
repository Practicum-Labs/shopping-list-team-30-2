package ru.ya.practicum.shopper.domain.impl

import kotlinx.coroutines.flow.first
import ru.ya.practicum.shopper.domain.api.ShoppingListItemInteractor
import ru.ya.practicum.shopper.domain.model.ShopperList
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository

class ShoppingListItemInteractorImpl(
    private val shopperListRepository: ShopperListRepository,
    private val shopperItemsRepository: ShopperItemRepository
) :
    ShoppingListItemInteractor {
    override suspend fun renameShoppingListItem(id: Int, newName: String) {
        shopperListRepository.rename(id, newName)
    }

    override suspend fun copyShoppingList(originalList: ShopperList, newName: String) {
        // 1. Создаём новый список
        val newListId = shopperListRepository.addShopperList(
            ShopperList(
                id = 0,
                name = newName,
                iconId = originalList.iconId
            )
        ).toInt()

        // 2. Получаем все элементы оригинального списка и копируем
        shopperItemsRepository.getAllItems(originalList.id.toInt())
            .first().data
            ?.forEach { item ->
                shopperItemsRepository.addItem(
                    item.copy(
                        id = 0,
                        name = item.name,
                        isBought = item.isBought,
                        position = item.position,
                        unit = item.unit,
                        value = item.value
                    ),
                    newListId
                )
            }
    }

    override suspend fun deleteShoppingList(id: Int) {
        shopperListRepository.deleteShopperListById(id)
    }
}