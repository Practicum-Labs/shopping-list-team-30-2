package ru.ya.practicum.shopper.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = ShopperItemEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = ShopperListsEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId")]
)
data class ShopperItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val listId: Int,
    val name: String,
    val unit: String? = null,
    val value: Float? = null,
    val isBought: Boolean = false,
    val position: Int = 0
) {
    companion object {
        const val TABLE_NAME = "shopper_items"
    }
}
