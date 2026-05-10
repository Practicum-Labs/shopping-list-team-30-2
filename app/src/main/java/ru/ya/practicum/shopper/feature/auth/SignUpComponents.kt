package ru.ya.practicum.shopper.feature.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R

@Composable
fun SignUpHeader() {
    Text(
        text = stringResource(R.string.registration),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun SignUpEmailField(
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
fun SignUpPasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    error: String?,
    onNext: () -> Unit,
    focusRequester: FocusRequester
) {
    PasswordField(
        value = password,
        onValueChange = onPasswordChange,
        error = error,
        onSubmit = onNext,
        modifier = Modifier.focusRequester(focusRequester)
    )
}

@Composable
fun SignUpConfirmPasswordField(
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    error: String?,
    onSubmit: () -> Unit,
    focusRequester: FocusRequester
) {
    ConfirmPasswordField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        error = error,
        onSubmit = onSubmit,
        modifier = Modifier.focusRequester(focusRequester)
    )
}

@Composable
fun SignUpGeneralError(error: String?) {
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SignUpSubmitButton(
    isLoading: Boolean,
    isFormValid: Boolean,
    onSubmit: () -> Unit
) {
    SubmitButton(
        isLoading = isLoading,
        text = stringResource(R.string.sign_in),
        onClick = onSubmit,
        enabled = isFormValid
    )
}
