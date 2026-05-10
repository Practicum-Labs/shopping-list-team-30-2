package ru.ya.practicum.shopper.feature.auth

fun validateSignUpState(state: SignUpState): SignUpValidationResult {
    val emailError = if (state.email.isNotBlank() && !AuthValidation.isEmailValid(state.email)) {
        "Введите корректный email"
    } else {
        null
    }

    val passwordError = if (state.password.isNotBlank() && !AuthValidation.isPasswordValid(state.password)) {
        "Пароль должен быть не менее 6 символов"
    } else {
        null
    }

    val confirmError = if (state.confirmPassword.isNotBlank() && state.password != state.confirmPassword) {
        "Пароли не совпадают"
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
