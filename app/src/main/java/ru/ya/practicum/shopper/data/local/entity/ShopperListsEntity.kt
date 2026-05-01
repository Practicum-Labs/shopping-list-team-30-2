package ru.ya.practicum.shopper.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ShopperListsEntity.TABLE_NAME)
data class ShopperListsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String, // Название списка
    val iconId: Int, // Id выбранной иконки
    val insertTime: Long = System.currentTimeMillis(), // Время добавления в базу данных
) {
    companion object {
        const val TABLE_NAME = "shopper_lists"
    }
}
