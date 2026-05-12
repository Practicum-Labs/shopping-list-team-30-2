package ru.ya.practicum.shopper.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R

@Composable
fun AuthForm(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val passwordFocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthTitle(isLoginMode = state.isLoginMode)
        Spacer(modifier = Modifier.height(32.dp))

        AuthEmailSection(
            state = state,
            onIntent = onIntent,
            passwordFocusRequester = passwordFocusRequester
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthPasswordSection(
            state = state,
            onIntent = onIntent,
            passwordFocusRequester = passwordFocusRequester
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthSubmitSection(
            state = state,
            onIntent = onIntent
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthToggleButton(
            state = state,
            onIntent = onIntent
        )
    }
}

@Composable
private fun AuthTitle(isLoginMode: Boolean) {
    Text(
        text = if (isLoginMode) {
            stringResource(R.string.entrance)
        } else {
            stringResource(R.string.registration)
        },
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun AuthEmailSection(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    passwordFocusRequester: FocusRequester
) {
    val emailError = if (state.email.isNotBlank() && !AuthValidation.isEmailValid(state.email)) {
        "Введите корректный email"
    } else {
        null
    }

    EmailField(
        value = state.email,
        onValueChange = {
            onIntent(AuthIntent.UpdateEmail(it))
            onIntent(AuthIntent.ResetError)
        },
        error = emailError ?: state.error?.takeIf { it.contains("email", ignoreCase = true) },
        onNext = {
            passwordFocusRequester.requestFocus()
        }
    )
}

@Composable
private fun AuthPasswordSection(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    passwordFocusRequester: FocusRequester
) {
    val passwordError =
        if (state.password.isNotBlank() && !AuthValidation.isPasswordValid(state.password)) {
            "Пароль должен быть не менее 6 символов"
        } else {
            null
        }

    PasswordField(
        value = state.password,
        onValueChange = {
            onIntent(AuthIntent.UpdatePassword(it))
            onIntent(AuthIntent.ResetError)
        },
        error = passwordError ?: state.error?.takeIf { it.contains("пароль", ignoreCase = true) },
        onSubmit = { onIntent(AuthIntent.Submit) },
        modifier = Modifier.focusRequester(passwordFocusRequester)
    )
}

@Composable
private fun AuthSubmitSection(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit
) {
    val isFormValid =
        AuthValidation.isEmailValid(state.email) && AuthValidation.isPasswordValid(state.password)

    SubmitButton(
        isLoading = state.isLoading,
        text = if (state.isLoginMode) stringResource(R.string.login) else stringResource(R.string.sign_in),
        onClick = { onIntent(AuthIntent.Submit) },
        enabled = isFormValid
    )
}

@Composable
private fun AuthToggleButton(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit
) {
    AuthTextButton(
        text = if (state.isLoginMode) {
            stringResource(R.string.no_account_register)
        } else {
            stringResource(R.string.account_exists_entrance)
        },
        onClick = {
            onIntent(AuthIntent.ToggleMode)
            onIntent(AuthIntent.ResetError)
        }
    )
}
