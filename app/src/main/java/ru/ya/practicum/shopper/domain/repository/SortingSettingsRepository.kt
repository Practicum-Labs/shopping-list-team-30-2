package ru.ya.practicum.shopper.domain.repository

import kotlinx.coroutines.flow.Flow

interface SortingSettingsRepository {
    fun isProductsSortByName(): Flow<Boolean>
    suspend fun setProductsSortByName(byName: Boolean)
}
