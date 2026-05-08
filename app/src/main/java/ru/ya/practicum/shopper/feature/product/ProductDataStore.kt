package ru.ya.practicum.shopper.feature.product

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "product_setting")

class ProductDataStore(private val context: Context) {

    companion object {
        private val PRODUCTS_SORT_BY_NAME_KEY = booleanPreferencesKey("products_sore_by_name")
    }

    val isProductsSortByName: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PRODUCTS_SORT_BY_NAME_KEY] ?: false }

    suspend fun setProductsSortByName(byName: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PRODUCTS_SORT_BY_NAME_KEY] = byName
        }
    }

}
