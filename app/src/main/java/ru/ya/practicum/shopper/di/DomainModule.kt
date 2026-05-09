package ru.ya.practicum.shopper.di

import org.koin.dsl.module
import ru.ya.practicum.shopper.data.impl.ShopperItemRepositoryImpl
import ru.ya.practicum.shopper.data.impl.ShopperListRepositoryImpl
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.domain.usecase.product.AddProductUseCase
import ru.ya.practicum.shopper.domain.usecase.product.ClearBoughtProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.DeleteAllProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.DeleteProductUseCase
import ru.ya.practicum.shopper.domain.usecase.product.GetProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.GetSortingSettingUseCase
import ru.ya.practicum.shopper.domain.usecase.product.MapProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.SaveSortingSettingUseCase
import ru.ya.practicum.shopper.domain.usecase.product.ToggleProductBoughtUseCase
import ru.ya.practicum.shopper.feature.product.ProductDependencies
import ru.ya.practicum.shopper.feature.product.ProductViewModel

val domainModule = module {
    single<ShopperItemRepository> {
        ShopperItemRepositoryImpl(get(), get())
    }

    single<ShopperListRepository> {
        ShopperListRepositoryImpl(get(), get())
    }

    factory { AddProductUseCase(get()) }
    factory { ToggleProductBoughtUseCase(get()) }
    factory { DeleteProductUseCase(get()) }
    factory { DeleteAllProductsUseCase(get()) }
    factory { ClearBoughtProductsUseCase(get()) }
    factory { GetProductsUseCase(get()) }
    factory { GetSortingSettingUseCase(get()) }
    factory { SaveSortingSettingUseCase(get()) }
    factory { MapProductsUseCase() }

    factory {
        ProductDependencies(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    factory { ProductViewModel(get()) }
}
