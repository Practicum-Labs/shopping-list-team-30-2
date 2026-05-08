package ru.ya.practicum.shopper.feature.auth

class AuthRepository(
    private val api: AuthApi,
    private val dataStore: AuthDataStore
) {
    suspend fun register(email: String, password: String): Result<AuthResponse> = runCatching {
        api.register(RegisterRequest(email, password))
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> = runCatching {
        api.login(LoginRequest(email, password))
    }

    suspend fun refreshToken(refreshToken: String): Result<RefreshResponse> = runCatching {
        api.refresh(RefreshRequest(refreshToken))
    }

    suspend fun saveTokens(response: AuthResponse) {
        dataStore.saveTokens(response.accessToken, response.refreshToken, response.userId)
    }

    suspend fun isAuthenticated(): Boolean {
        val token = dataStore.getAccessToken()
        return if (token != null) {
            runCatching { api.checkAuth("Bearer $token") }
                .getOrNull()?.success == true
        } else {
            false
        }
    }
}
