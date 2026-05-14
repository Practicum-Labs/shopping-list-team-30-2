package ru.ya.practicum.shopper.domain.usecase.product

import ru.ya.practicum.shopper.core.model.Product
import ru.ya.practicum.shopper.domain.model.ShopperItem
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class ChangeProductParams(
    val id: Int = 0,
    val name: String,
    val unit: String?,
    val value: Float?,
    val listId: Int,
    val position: Int = 0,
    val isBought: Boolean = false
)

class UpdateProductUseCase(
    private val repository: ShopperItemRepository
) : UseCase<ChangeProductParams, Unit>() {
    override suspend fun invoke(params: ChangeProductParams) {
        require(params.name.isNotBlank()) { "Product name cannot be blank" }
        require(params.listId > 0) { "Invalid list ID" }

        val item = ShopperItem(
            id = params.id,
            name = params.name,
            unit = params.unit,
            value = params.value,
            isBought = params.isBought,
            position = params.position
        )
        repository.updateItem(item, params.listId)
    }
}