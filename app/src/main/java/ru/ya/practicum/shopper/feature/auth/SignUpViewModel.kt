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
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.resource.ResourceProvider

class SignUpViewModel(
    private val repository: AuthRepository,
    private val resourceProvider: ResourceProvider
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
        val validationError = AuthValidation.validateEmail(email)
        val emailError = validationError?.getMessage(resourceProvider)

        _state.update {
            it.copy(
                email = email,
                emailError = emailError,
                generalError = null
            )
        }
    }

    private fun updatePassword(password: String) {
        val validationError = AuthValidation.validatePassword(password)
        val passwordError = validationError?.getMessage(resourceProvider)

        _state.update {
            it.copy(
                password = password,
                passwordError = passwordError,
                generalError = null
            )
        }
        if (_state.value.confirmPassword.isNotEmpty()) {
            updateConfirmPassword(_state.value.confirmPassword)
        }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        val currentPassword = _state.value.password
        val confirmError = if (confirmPassword != currentPassword) {
            resourceProvider.getString(R.string.error_passwords_mismatch)
        } else {
            null
        }
        _state.update {
            it.copy(
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

    private fun isValid(): Boolean {
        val state = _state.value
        val emailValidationError = AuthValidation.validateEmail(state.email)
        val passwordValidationError = AuthValidation.validatePassword(state.password)
        val confirmError = if (state.password != state.confirmPassword) {
            resourceProvider.getString(R.string.error_passwords_mismatch)
        } else {
            null
        }

        _state.update {
            it.copy(
                emailError = emailValidationError?.getMessage(resourceProvider),
                passwordError = passwordValidationError?.getMessage(resourceProvider),
                confirmPasswordError = confirmError
            )
        }

        return emailValidationError == null && passwordValidationError == null && confirmError == null
    }

    private fun submit() {
        if (!isValid()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            val result = repository.register(_state.value.email, _state.value.password)

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    sendEffect(SignUpEffect.RegistrationSuccess)
                },
                onFailure = { exception ->
                    val message = when (exception) {
                        is AuthException -> exception.getUserMessage(resourceProvider)
                        else -> resourceProvider.getString(
                            R.string.error_unknown,
                            exception.message ?: ""
                        )
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = message
                        )
                    }
                }
            )
        }
    }

    private fun sendEffect(effect: SignUpEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isSuccess: Boolean = false
)

sealed class SignUpIntent {
    data class UpdateEmail(val email: String) : SignUpIntent()
    data class UpdatePassword(val password: String) : SignUpIntent()
    data class UpdateConfirmPassword(val password: String) : SignUpIntent()
    data object Submit : SignUpIntent()
    data object ResetErrors : SignUpIntent()
    data object NavigateBack : SignUpIntent()
}

sealed class SignUpEffect {
    data class ShowError(val message: String) : SignUpEffect()
    data object NavigateToAuth : SignUpEffect()
    data object RegistrationSuccess : SignUpEffect()
}
