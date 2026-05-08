package ru.ya.practicum.shopper.feature.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ya.practicum.shopper.core.ui.theme.Dimens

class OnboardViewModel(
    private val dataStore: OnboardDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardState())
    val state: StateFlow<OnboardState> = _state.asStateFlow()

    init {
        startAutoNavigation()
    }

    fun onEvent(event: OnboardEvent) {
        when (event) {
            OnboardEvent.OnStartClicked -> navigateToMain()
        }
    }

    private fun startAutoNavigation() {
        viewModelScope.launch {
            delay(Dimens.AUTO_NAVIGATION_DELAY_MS)
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        if (_state.value.isNavigatingToMain) return

        viewModelScope.launch {
            dataStore.getOrCreateUserId()
            dataStore.setOnboardCompleted(true)
            _state.update { it.copy(isNavigatingToMain = true) }
        }
    }
}

data class OnboardState(
    val isNavigatingToMain: Boolean = false
)

sealed class OnboardEvent {
    object OnStartClicked : OnboardEvent()
}
