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
import ru.ya.practicum.shopper.core.ui.theme.Dimens

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginMode: Boolean = true,
    val isAuthenticated: Boolean = false
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
            is AuthIntent.UpdateEmail -> updateField(intent.email, isEmail = true)
            is AuthIntent.UpdatePassword -> updateField(intent.password, isEmail = false)
            AuthIntent.Submit -> submit()
            AuthIntent.ResetError -> resetError()
            AuthIntent.NavigateToSignUp -> sendEffect(AuthEffect.NavigateToSignUp)
            AuthIntent.NavigateToRecovery -> sendEffect(AuthEffect.NavigateToRecovery)
            AuthIntent.ToggleMode -> {}
        }
    }

    private fun updateField(value: String, isEmail: Boolean) {
        _state.update {
            if (isEmail) {
                it.copy(email = value, error = null, isLoading = false)
            } else {
                it.copy(password = value, error = null, isLoading = false)
            }
        }
    }

    private fun resetError() {
        _state.update { it.copy(error = null, isLoading = false) }
    }

    private fun updateState(
        isLoading: Boolean? = null,
        error: String? = null,
        isAuthenticated: Boolean? = null
    ) {
        _state.update { state ->
            state.copy(
                isLoading = isLoading ?: state.isLoading,
                error = error ?: state.error,
                isAuthenticated = isAuthenticated ?: state.isAuthenticated
            )
        }
    }

    private fun isValid(state: AuthState): Boolean {
        return when {
            state.email.isBlank() -> {
                updateState(error = "Введите email", isLoading = false)
                false
            }

            !AuthValidation.isEmailValid(state.email) -> {
                updateState(error = "Введите корректный email", isLoading = false)
                false
            }

            state.password.isBlank() -> {
                updateState(error = "Введите пароль", isLoading = false)
                false
            }

            !AuthValidation.isPasswordValid(state.password) -> {
                updateState(error = "Пароль должен быть не менее 6 символов", isLoading = false)
                false
            }

            state.isLoading -> false
            else -> true
        }
    }

    private fun submit() {
        val currentState = _state.value

        if (!isValid(currentState)) return

        viewModelScope.launch {
            updateState(isLoading = true, error = null)

            val result = if (currentState.isLoginMode) {
                repository.login(currentState.email, currentState.password)
            } else {
                repository.register(currentState.email, currentState.password)
            }

            result.fold(
                onSuccess = { authResponse ->
                    repository.saveTokens(authResponse)
                    updateState(isLoading = false, isAuthenticated = true, error = null)
                    sendEffect(AuthEffect.NavigateToMain)
                },
                onFailure = { error ->
                    handleError(error)
                }
            )
        }
    }

    private fun handleError(error: Throwable) {
        val errorMessage = when (error) {
            is retrofit2.HttpException -> {
                when (error.code()) {
                    Dimens.RESPONSE_400 -> "Неверный формат данных"
                    Dimens.RESPONSE_401 -> "Неверный email или пароль"
                    Dimens.RESPONSE_409 -> "Пользователь уже существует"
                    else -> "Ошибка сервера: ${error.code()}"
                }
            }

            else -> "Ошибка сети: ${error.message}"
        }
        updateState(error = errorMessage, isLoading = false)
        sendEffect(AuthEffect.ShowError(errorMessage))
    }

    private fun sendEffect(effect: AuthEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
