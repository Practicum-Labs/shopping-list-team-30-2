package ru.ya.practicum.shopper.feature.onboard

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class OnboardDataStore(private val context: Context) {

    companion object {
        private val ONBOARD_COMPLETED_KEY = booleanPreferencesKey("onboard_completed")
    }

    val isOnboardCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARD_COMPLETED_KEY] ?: false
        }

    suspend fun setOnboardCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARD_COMPLETED_KEY] = completed
        }
    }
}
