package ru.ya.practicum.shopper.domain.usecase.product

import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class DeleteAllProductsParams(
    val listId: Int,
)

class DeleteAllProductsUseCase(
    private val repository: ShopperItemRepository
) : UseCase<DeleteAllProductsParams, Unit>() {

    override suspend operator fun invoke(params: DeleteAllProductsParams) {
        require(params.listId > 0) { "Invalid list ID" }

        repository.deleteAllItemsByListId(params.listId)
    }
}
