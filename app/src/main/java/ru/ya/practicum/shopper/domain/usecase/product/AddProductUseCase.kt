package ru.ya.practicum.shopper.domain.usecase.product

import ru.ya.practicum.shopper.domain.model.ShopperItem
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class AddProductParams(
    val name: String,
    val unit: String?,
    val value: Float?,
    val listId: Int,
    val position: Int = 0
)

class AddProductUseCase(
    private val repository: ShopperItemRepository
) : UseCase<AddProductParams, Long>() {

    override suspend operator fun invoke(params: AddProductParams): Long {
        require(params.name.isNotBlank()) { "Product name cannot be blank" }
        require(params.listId > 0) { "Invalid list ID" }

        val item = ShopperItem(
            name = params.name,
            unit = params.unit,
            value = params.value,
            isBought = false,
            position = params.position
        )

        repository.addItem(item, params.listId)
        return item.id.toLong()
    }
}
