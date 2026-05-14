package ru.ya.practicum.shopper.feature.auth

import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.resource.ResourceProvider
import ru.ya.practicum.shopper.core.ui.theme.Dimens

object AuthValidation {

    fun validateEmail(email: String): ValidationEmailError? {
        return when {
            email.isBlank() -> ValidationEmailError.Empty
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationEmailError.Invalid
            else -> null
        }
    }

    fun validatePassword(password: String): ValidationPasswordError? {
        return when {
            password.isBlank() -> ValidationPasswordError.Empty
            password.length < Dimens.PASSWORD_LENGTH -> ValidationPasswordError.TooShort
            else -> null
        }
    }

    fun isEmailValid(email: String): Boolean = validateEmail(email) == null

    fun isPasswordValid(password: String): Boolean = validatePassword(password) == null
}

sealed class ValidationEmailError {
    object Empty : ValidationEmailError()
    object Invalid : ValidationEmailError()

    fun getMessage(resourceProvider: ResourceProvider): String {
        return when (this) {
            Empty -> resourceProvider.getString(R.string.error_email_empty)
            Invalid -> resourceProvider.getString(R.string.error_email_invalid)
        }
    }
}

sealed class ValidationPasswordError {
    object Empty : ValidationPasswordError()
    object TooShort : ValidationPasswordError()

    fun getMessage(resourceProvider: ResourceProvider): String {
        return when (this) {
            Empty -> resourceProvider.getString(R.string.error_password_empty)
            TooShort -> resourceProvider.getString(R.string.error_password_too_short)
        }
    }
}

data class AuthValidationResult(
    val emailError: String?,
    val passwordError: String?,
    val isFormValid: Boolean
)

@Composable
fun validateAuthState(state: AuthState): AuthValidationResult {
    val emailError = if (state.email.isNotBlank() && !AuthValidation.isEmailValid(state.email)) {
        stringResource(R.string.error_email_invalid)
    } else {
        null
    }

    val passwordError =
        if (state.password.isNotBlank() && !AuthValidation.isPasswordValid(state.password)) {
            stringResource(R.string.error_password_too_short)
        } else {
            null
        }

    val isFormValid =
        AuthValidation.isEmailValid(state.email) && AuthValidation.isPasswordValid(state.password)

    return AuthValidationResult(emailError, passwordError, isFormValid)
}
