package ru.ya.practicum.shopper.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.ya.practicum.shopper.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryScreen(
    onBackClick: () -> Unit,
    viewModel: RecoveryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val emailFocusRequester = remember { FocusRequester() }
    var showSuccessDialog by remember { mutableStateOf(false) }

    RecoveryEffectHandler(
        effect = viewModel.effect,
        onNavigateToAuth = onBackClick,
        onRecoverySuccess = { showSuccessDialog = true }
    )

    if (showSuccessDialog) {
        SuccessDialog(
            email = state.email,
            onDismiss = {
                showSuccessDialog = false
                onBackClick()
            }
        )
    }

    RecoveryScaffold(
        snackbarHostState = snackbarHostState,
        onBackClick = { viewModel.handleIntent(RecoveryIntent.NavigateBack) }
    ) { paddingValues ->
        RecoveryForm(
            state = state,
            onIntent = viewModel::handleIntent,
            focusManager = focusManager,
            emailFocusRequester = emailFocusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun RecoveryEffectHandler(
    effect: kotlinx.coroutines.flow.Flow<RecoveryEffect>,
    onNavigateToAuth: () -> Unit,
    onRecoverySuccess: () -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collect { effect ->
            when (effect) {
                is RecoveryEffect.ShowError -> {
                }
                RecoveryEffect.NavigateToAuth -> onNavigateToAuth()
                RecoveryEffect.RecoverySuccess -> onRecoverySuccess()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecoveryScaffold(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.password_recovery),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Composable
private fun SuccessDialog(
    email: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        title = {
            Text(
                text = stringResource(R.string.recovery_success_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.recovery_success_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.recovery_success_instruction),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(stringResource(R.string.recovery_success_button))
            }
        }
    )
}

@Composable
private fun RecoveryForm(
    state: RecoveryState,
    onIntent: (RecoveryIntent) -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager,
    emailFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val isFormValid = AuthValidation.isEmailValid(state.email)

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RecoveryHeader()
        Spacer(modifier = Modifier.height(32.dp))
        RecoveryEmailField(
            state = state,
            onIntent = onIntent,
            emailFocusRequester = emailFocusRequester
        )
        RecoveryErrorText(error = state.generalError)
        Spacer(modifier = Modifier.height(24.dp))
        RecoverySubmitButton(
            isLoading = state.isLoading,
            isEnabled = isFormValid,
            onSubmit = {
                focusManager.clearFocus()
                onIntent(RecoveryIntent.Submit)
            }
        )
    }
}

@Composable
private fun RecoveryHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.password_recovery),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.recovery_instruction),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RecoveryEmailField(
    state: RecoveryState,
    onIntent: (RecoveryIntent) -> Unit,
    emailFocusRequester: FocusRequester
) {
    val emailError = if (state.email.isNotBlank() && !AuthValidation.isEmailValid(state.email)) {
        stringResource(R.string.error_email_invalid)
    } else {
        state.emailError
    }

    val isFormValid = AuthValidation.isEmailValid(state.email)

    EmailField(
        value = state.email,
        onValueChange = { onIntent(RecoveryIntent.UpdateEmail(it)) },
        error = emailError,
        onNext = {
            if (isFormValid) onIntent(RecoveryIntent.Submit)
        },
        modifier = Modifier.focusRequester(emailFocusRequester)
    )
}

@Composable
private fun RecoveryErrorText(error: String?) {
    if (error != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RecoverySubmitButton(
    isLoading: Boolean,
    isEnabled: Boolean,
    onSubmit: () -> Unit
) {
    Button(
        onClick = onSubmit,
        enabled = !isLoading && isEnabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = stringResource(R.string.send_recovery_email),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
