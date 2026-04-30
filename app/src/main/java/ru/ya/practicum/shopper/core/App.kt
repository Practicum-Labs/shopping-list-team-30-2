package ru.ya.practicum.shopper.core

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.ya.practicum.shopper.di.dataModule
import ru.ya.practicum.shopper.di.domainModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                domainModule,
            )
        }
    }
}
