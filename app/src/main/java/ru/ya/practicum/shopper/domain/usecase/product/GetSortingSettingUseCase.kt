package ru.ya.practicum.shopper.domain.usecase.product

import kotlinx.coroutines.flow.Flow
import ru.ya.practicum.shopper.domain.usecase.UseCase
import ru.ya.practicum.shopper.feature.product.ProductDataStore

class GetSortingSettingUseCase(
    private val dataStore: ProductDataStore
) : UseCase<Unit, Flow<Boolean>>() {

    override suspend operator fun invoke(params: Unit): Flow<Boolean> {
        return dataStore.isProductsSortByName
    }
}
