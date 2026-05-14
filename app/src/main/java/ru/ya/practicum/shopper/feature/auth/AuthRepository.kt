package ru.ya.practicum.shopper.feature.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.resource.ResourceProvider
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
        } catch (_: FirebaseNetworkException) {
            Result.failure(AuthException.NetworkError("Network error occurred"))
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
        } catch (_: FirebaseNetworkException) {
            Result.failure(AuthException.NetworkError("Network error occurred"))
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
        } catch (_: FirebaseNetworkException) {
            Result.failure(AuthException.NetworkError("Network error occurred"))
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

sealed class AuthException(
    val messageResId: Int
) : Exception() {
    class WeakPassword : AuthException(R.string.error_auth_weak_password)
    class InvalidEmail : AuthException(R.string.error_auth_invalid_email)
    class EmailAlreadyInUse : AuthException(R.string.error_auth_email_in_use)
    class UserNotFound : AuthException(R.string.error_auth_user_not_found)
    class WrongPassword : AuthException(R.string.error_auth_wrong_password)
    class NetworkTimeout : AuthException(R.string.error_auth_network_timeout)
    class NoInternet : AuthException(R.string.error_auth_no_internet)
    data class NetworkError(val detail: String) : AuthException(R.string.error_auth_network_error)
    class UserCreationFailed : AuthException(R.string.error_auth_user_creation_failed)
    class LoginFailed : AuthException(R.string.error_auth_login_failed)

    fun getUserMessage(resourceProvider: ResourceProvider): String {
        return when (this) {
            is NetworkError -> resourceProvider.getString(messageResId, detail)
            else -> resourceProvider.getString(messageResId)
        }
    }
}
