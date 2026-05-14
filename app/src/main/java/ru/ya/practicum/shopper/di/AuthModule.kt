package ru.ya.practicum.shopper.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.ya.practicum.shopper.feature.auth.AuthDataStore
import ru.ya.practicum.shopper.feature.auth.AuthRepository
import ru.ya.practicum.shopper.feature.auth.AuthViewModel
import ru.ya.practicum.shopper.feature.auth.RecoveryViewModel
import ru.ya.practicum.shopper.feature.auth.SignUpViewModel

val authModule = module {
    single { Firebase.auth }

    single { AuthDataStore(androidContext()) }
    single { AuthRepository(get(), get()) }

    viewModel { AuthViewModel(get(), get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { RecoveryViewModel(get(), get()) }
}
