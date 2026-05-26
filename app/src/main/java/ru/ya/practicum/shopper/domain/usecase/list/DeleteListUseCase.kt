package ru.ya.practicum.shopper.domain.usecase.list

import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class DeleteListParams(val listId: Int)

class DeleteListUseCase(
    private val repository: ShopperListRepository
) : UseCase<DeleteListParams, Unit>() {
    override suspend operator fun invoke(params: DeleteListParams) {
        require(params.listId > 0) { "Invalid list ID" }
        repository.deleteShopperListById(params.listId)
    }
}
