package ru.ya.practicum.shopper.feature.auth

import android.util.Patterns
import ru.ya.practicum.shopper.core.ui.theme.Dimens

object AuthValidation {

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email не может быть пустым"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Введите корректный email"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Пароль не может быть пустым"
            password.length < Dimens.PASSWORD_LENGTH -> "Пароль должен быть не менее ${Dimens.PASSWORD_LENGTH} символов"
            else -> null
        }
    }

    fun isEmailValid(email: String): Boolean {
        return validateEmail(email) == null
    }

    fun isPasswordValid(password: String): Boolean {
        return validatePassword(password) == null
    }
}

data class AuthValidationResult(
    val emailError: String?,
    val passwordError: String?,
    val isFormValid: Boolean
)

fun validateAuthState(state: AuthState): AuthValidationResult {
    val emailError = if (state.email.isNotBlank() && !AuthValidation.isEmailValid(state.email)) {
        "Введите корректный email"
    } else {
        null
    }

    val passwordError =
        if (state.password.isNotBlank() && !AuthValidation.isPasswordValid(state.password)) {
            "Пароль должен быть не менее ${Dimens.PASSWORD_LENGTH} символов"
        } else {
            null
        }

    val isFormValid =
        AuthValidation.isEmailValid(state.email) && AuthValidation.isPasswordValid(state.password)

    return AuthValidationResult(emailError, passwordError, isFormValid)
}
