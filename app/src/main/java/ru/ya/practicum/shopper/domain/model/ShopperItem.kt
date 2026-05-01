package ru.ya.practicum.shopper.domain.model

data class ShopperItem(
    val id: Int = 0,
    val name: String,
    val unit: String? = null,
    val value: Float? = null,
    val isBought: Boolean = false,
    val position: Int = 0
)
