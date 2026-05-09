package ru.ya.practicum.shopper.domain.usecase.product

import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class ClearBoughtProductsParams(
    val listId: Int,
)

class ClearBoughtProductsUseCase(
    private val repository: ShopperItemRepository
) : UseCase<ClearBoughtProductsParams, Unit>() {

    override suspend operator fun invoke(params: ClearBoughtProductsParams) {
        require(params.listId > 0) { "Invalid list ID" }

        repository.clearBoughtItems(params.listId)
    }
}
