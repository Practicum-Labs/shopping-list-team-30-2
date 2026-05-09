package ru.ya.practicum.shopper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.ya.practicum.shopper.data.local.entity.ShopperListsEntity

@Dao
interface ShopperListsDao {
    @Query("SELECT * FROM ${ShopperListsEntity.TABLE_NAME} ORDER BY insertTime DESC")
    fun getAllLists(): Flow<List<ShopperListsEntity>>

    @Query("SELECT * FROM ${ShopperListsEntity.TABLE_NAME} WHERE id = :id")
    suspend fun getListById(id: Int): ShopperListsEntity?

    @Insert
    suspend fun insert(list: ShopperListsEntity): Long

    @Update
    suspend fun update(list: ShopperListsEntity)

    @Delete
    suspend fun delete(list: ShopperListsEntity)

    @Query("DELETE FROM ${ShopperListsEntity.TABLE_NAME} WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE ${ShopperListsEntity.TABLE_NAME} SET name = :newName WHERE id = :id")
    suspend fun rename(id: Int, newName: String)
}
