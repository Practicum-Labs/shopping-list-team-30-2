package ru.ya.practicum.shopper.di

import org.koin.dsl.module
import ru.ya.practicum.shopper.data.impl.ShopperItemRepositoryImpl
import ru.ya.practicum.shopper.data.impl.ShopperListRepositoryImpl
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.domain.usecase.product.AddProductUseCase
import ru.ya.practicum.shopper.feature.product.ProductViewModel

val domainModule = module {
    single<ShopperItemRepository> {
        ShopperItemRepositoryImpl(get(), get())
    }

    single<ShopperListRepository> {
        ShopperListRepositoryImpl(get(), get())
    }

    factory { AddProductUseCase(get()) }

    factory { ProductViewModel(get(), get(), get(), get()) }
}
