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
            onDelete = ForeignKey.CASCADE // за удалением списка следует удаление товаров
        )
    ],
    indices = [Index("listId")]
)
data class ShopperItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val listId: Int, // к какому списку относимся
    val name: String, // название продукта
    val unit: String? = null, // "кг", "шт", "л" и т.д.
    val value: Float? = null, // количество
    val isBought: Boolean = false, // купили ли
    val position: Int = 0 // для drag & drop
) {
    companion object {
        const val TABLE_NAME = "shopper_items"
    }
}
