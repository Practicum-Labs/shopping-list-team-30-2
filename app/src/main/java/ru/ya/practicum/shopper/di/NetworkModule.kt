package ru.ya.practicum.shopper.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ya.practicum.shopper.BuildConfig
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.feature.auth.AuthInterceptor
import ru.ya.practicum.shopper.feature.auth.TokenAuthenticator
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { AuthInterceptor(get()) }
    single { TokenAuthenticator(get()) }

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Dimens.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Dimens.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Dimens.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
