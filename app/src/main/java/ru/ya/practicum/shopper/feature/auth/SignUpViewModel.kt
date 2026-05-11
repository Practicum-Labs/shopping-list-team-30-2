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

class SignUpViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    private val _effect = Channel<SignUpEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: SignUpIntent) {
        when (intent) {
            is SignUpIntent.UpdateEmail -> updateEmail(intent.email)
            is SignUpIntent.UpdatePassword -> updatePassword(intent.password)
            is SignUpIntent.UpdateConfirmPassword -> updateConfirmPassword(intent.password)
            SignUpIntent.Submit -> submit()
            SignUpIntent.ResetErrors -> resetErrors()
            SignUpIntent.NavigateBack -> sendEffect(SignUpEffect.NavigateToAuth)
        }
    }

    private fun updateEmail(email: String) {
        val emailError = AuthValidation.validateEmail(email)
        _state.update { currentState ->
            currentState.copy(
                email = email,
                emailError = emailError,
                generalError = null
            )
        }
    }

    private fun updatePassword(password: String) {
        val passwordError = AuthValidation.validatePassword(password)
        _state.update { currentState ->
            currentState.copy(
                password = password,
                passwordError = passwordError,
                generalError = null
            )
        }

        val currentState = _state.value
        if (currentState.confirmPassword.isNotEmpty()) {
            updateConfirmPassword(currentState.confirmPassword)
        }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        val currentPassword = _state.value.password
        val confirmError = if (confirmPassword != currentPassword) {
            "Пароли не совпадают"
        } else {
            null
        }
        _state.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = confirmError,
                generalError = null
            )
        }
    }

    private fun resetErrors() {
        _state.update {
            it.copy(
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                generalError = null
            )
        }
    }

    private fun submit() {
        val currentState = _state.value

        val emailError = AuthValidation.validateEmail(currentState.email)
        val passwordError = AuthValidation.validatePassword(currentState.password)
        val confirmError = if (currentState.password != currentState.confirmPassword) {
            "Пароли не совпадают"
        } else {
            null
        }

        if (emailError != null || passwordError != null || confirmError != null) {
            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmError,
                    generalError = null
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            val result = repository.register(currentState.email, currentState.password)

            result.fold(
                onSuccess = { authResponse ->
                    repository.saveTokens(authResponse)
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    sendEffect(SignUpEffect.RegistrationSuccess)
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
                    Dimens.RESPONSE_400 -> "Некорректный email или пароль менее 7 символов"
                    Dimens.RESPONSE_409 -> "Пользователь с таким email уже существует"
                    else -> "Ошибка сервера: ${error.code()}"
                }
            }

            else -> "Ошибка сети: ${error.message}"
        }
        _state.update {
            it.copy(
                isLoading = false,
                generalError = errorMessage
            )
        }
        sendEffect(SignUpEffect.ShowError(errorMessage))
    }

    private fun sendEffect(effect: SignUpEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
