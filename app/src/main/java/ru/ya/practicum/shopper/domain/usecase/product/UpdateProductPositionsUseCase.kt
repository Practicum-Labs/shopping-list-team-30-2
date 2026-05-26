package ru.ya.practicum.shopper.domain.usecase.product

import ru.ya.practicum.shopper.core.model.Product
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

class UpdateProductPositionsUseCase(
    private val repository: ShopperItemRepository
) : UseCase<List<Product>, Unit>() {
    override suspend fun invoke(params: List<Product>) {
        repository.updateProductPositions(params)
    }
}
