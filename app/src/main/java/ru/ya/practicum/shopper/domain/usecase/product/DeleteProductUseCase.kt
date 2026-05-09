package ru.ya.practicum.shopper.domain.usecase.product

import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class DeleteProductParams(
    val productId: Int
)

class DeleteProductUseCase(
    private val repository: ShopperItemRepository
) : UseCase<DeleteProductParams, Unit>() {

    override suspend operator fun invoke(params: DeleteProductParams) {
        require(params.productId > 0) { "Invalid product ID" }
        repository.deleteItemById(params.productId)
    }
}
