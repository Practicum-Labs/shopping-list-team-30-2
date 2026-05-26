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
fun SignUpFormContent(deps: SignUpFormDependencies) {
    val validationResult = validateSignUpState(deps.state)

    Column(
        modifier = deps.modifier
            .padding(horizontal = 24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SignUpHeader()
        Spacer(modifier = Modifier.height(32.dp))

        SignUpEmailField(
            email = deps.state.email,
            onEmailChange = { deps.onIntent(SignUpIntent.UpdateEmail(it)) },
            error = validationResult.emailError,
            onNext = { deps.passwordFocusRequester.requestFocus() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SignUpPasswordField(
            password = deps.state.password,
            onPasswordChange = { deps.onIntent(SignUpIntent.UpdatePassword(it)) },
            error = validationResult.passwordError,
            onNext = { deps.confirmPasswordFocusRequester.requestFocus() },
            focusRequester = deps.passwordFocusRequester
        )

        Spacer(modifier = Modifier.height(16.dp))

        SignUpConfirmPasswordField(
            confirmPassword = deps.state.confirmPassword,
            onConfirmPasswordChange = { deps.onIntent(SignUpIntent.UpdateConfirmPassword(it)) },
            error = validationResult.confirmError,
            onSubmit = {
                deps.focusManager.clearFocus()
                if (validationResult.isFormValid) deps.onIntent(SignUpIntent.Submit)
            },
            focusRequester = deps.confirmPasswordFocusRequester
        )

        SignUpGeneralError(error = deps.state.generalError)
        Spacer(modifier = Modifier.height(24.dp))

        SignUpSubmitButton(
            isLoading = deps.state.isLoading,
            isFormValid = validationResult.isFormValid,
            onSubmit = { deps.onIntent(SignUpIntent.Submit) }
        )
    }
}

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

data class SignUpFormDependencies(
    val state: SignUpState,
    val onIntent: (SignUpIntent) -> Unit,
    val focusManager: androidx.compose.ui.focus.FocusManager,
    val passwordFocusRequester: FocusRequester,
    val confirmPasswordFocusRequester: FocusRequester,
    val modifier: Modifier = Modifier
)
