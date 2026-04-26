package ru.ya.practicum.shopper.feature.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardViewModel(
    private val dataStore: OnboardDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardState())
    val state: StateFlow<OnboardState> = _state.asStateFlow()

    fun onEvent(event: OnboardEvent) {
        when (event) {
            OnboardEvent.OnStartClicked -> {
                viewModelScope.launch {
                    dataStore.setOnboardCompleted(true)
                    _state.value = _state.value.copy(
                        isNavigatingToMain = true
                    )
                }
            }
        }
    }
}

data class OnboardState(
    val isNavigatingToMain: Boolean = false
)

sealed class OnboardEvent {
    object OnStartClicked : OnboardEvent()
}
