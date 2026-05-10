// ✅ НОВЫЙ КОД
package ru.ya.practicum.shopper.domain.usecase.product

import kotlinx.coroutines.flow.Flow
import ru.ya.practicum.shopper.domain.repository.SortingSettingsRepository
import ru.ya.practicum.shopper.domain.usecase.UseCase

class GetSortingSettingUseCase(
    private val repository: SortingSettingsRepository
) : UseCase<Unit, Flow<Boolean>>() {
    override suspend operator fun invoke(params: Unit): Flow<Boolean> {
        return repository.isProductsSortByName()
    }
}
