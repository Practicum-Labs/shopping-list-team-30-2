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

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginMode: Boolean = true
)

sealed class AuthIntent {
    data class UpdateEmail(val email: String) : AuthIntent()
    data class UpdatePassword(val password: String) : AuthIntent()
    data object ToggleMode : AuthIntent()
    data object Submit : AuthIntent()
    data object ResetError : AuthIntent()
    data object NavigateToSignUp : AuthIntent()
    data object NavigateToRecovery : AuthIntent()
}

sealed class AuthEffect {
    data class ShowError(val message: String) : AuthEffect()
    data object NavigateToMain : AuthEffect()
    data object NavigateToSignUp : AuthEffect()
    data object NavigateToRecovery : AuthEffect()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdateEmail -> updateEmail(intent.email)
            is AuthIntent.UpdatePassword -> updatePassword(intent.password)
            AuthIntent.Submit -> submit()
            AuthIntent.ResetError -> resetError()
            AuthIntent.NavigateToSignUp -> sendEffect(AuthEffect.NavigateToSignUp)
            AuthIntent.NavigateToRecovery -> sendEffect(AuthEffect.NavigateToRecovery)
            AuthIntent.ToggleMode -> toggleMode()
        }
    }

    private fun updateEmail(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }

    private fun updatePassword(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    private fun resetError() {
        _state.update { it.copy(error = null, isLoading = false) }
    }

    private fun toggleMode() {
        _state.update {
            it.copy(
                isLoginMode = !it.isLoginMode,
                error = null,
                email = "",
                password = ""
            )
        }
    }

    private fun isValid(): Boolean {
        val state = _state.value
        return when {
            state.email.isBlank() -> {
                setError("Введите email")
                false
            }
            !AuthValidation.isEmailValid(state.email) -> {
                setError("Введите корректный email")
                false
            }
            state.password.isBlank() -> {
                setError("Введите пароль")
                false
            }
            !AuthValidation.isPasswordValid(state.password) -> {
                setError("Пароль должен быть не менее 6 символов")
                false
            }
            else -> true
        }
    }

    private fun setError(message: String) {
        _state.update { it.copy(error = message, isLoading = false) }
        sendEffect(AuthEffect.ShowError(message))
    }

    private fun submit() {
        if (!isValid()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = if (_state.value.isLoginMode) {
                repository.login(_state.value.email, _state.value.password)
            } else {
                repository.register(_state.value.email, _state.value.password)
            }

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false, error = null) }
                    sendEffect(AuthEffect.NavigateToMain)
                },
                onFailure = { exception ->
                    val message = when (exception) {
                        is AuthException -> exception.getUserMessage()
                        else -> "Ошибка: ${exception.message}"
                    }
                    _state.update { it.copy(isLoading = false, error = message) }
                    sendEffect(AuthEffect.ShowError(message))
                }
            )
        }
    }

    private fun sendEffect(effect: AuthEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
