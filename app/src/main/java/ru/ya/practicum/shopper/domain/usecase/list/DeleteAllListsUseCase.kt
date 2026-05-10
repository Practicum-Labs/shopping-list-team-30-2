package ru.ya.practicum.shopper.domain.usecase.list

import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class DeleteAllListsParams(val listIds: List<Int>)

class DeleteAllListsUseCase(
    private val repository: ShopperListRepository
) : UseCase<DeleteAllListsParams, Unit>() {
    override suspend operator fun invoke(params: DeleteAllListsParams) {
        params.listIds.forEach { listId ->
            repository.deleteShopperListById(listId)
        }
    }
}
