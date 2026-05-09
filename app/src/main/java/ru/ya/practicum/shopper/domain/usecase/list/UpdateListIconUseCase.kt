package ru.ya.practicum.shopper.domain.usecase.list

import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class UpdateListIconParams(
    val listId: Int,
    val newIconId: Int
)

class UpdateListIconUseCase(
    private val repository: ShopperListRepository
) : UseCase<UpdateListIconParams, Unit>() {
    override suspend operator fun invoke(params: UpdateListIconParams) {
        require(params.listId > 0) { "Invalid list ID" }

        val existingList = repository.getShopperListById(params.listId)
        require(existingList != null) { "List not found" }

        val updatedList = existingList.copy(iconId = params.newIconId)
        repository.updateShopperList(updatedList)
    }
}
