package ru.ya.practicum.shopper.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp

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
