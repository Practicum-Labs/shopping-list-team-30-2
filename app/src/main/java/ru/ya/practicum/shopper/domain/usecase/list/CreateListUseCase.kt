package ru.ya.practicum.shopper.domain.usecase.list

import ru.ya.practicum.shopper.domain.model.ShopperList
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class CreateListParams(
    val name: String,
    val iconId: Int,
    val userId: String
)

class CreateListUseCase(
    private val repository: ShopperListRepository
) : UseCase<CreateListParams, Long>() {
    override suspend operator fun invoke(params: CreateListParams): Long {
        require(params.name.isNotBlank()) { "List name cannot be blank" }
        require(params.userId.isNotBlank()) { "User ID cannot be blank" }

        val list = ShopperList(
            id = 0,
            name = params.name,
            iconId = params.iconId,
            createdAt = System.currentTimeMillis(),
            userId = params.userId
        )
        return repository.addShopperList(list)
    }
}
