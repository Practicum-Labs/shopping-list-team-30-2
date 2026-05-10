package ru.ya.practicum.shopper.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import ru.ya.practicum.shopper.feature.auth.AuthApi
import ru.ya.practicum.shopper.feature.auth.AuthDataStore
import ru.ya.practicum.shopper.feature.auth.AuthRepository
import ru.ya.practicum.shopper.feature.auth.AuthViewModel
import ru.ya.practicum.shopper.feature.auth.RecoveryViewModel
import ru.ya.practicum.shopper.feature.auth.SignUpViewModel

val authModule = module {
    single { AuthDataStore(androidContext()) }

    single { get<Retrofit>().create(AuthApi::class.java) }

    single { AuthRepository(get(), get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { RecoveryViewModel() }
}
