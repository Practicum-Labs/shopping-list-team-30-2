package ru.ya.practicum.shopper.domain.impl

import kotlinx.coroutines.flow.firstOrNull
import ru.ya.practicum.shopper.core.model.ShoppingList
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

    override suspend fun copyShoppingList(
        originalList: ShoppingList,
        newName: String
    ) {
        val newListId = shopperListRepository.addShopperList(
            ShopperList(
                id = 0,
                name = newName,
                iconId = originalList.iconResId,
                userId = originalList.userId
            )
        ).toInt()

        val items = shopperItemsRepository.getAllItems(originalList.id)
            .firstOrNull()?.data
            ?: return

        shopperItemsRepository.insertItems(
            items.map { it.copy(id = 0) },
            newListId
        )
    }

    override suspend fun deleteShoppingList(id: Int) {
        shopperListRepository.deleteShopperListById(id)
    }
}
