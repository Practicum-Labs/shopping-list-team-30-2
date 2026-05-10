package ru.ya.practicum.shopper.domain.usecase.product

import kotlinx.coroutines.flow.Flow
import ru.ya.practicum.shopper.domain.model.ShopperItem
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class GetProductsParams(
    val listId: Int,
    val orderByName: Boolean = false
)

class GetProductsUseCase(
    private val repository: ShopperItemRepository
) : UseCase<GetProductsParams, Flow<List<ShopperItem>>>() {
    override suspend operator fun invoke(params: GetProductsParams): Flow<List<ShopperItem>> {
        require(params.listId > 0) { "Invalid list ID" }
        return repository.getAllItems(params.listId, params.orderByName)
    }
}
