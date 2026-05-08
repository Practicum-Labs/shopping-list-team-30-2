package ru.ya.practicum.shopper.feature.auth

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ya.practicum.shopper.BuildConfig
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class TokenAuthenticator(
    private val dataStore: AuthDataStore
) : Authenticator {

    @Volatile
    private var cachedRefreshToken: String? = null

    @Volatile
    private var isRefreshing = false

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.accessTokenFlow.onEach { token ->
                isRefreshing = false
            }.launchIn(CoroutineScope(Dispatchers.IO))
        }

        CoroutineScope(Dispatchers.IO).launch {
            dataStore.getRefreshToken()?.let {
                cachedRefreshToken = it
            }
        }
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        return synchronized(this) {
            if (isRefreshing) {
                return null
            }

            isRefreshing = true

            try {
                performAuthentication(response)
            } finally {
                isRefreshing = false
            }
        }
    }

    private fun performAuthentication(response: Response): Request? {
        val refreshToken = cachedRefreshToken
        if (refreshToken.isNullOrBlank()) {
            return null
        }

        val api = createAuthApi()
        val refreshResponse = runBlocking {
            try {
                api.refresh(RefreshRequest(refreshToken))
            } catch (e: HttpException) {
                handleHttpError(e)
                null
            } catch (e: IOException) {
                handleNetworkError(e)
                null
            } catch (e: IllegalStateException) {
                handleStateError(e)
                null
            }
        }

        return refreshResponse?.let {
            updateTokens(it)
            createNewRequest(response, it.accessToken)
        } ?: run {
            clearTokens()
            null
        }
    }

    private fun updateTokens(refreshResponse: RefreshResponse) {
        cachedRefreshToken = refreshResponse.refreshToken
        runBlocking {
            val userId = dataStore.getUserId() ?: 0
            dataStore.saveTokens(
                refreshResponse.accessToken,
                refreshResponse.refreshToken,
                userId
            )
        }
    }

    private fun createNewRequest(response: Response, accessToken: String): Request {
        return response.request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }

    private fun clearTokens() {
        runBlocking { dataStore.clearTokens() }
    }

    private fun createAuthApi(): AuthApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(AuthApi::class.java)
    }

    private fun handleHttpError(error: HttpException) {
        when (error.code()) {
            Dimens.RESPONSE_401, Dimens.RESPONSE_403 -> {
                runBlocking { dataStore.clearTokens() }
            }

            else -> {
                Log.e("TokenAuthenticator", "HTTP error refreshing token: ${error.code()}")
            }
        }
    }

    private fun handleNetworkError(error: IOException) {
        when (error) {
            is SocketTimeoutException -> {
                Log.e("TokenAuthenticator", "Timeout while refreshing token")
            }

            is UnknownHostException -> {
                Log.e("TokenAuthenticator", "No internet connection")
            }

            else -> {
                Log.e(
                    "TokenAuthenticator",
                    "Network error while refreshing token: ${error.message}"
                )
            }
        }
    }

    private fun handleStateError(error: IllegalStateException) {
        Log.e("TokenAuthenticator", "State error while refreshing token: ${error.message}")
    }
}
