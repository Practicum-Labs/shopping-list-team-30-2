package ru.ya.practicum.shopper.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.ya.practicum.shopper.core.resource.AndroidResourceProvider
import ru.ya.practicum.shopper.core.resource.ResourceProvider

val coreModule = module {
    single<ResourceProvider> {
        AndroidResourceProvider(androidContext())
    }
}
