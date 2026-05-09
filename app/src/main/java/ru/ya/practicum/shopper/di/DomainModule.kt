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

    factory {
        ProductDependencies(
            application = get(),
            itemRepository = get(),
            dataStore = get(),
            addProductUseCase = get(),
            toggleProductBoughtUseCase = get(),
            deleteProductUseCase = get(),
            deleteAllProductsUseCase = get(),
            clearBoughtProductsUseCase = get()
        )
    }

    factory { ProductViewModel(get()) }
}
