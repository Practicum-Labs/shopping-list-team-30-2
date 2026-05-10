package ru.ya.practicum.shopper.feature.auth

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
    object Submit : SignUpIntent()
    object ResetErrors : SignUpIntent()
    object NavigateBack : SignUpIntent()
}

sealed class SignUpEffect {
    data class ShowError(val message: String) : SignUpEffect()
    object NavigateToAuth : SignUpEffect()
    object RegistrationSuccess : SignUpEffect()
}
