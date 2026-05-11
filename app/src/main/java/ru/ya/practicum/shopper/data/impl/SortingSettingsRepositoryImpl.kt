package ru.ya.practicum.shopper.data.impl

import kotlinx.coroutines.flow.Flow
import ru.ya.practicum.shopper.domain.repository.SortingSettingsRepository
import ru.ya.practicum.shopper.feature.product.ProductDataStore

class SortingSettingsRepositoryImpl(
    private val dataStore: ProductDataStore
) : SortingSettingsRepository {

    override fun isProductsSortByName(): Flow<Boolean> = dataStore.isProductsSortByName

    override suspend fun setProductsSortByName(byName: Boolean) {
        dataStore.setProductsSortByName(byName)
    }
}
