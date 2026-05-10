package ru.ya.practicum.shopper.di

import org.koin.dsl.module
import ru.ya.practicum.shopper.data.impl.ShopperItemRepositoryImpl
import ru.ya.practicum.shopper.data.impl.ShopperListRepositoryImpl
import ru.ya.practicum.shopper.domain.api.ShoppingListItemInteractor
import ru.ya.practicum.shopper.domain.impl.ShoppingListItemInteractorImpl
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.feature.product.ProductViewModel

val domainModule = module {
    single<ShopperItemRepository> {
        ShopperItemRepositoryImpl(get(), get())
    }

    single<ShopperListRepository> {
        ShopperListRepositoryImpl(get(), get())
    }

    factory { ProductViewModel(get(), get(), get()) }

    single<ShoppingListItemInteractor> { ShoppingListItemInteractorImpl(get(), get()) }
}
