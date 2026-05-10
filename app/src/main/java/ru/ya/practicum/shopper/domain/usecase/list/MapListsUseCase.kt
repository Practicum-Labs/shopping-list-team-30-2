package ru.ya.practicum.shopper.domain.usecase.list

import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.domain.model.ShopperList
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class MapListsParams(
    val lists: List<ShopperList>?
)

class MapListsUseCase : UseCase<MapListsParams, List<ShoppingList>>() {
    override suspend operator fun invoke(params: MapListsParams): List<ShoppingList> {
        return params.lists?.map { list ->
            ShoppingList(
                id = list.id.toInt(),
                name = list.name,
                iconResId = list.iconId
            )
        } ?: emptyList()
    }
}
