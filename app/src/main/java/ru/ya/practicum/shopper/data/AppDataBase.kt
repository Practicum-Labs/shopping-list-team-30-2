package ru.ya.practicum.shopper.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.ya.practicum.shopper.data.local.dao.ShopperItemDao
import ru.ya.practicum.shopper.data.local.dao.ShopperListsDao
import ru.ya.practicum.shopper.data.local.entity.ShopperItemEntity
import ru.ya.practicum.shopper.data.local.entity.ShopperListsEntity

@Database(
    entities = [ShopperListsEntity::class, ShopperItemEntity::class],
    version = 2
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun shopperListsDao(): ShopperListsDao
    abstract fun shopperItemDao(): ShopperItemDao
}
