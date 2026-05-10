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

data class SignUpFormDependencies(
    val state: SignUpState,
    val onIntent: (SignUpIntent) -> Unit,
    val focusManager: androidx.compose.ui.focus.FocusManager,
    val passwordFocusRequester: FocusRequester,
    val confirmPasswordFocusRequester: FocusRequester,
    val modifier: Modifier = Modifier
)
