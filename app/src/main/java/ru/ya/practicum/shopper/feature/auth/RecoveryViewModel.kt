package ru.ya.practicum.shopper.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecoveryViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecoveryState())
    val state: StateFlow<RecoveryState> = _state.asStateFlow()

    private val _effect = Channel<RecoveryEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: RecoveryIntent) {
        when (intent) {
            is RecoveryIntent.UpdateEmail -> updateEmail(intent.email)
            RecoveryIntent.Submit -> submit()
            RecoveryIntent.ResetErrors -> resetErrors()
            RecoveryIntent.NavigateBack -> sendEffect(RecoveryEffect.NavigateToAuth)
        }
    }

    private fun updateEmail(email: String) {
        val emailError = AuthValidation.validateEmail(email)
        _state.update {
            it.copy(
                email = email,
                emailError = emailError,
                generalError = null
            )
        }
    }

    private fun resetErrors() {
        _state.update {
            it.copy(
                emailError = null,
                generalError = null
            )
        }
    }

    private fun isValid(): Boolean {
        val emailError = AuthValidation.validateEmail(_state.value.email)
        _state.update { it.copy(emailError = emailError) }
        return emailError == null
    }

    private fun submit() {
        if (!isValid()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            val result = repository.resetPassword(_state.value.email)

            result.fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            generalError = null
                        )
                    }
                    sendEffect(RecoveryEffect.RecoverySuccess)
                },
                onFailure = { exception ->
                    val message = when (exception) {
                        is AuthException -> exception.getUserMessage()
                        else -> "Ошибка: ${exception.message}"
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = message
                        )
                    }
                    sendEffect(RecoveryEffect.ShowError(message))
                }
            )
        }
    }

    private fun sendEffect(effect: RecoveryEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}

data class RecoveryState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val generalError: String? = null,
    val isSuccess: Boolean = false
)

sealed class RecoveryIntent {
    data class UpdateEmail(val email: String) : RecoveryIntent()
    data object Submit : RecoveryIntent()
    data object ResetErrors : RecoveryIntent()
    data object NavigateBack : RecoveryIntent()
}

sealed class RecoveryEffect {
    data class ShowError(val message: String) : RecoveryEffect()
    data object NavigateToAuth : RecoveryEffect()
    data object RecoverySuccess : RecoveryEffect()
}
