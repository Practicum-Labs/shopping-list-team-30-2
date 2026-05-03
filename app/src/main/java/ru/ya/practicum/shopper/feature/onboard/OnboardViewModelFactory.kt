package ru.ya.practicum.shopper.feature.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OnboardViewModelFactory(
    private val dataStore: OnboardDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnboardViewModel(dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
