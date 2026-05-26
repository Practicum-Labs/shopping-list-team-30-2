package ru.ya.practicum.shopper.data.converter

import ru.ya.practicum.shopper.data.local.entity.ShopperListsEntity
import ru.ya.practicum.shopper.domain.model.ShopperList

class ShopperListMapper {
    fun toDomain(entity: ShopperListsEntity): ShopperList {
        return ShopperList(
            id = entity.id.toLong(),
            name = entity.name,
            iconId = entity.iconId,
            createdAt = entity.insertTime,
            userId = entity.userId
        )
    }

    fun toEntity(domain: ShopperList): ShopperListsEntity {
        return ShopperListsEntity(
            id = domain.id.toInt(),
            name = domain.name,
            iconId = domain.iconId,
            insertTime = System.currentTimeMillis(),
            userId = domain.userId
        )
    }
}
