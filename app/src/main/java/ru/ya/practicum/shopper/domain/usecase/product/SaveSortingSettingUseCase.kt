package ru.ya.practicum.shopper.domain.usecase.product

import ru.ya.practicum.shopper.domain.usecase.UseCase
import ru.ya.practicum.shopper.feature.product.ProductDataStore

data class SaveSortingSettingParams(
    val sortByName: Boolean
)

class SaveSortingSettingUseCase(
    private val dataStore: ProductDataStore
) : UseCase<SaveSortingSettingParams, Unit>() {

    override suspend operator fun invoke(params: SaveSortingSettingParams) {
        dataStore.setProductsSortByName(params.sortByName)
    }
}
