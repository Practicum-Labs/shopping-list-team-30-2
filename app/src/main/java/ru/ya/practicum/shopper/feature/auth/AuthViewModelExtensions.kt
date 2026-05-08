package ru.ya.practicum.shopper.feature.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun MutableStateFlow<AuthState>.updateField(value: String, isEmail: Boolean) {
    update {
        if (isEmail) {
            it.copy(email = value, error = null, isLoading = false)
        } else {
            it.copy(password = value, error = null, isLoading = false)
        }
    }
}

fun MutableStateFlow<AuthState>.resetError() {
    update { it.copy(error = null, isLoading = false) }
}

fun MutableStateFlow<AuthState>.setLoading(loading: Boolean) {
    update { it.copy(isLoading = loading) }
}

fun MutableStateFlow<AuthState>.setError(message: String) {
    update { it.copy(error = message, isLoading = false) }
}

fun MutableStateFlow<AuthState>.toggleMode() {
    update {
        it.copy(
            isLoginMode = !it.isLoginMode,
            error = null,
            isLoading = false,
            email = "",
            password = ""
        )
    }
}

fun MutableStateFlow<AuthState>.setSuccess() {
    update {
        it.copy(
            isLoading = false,
            isAuthenticated = true,
            error = null
        )
    }
}
