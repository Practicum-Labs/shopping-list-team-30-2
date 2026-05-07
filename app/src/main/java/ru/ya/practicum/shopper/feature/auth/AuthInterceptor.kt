package ru.ya.practicum.shopper.feature.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val dataStore: AuthDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.url.host.contains("practicumopbackend")) {
            val token = runBlocking {
                runCatching { dataStore.getAccessToken() }.getOrNull()
            }
            if (!token.isNullOrBlank()) {
                request = request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            }
        }

        return chain.proceed(request)
    }
}
