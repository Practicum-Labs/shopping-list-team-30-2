package ru.ya.practicum.shopper.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.ya.practicum.shopper.data.AppDataBase
import ru.ya.practicum.shopper.data.converter.ShopperItemMapper
import ru.ya.practicum.shopper.data.converter.ShopperListMapper
import ru.ya.practicum.shopper.data.local.dao.ShopperItemDao
import ru.ya.practicum.shopper.data.local.dao.ShopperListsDao

val dataModule = module {
    single<AppDataBase> {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            "app.db"
        ).build()
    }

    single<ShopperItemDao> { get<AppDataBase>().shopperItemDao() }
    single<ShopperListsDao> { get<AppDataBase>().shopperListsDao() }
    single { ShopperItemMapper() }
    single { ShopperListMapper() }
}
