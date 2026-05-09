package ru.ya.practicum.shopper.feature.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthDataStore(private val context: Context) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[ACCESS_TOKEN_KEY] }

    suspend fun saveTokens(accessToken: String, refreshToken: String, userId: Long) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_ID_KEY] = userId.toString()
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getAccessToken(): String? =
        context.dataStore.data.map { it[ACCESS_TOKEN_KEY] }.first()

    suspend fun getRefreshToken(): String? =
        context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }.first()

    suspend fun getUserId(): Long? {
        return context.dataStore.data
            .map { preferences -> preferences[USER_ID_KEY]?.toLongOrNull() }
            .first()
    }

    suspend fun hasValidTokens(): Boolean {
        val token = getAccessToken()
        return !token.isNullOrBlank()
    }
}
