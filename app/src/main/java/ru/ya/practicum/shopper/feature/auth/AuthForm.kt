package ru.ya.practicum.shopper.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

        EmailField(
            value = state.email,
            onValueChange = {
                onIntent(AuthIntent.UpdateEmail(it))
                onIntent(AuthIntent.ResetError)
            },
            isError = isEmailError(state),
            onNext = {
                passwordFocusRequester.requestFocus()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            value = state.password,
            onValueChange = {
                onIntent(AuthIntent.UpdatePassword(it))
                onIntent(AuthIntent.ResetError)
            },
            isError = isPasswordError(state),
            onSubmit = { onIntent(AuthIntent.Submit) },
            modifier = Modifier.focusRequester(passwordFocusRequester)
        )

        ErrorMessage(error = state.error)
        Spacer(modifier = Modifier.height(24.dp))

        SubmitButton(
            isLoading = state.isLoading,
            isLoginMode = state.isLoginMode,
            onClick = { onIntent(AuthIntent.Submit) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ToggleModeButton(
            isLoginMode = state.isLoginMode,
            onClick = {
                onIntent(AuthIntent.ToggleMode)
                onIntent(AuthIntent.ResetError)
            }
        )
    }
}

@Composable
private fun isEmailError(state: AuthState): Boolean {
    return state.error?.contains(stringResource(R.string.email), ignoreCase = true) == true
}

@Composable
private fun isPasswordError(state: AuthState): Boolean {
    return state.error?.contains(stringResource(R.string.password), ignoreCase = true) == true
}
