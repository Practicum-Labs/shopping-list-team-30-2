package ru.ya.practicum.shopper.core.model

data class ShoppingList(
    val id: Int = 0,
    val name: String,
    val iconResId: Int,
    val userId: String = ""
)
