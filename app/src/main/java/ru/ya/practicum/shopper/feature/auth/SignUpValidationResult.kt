package ru.ya.practicum.shopper.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.ya.practicum.shopper.R

@Composable
fun validateSignUpState(state: SignUpState): SignUpValidationResult {
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

    val confirmError =
        if (state.confirmPassword.isNotBlank() && state.password != state.confirmPassword) {
            stringResource(R.string.error_passwords_mismatch)
        } else {
            null
        }

    val isFormValid = AuthValidation.isEmailValid(state.email) &&
        AuthValidation.isPasswordValid(state.password) &&
        state.password == state.confirmPassword

    return SignUpValidationResult(emailError, passwordError, confirmError, isFormValid)
}

data class SignUpValidationResult(
    val emailError: String?,
    val passwordError: String?,
    val confirmError: String?,
    val isFormValid: Boolean
)
