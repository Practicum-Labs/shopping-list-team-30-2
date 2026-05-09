package ru.ya.practicum.shopper.domain.usecase.product

import ru.ya.practicum.shopper.domain.model.ShopperItem
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class ToggleProductBoughtParams(
    val productId: Int,
    val listId: Int,
    val productName: String,
    val productUnit: String?,
    val productValue: Float?,
    val currentIsBought: Boolean
)

class ToggleProductBoughtUseCase(
    private val repository: ShopperItemRepository
) : UseCase<ToggleProductBoughtParams, Unit>() {

    override suspend operator fun invoke(params: ToggleProductBoughtParams) {
        val updatedItem = ShopperItem(
            id = params.productId,
            name = params.productName,
            unit = params.productUnit,
            value = params.productValue,
            isBought = !params.currentIsBought,
            position = 0
        )

        repository.updateItem(updatedItem, params.listId)
    }
}
