package ru.ya.practicum.shopper.domain.usecase.list

import kotlinx.coroutines.flow.Flow
import ru.ya.practicum.shopper.core.util.Resource
import ru.ya.practicum.shopper.domain.model.ShopperList
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class GetListsParams(val userId: String)

class GetListsUseCase(
    private val repository: ShopperListRepository
) : UseCase<GetListsParams, Flow<Resource<List<ShopperList>>>>() {
    override suspend operator fun invoke(params: GetListsParams): Flow<Resource<List<ShopperList>>> {
        require(params.userId.isNotBlank()) { "User ID cannot be blank" }
        return repository.getAllShopperLists(params.userId)
    }
}
