package ru.ya.practicum.shopper.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.ya.practicum.shopper.data.AppDataBase
import ru.ya.practicum.shopper.data.converter.ShopperItemMapper
import ru.ya.practicum.shopper.data.converter.ShopperListMapper
import ru.ya.practicum.shopper.data.local.dao.ShopperItemDao
import ru.ya.practicum.shopper.data.local.dao.ShopperListsDao
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE shopper_lists ADD COLUMN userId TEXT NOT NULL DEFAULT ''"
        )
    }
}

val dataModule = module {
    single<AppDataBase> {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            "app.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    single<ShopperItemDao> { get<AppDataBase>().shopperItemDao() }

    single<ShopperListsDao> { get<AppDataBase>().shopperListsDao() }

    single { ShopperItemMapper() }

    single { ShopperListMapper() }

    single { OnboardDataStore(androidContext()) }
}
