package ru.ya.practicum.shopper.core.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme")

class ThemeDataStore(private val context: Context) {

    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val USER_THEME_PREFERENCE_KEY = booleanPreferencesKey("user_theme_preference")
    }

    val isDarkTheme: Flow<Boolean?> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY]
        }

    val hasUserThemePreference: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[USER_THEME_PREFERENCE_KEY] ?: false
        }

    suspend fun setDarkTheme(isDark: Boolean, isUserPreference: Boolean = true) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDark
            preferences[USER_THEME_PREFERENCE_KEY] = isUserPreference
        }
    }

//    suspend fun resetToSystemTheme() {
//        context.dataStore.edit { preferences ->
//            preferences.remove(DARK_THEME_KEY)
//            preferences.remove(USER_THEME_PREFERENCE_KEY)
//        }
//    }
}
