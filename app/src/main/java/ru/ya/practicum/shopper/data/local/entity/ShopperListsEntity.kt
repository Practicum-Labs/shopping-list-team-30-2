package ru.ya.practicum.shopper.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ShopperListsEntity.TABLE_NAME)
data class ShopperListsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val iconId: Int,
    val insertTime: Long = System.currentTimeMillis(),
    val userId: String = ""
) {
    companion object {
        const val TABLE_NAME = "shopper_lists"
    }
}
