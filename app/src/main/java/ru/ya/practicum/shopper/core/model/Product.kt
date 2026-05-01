package ru.ya.practicum.shopper.core.model

data class Product(
    val id: Long = 0,
    val name: String,
    val amount: String,
    val unit: String = "",
    val isBought: Boolean = false
)
