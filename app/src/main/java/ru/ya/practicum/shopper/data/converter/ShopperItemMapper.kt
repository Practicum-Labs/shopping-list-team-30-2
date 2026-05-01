package ru.ya.practicum.shopper.data.converter

import ru.ya.practicum.shopper.data.local.entity.ShopperItemEntity
import ru.ya.practicum.shopper.domain.model.ShopperItem

class ShopperItemMapper {
    fun toEntity(item: ShopperItem, listId: Int): ShopperItemEntity {
        return ShopperItemEntity(
            id = item.id,
            listId = listId,
            name = item.name,
            unit = item.unit,
            value = item.value,
            isBought = item.isBought,
            position = item.position
        )
    }

    fun toDomain(entity: ShopperItemEntity): ShopperItem {
        return ShopperItem(
            id = entity.id,
            name = entity.name,
            unit = entity.unit,
            value = entity.value,
            isBought = entity.isBought,
            position = entity.position
        )
    }
}
