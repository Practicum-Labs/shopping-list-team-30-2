package ru.ya.practicum.shopper.feature.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/registration")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): RefreshResponse

    @GET("/auth/check")
    suspend fun checkAuth(@Header("Authorization") token: String): CheckResponse

    @POST("/auth/recovery")
    suspend fun recoverPassword(@Header("email") email: String): Response<Unit>
}

data class RegisterRequest(val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class RefreshRequest(val refreshToken: String)

data class AuthResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String
)

data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String
)

data class CheckResponse(
    val success: Boolean,
    val refresh: Boolean
)
