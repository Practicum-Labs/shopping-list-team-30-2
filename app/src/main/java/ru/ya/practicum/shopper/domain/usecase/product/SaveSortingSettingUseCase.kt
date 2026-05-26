package ru.ya.practicum.shopper.domain.usecase.product

import ru.ya.practicum.shopper.domain.repository.SortingSettingsRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class SaveSortingSettingParams(
    val sortByName: Boolean
)

class SaveSortingSettingUseCase(
    private val repository: SortingSettingsRepository
) : UseCase<SaveSortingSettingParams, Unit>() {
    override suspend operator fun invoke(params: SaveSortingSettingParams) {
        repository.setProductsSortByName(params.sortByName)
    }
}
