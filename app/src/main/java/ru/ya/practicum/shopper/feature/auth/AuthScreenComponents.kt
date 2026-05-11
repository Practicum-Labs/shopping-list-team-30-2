package ru.ya.practicum.shopper.feature.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import ru.ya.practicum.shopper.R

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
