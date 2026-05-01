package ru.ya.practicum.shopper.domain.model

data class ShopperList(
    val id: Long,
    val name: String,
    val iconId: Int,
    val createdAt: Long = 0L
)
