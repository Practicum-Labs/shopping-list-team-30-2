package ru.ya.practicum.shopper.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RecoveryViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(RecoveryState())
    val state: StateFlow<RecoveryState> = _state.asStateFlow()

    private val _effect = Channel<RecoveryEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: RecoveryIntent) {
        when (intent) {
            is RecoveryIntent.UpdateEmail -> updateEmail(intent.email)
            RecoveryIntent.Submit -> submit()
            RecoveryIntent.ResetErrors -> resetErrors()
            RecoveryIntent.NavigateBack -> sendEffect(RecoveryEffect.NavigateToAuth)
        }
    }

    private fun updateEmail(email: String) {
        val emailError = AuthValidation.validateEmail(email)
        _state.update {
            it.copy(
                email = email,
                emailError = emailError,
                generalError = null
            )
        }
    }

    private fun resetErrors() {
        _state.update {
            it.copy(
                emailError = null,
                generalError = null
            )
        }
    }

    private fun submit() {
        val currentState = _state.value
        val emailError = AuthValidation.validateEmail(currentState.email)

        if (emailError != null) {
            _state.update { it.copy(emailError = emailError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            try {
                auth.sendPasswordResetEmail(currentState.email).await()
                handleSuccess()
            } catch (_: FirebaseAuthInvalidUserException) {
                handleError("Пользователь с таким email не найден")
            } catch (_: FirebaseAuthInvalidCredentialsException) {
                handleError("Неверный формат email")
            } catch (_: FirebaseNetworkException) {
                handleError("Ошибка сети. Проверьте подключение к интернету.")
            } catch (e: FirebaseAuthException) {
                val message = when (e.errorCode) {
                    "ERROR_TOO_MANY_REQUESTS" -> "Слишком много запросов. Попробуйте позже."
                    else -> "Ошибка сервера. Попробуйте позже."
                }
                handleError(message)
            } catch (_: SocketTimeoutException) {
                handleError("Превышено время ожидания. Проверьте подключение к интернету.")
            } catch (_: UnknownHostException) {
                handleError("Отсутствует подключение к интернету")
            } catch (e: IOException) {
                handleError("Ошибка сети: ${e.message ?: "Проверьте подключение"}")
            }
        }
    }

    private fun handleSuccess() {
        _state.update {
            it.copy(
                isLoading = false,
                isSuccess = true,
                generalError = null
            )
        }
        sendEffect(RecoveryEffect.RecoverySuccess)
    }

    private fun handleError(userMessage: String) {
        _state.update {
            it.copy(
                isLoading = false,
                generalError = userMessage
            )
        }
        sendEffect(RecoveryEffect.ShowError(userMessage))
    }

    private fun sendEffect(effect: RecoveryEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
