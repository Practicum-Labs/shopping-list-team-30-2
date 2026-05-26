package ru.ya.practicum.shopper.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R

@Composable
fun AuthFormContent(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager,
    passwordFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val (emailError, passwordError, isFormValid) = validateAuthState(state)

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthHeader()
        Spacer(modifier = Modifier.height(32.dp))

        AuthEmailField(
            email = state.email,
            onEmailChange = { onIntent(AuthIntent.UpdateEmail(it)) },
            error = emailError,
            onNext = { passwordFocusRequester.requestFocus() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthPasswordField(
            password = state.password,
            onPasswordChange = { onIntent(AuthIntent.UpdatePassword(it)) },
            error = passwordError,
            onSubmit = {
                focusManager.clearFocus()
                if (isFormValid) onIntent(AuthIntent.Submit)
            },
            focusRequester = passwordFocusRequester
        )

        AuthErrorText(error = state.error, isLoading = state.isLoading)
        Spacer(modifier = Modifier.height(24.dp))

        AuthLoginButton(
            isLoading = state.isLoading,
            isFormValid = isFormValid,
            onSubmit = { onIntent(AuthIntent.Submit) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthNavigationButtons(
            onNavigateToSignUp = { onIntent(AuthIntent.NavigateToSignUp) },
            onNavigateToRecovery = { onIntent(AuthIntent.NavigateToRecovery) }
        )
    }
}

@Composable
fun AuthHeader() {
    Text(
        text = stringResource(R.string.entrance),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun AuthEmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    error: String?,
    onNext: () -> Unit
) {
    EmailField(
        value = email,
        onValueChange = onEmailChange,
        error = error,
        onNext = onNext
    )
}

@Composable
fun AuthPasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    error: String?,
    onSubmit: () -> Unit,
    focusRequester: FocusRequester
) {
    PasswordField(
        value = password,
        onValueChange = onPasswordChange,
        error = error,
        onSubmit = onSubmit,
        modifier = Modifier.focusRequester(focusRequester)
    )
}

@Composable
fun AuthErrorText(error: String?, isLoading: Boolean) {
    if (error != null && !isLoading) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AuthLoginButton(
    isLoading: Boolean,
    isFormValid: Boolean,
    onSubmit: () -> Unit
) {
    SubmitButton(
        isLoading = isLoading,
        text = stringResource(R.string.login),
        onClick = onSubmit,
        enabled = isFormValid
    )
}

@Composable
fun AuthNavigationButtons(
    onNavigateToSignUp: () -> Unit,
    onNavigateToRecovery: () -> Unit
) {
    AuthTextButton(
        text = stringResource(R.string.no_account_register),
        onClick = onNavigateToSignUp
    )
    AuthTextButton(
        text = stringResource(R.string.forgot_password),
        onClick = onNavigateToRecovery
    )
}
