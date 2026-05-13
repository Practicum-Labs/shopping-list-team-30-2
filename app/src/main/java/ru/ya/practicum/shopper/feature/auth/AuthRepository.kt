package ru.ya.practicum.shopper.feature.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    private val dataStore: AuthDataStore
) {

    suspend fun register(email: String, password: String): Result<AuthResult> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(AuthException.UserCreationFailed())

            val authResult = AuthResult(
                userId = user.uid,
                email = user.email ?: email,
                isEmailVerified = user.isEmailVerified
            )

            dataStore.saveUserId(user.uid)
            Result.success(authResult)
        } catch (_: FirebaseAuthWeakPasswordException) {
            Result.failure(AuthException.WeakPassword())
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            Result.failure(AuthException.InvalidEmail())
        } catch (_: FirebaseAuthUserCollisionException) {
            Result.failure(AuthException.EmailAlreadyInUse())
        } catch (_: SocketTimeoutException) {
            Result.failure(AuthException.NetworkTimeout())
        } catch (_: UnknownHostException) {
            Result.failure(AuthException.NoInternet())
        } catch (e: IOException) {
            Result.failure(AuthException.NetworkError(e.message ?: "Network error"))
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResult> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(AuthException.LoginFailed())

            val authResult = AuthResult(
                userId = user.uid,
                email = user.email ?: email,
                isEmailVerified = user.isEmailVerified
            )

            dataStore.saveUserId(user.uid)
            Result.success(authResult)
        } catch (_: FirebaseAuthInvalidUserException) {
            Result.failure(AuthException.UserNotFound())
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            Result.failure(AuthException.WrongPassword())
        } catch (_: SocketTimeoutException) {
            Result.failure(AuthException.NetworkTimeout())
        } catch (_: UnknownHostException) {
            Result.failure(AuthException.NoInternet())
        } catch (e: IOException) {
            Result.failure(AuthException.NetworkError(e.message ?: "Network error"))
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (_: FirebaseAuthInvalidUserException) {
            Result.failure(AuthException.UserNotFound())
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            Result.failure(AuthException.InvalidEmail())
        } catch (_: SocketTimeoutException) {
            Result.failure(AuthException.NetworkTimeout())
        } catch (_: UnknownHostException) {
            Result.failure(AuthException.NoInternet())
        } catch (e: IOException) {
            Result.failure(AuthException.NetworkError(e.message ?: "Network error"))
        }
    }
}

data class AuthResult(
    val userId: String,
    val email: String,
    val isEmailVerified: Boolean = false
)

sealed class AuthException(message: String) : Exception(message) {
    class WeakPassword : AuthException("Пароль должен содержать минимум 6 символов")
    class InvalidEmail : AuthException("Неверный формат email")
    class EmailAlreadyInUse : AuthException("Пользователь с таким email уже существует")
    class UserNotFound : AuthException("Пользователь с таким email не найден")
    class WrongPassword : AuthException("Неверный пароль")
    class NetworkTimeout : AuthException("Превышено время ожидания. Проверьте подключение")
    class NoInternet : AuthException("Отсутствует подключение к интернету")
    data class NetworkError(val detail: String) : AuthException("Ошибка сети: $detail")
    class UserCreationFailed : AuthException("Ошибка создания пользователя")
    class LoginFailed : AuthException("Ошибка входа в систему")

    fun getUserMessage(): String = when (this) {
        is WeakPassword -> "Пароль должен содержать минимум 6 символов"
        is InvalidEmail -> "Введите корректный email"
        is EmailAlreadyInUse -> "Пользователь с таким email уже существует"
        is UserNotFound -> "Пользователь с таким email не найден"
        is WrongPassword -> "Неверный пароль"
        is NetworkTimeout -> "Превышено время ожидания. Проверьте подключение к интернету"
        is NoInternet -> "Отсутствует подключение к интернету"
        is UserCreationFailed -> "Ошибка создания пользователя. Попробуйте позже"
        is LoginFailed -> "Ошибка входа в систему. Попробуйте позже"
        is NetworkError -> detail
    }
}
