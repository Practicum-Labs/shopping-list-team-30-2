package ru.ya.practicum.shopper.domain.usecase.product

import ru.ya.practicum.shopper.core.model.Product
import ru.ya.practicum.shopper.domain.model.ShopperItem
import ru.ya.practicum.shopper.domain.usecase.UseCase

data class MapProductsParams(
    val items: List<ShopperItem>?,
    val defaultUnit: String,
    val defaultQuantity: String
)

class MapProductsUseCase : UseCase<MapProductsParams, List<Product>>() {

    override suspend operator fun invoke(params: MapProductsParams): List<Product> {
        return params.items?.map { item ->
            Product(
                id = item.id.toLong(),
                name = item.name,
                amount = formatValue(item.value, params.defaultQuantity),
                unit = item.unit ?: params.defaultUnit,
                isBought = item.isBought,
                position = item.position
            )
        } ?: emptyList()
    }

    private fun formatValue(value: Float?, defaultQuantity: String): String {
        return value?.let {
            if (it == it.toLong().toFloat()) {
                it.toLong().toString()
            } else {
                it.toString()
            }
        } ?: defaultQuantity
    }
}
