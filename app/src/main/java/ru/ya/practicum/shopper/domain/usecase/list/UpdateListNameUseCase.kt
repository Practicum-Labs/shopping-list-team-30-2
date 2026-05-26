package ru.ya.practicum.shopper.domain.usecase.list

import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class UpdateListNameParams(
    val listId: Int,
    val newName: String
)

class UpdateListNameUseCase(
    private val repository: ShopperListRepository
) : UseCase<UpdateListNameParams, Unit>() {
    override suspend operator fun invoke(params: UpdateListNameParams) {
        require(params.listId > 0) { "Invalid list ID" }
        require(params.newName.isNotBlank()) { "List name cannot be blank" }

        val existingList = repository.getShopperListById(params.listId)
        require(existingList != null) { "List not found" }

        val updatedList = existingList.copy(name = params.newName)
        repository.updateShopperList(updatedList)
    }
}
