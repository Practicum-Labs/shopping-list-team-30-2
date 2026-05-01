package ru.ya.practicum.shopper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.ya.practicum.shopper.data.local.entity.ShopperItemEntity

@Dao
interface ShopperItemDao {
    @Query("SELECT * FROM ${ShopperItemEntity.TABLE_NAME} WHERE listId = :listId ORDER BY position ASC")
    fun getItems(listId: Int): Flow<List<ShopperItemEntity>>

    @Insert
    suspend fun insert(item: ShopperItemEntity)
    @Update
    suspend fun update(item: ShopperItemEntity)
    @Delete
    suspend fun delete(item: ShopperItemEntity)

    @Query("DELETE FROM ${ShopperItemEntity.TABLE_NAME} WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE ${ShopperItemEntity.TABLE_NAME} SET isBought = :bought WHERE id = :id")
    suspend fun setBought(id: Int, bought: Boolean)

    @Query("DELETE FROM ${ShopperItemEntity.TABLE_NAME} WHERE listId = :listId AND isBought = 1")
    suspend fun clearBought(listId: Int)
}
