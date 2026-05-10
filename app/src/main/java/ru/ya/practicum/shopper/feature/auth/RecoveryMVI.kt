package ru.ya.practicum.shopper.feature.auth

data class RecoveryState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val generalError: String? = null,
    val isSuccess: Boolean = false
)

sealed class RecoveryIntent {
    data class UpdateEmail(val email: String) : RecoveryIntent()
    object Submit : RecoveryIntent()
    object ResetErrors : RecoveryIntent()
    object NavigateBack : RecoveryIntent()
}

sealed class RecoveryEffect {
    data class ShowError(val message: String) : RecoveryEffect()
    object NavigateToAuth : RecoveryEffect()
    object RecoverySuccess : RecoveryEffect()
}
