package ru.ya.practicum.shopper.feature.auth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }

    AuthEffectHandler(
        effect = viewModel.effect,
        snackbarHostState = snackbarHostState,
        onAuthSuccess = onAuthSuccess,
        onNavigateToSignUp = onNavigateToSignUp,
        onNavigateToRecovery = onNavigateToRecovery
    )

    AuthScaffold(
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        AuthFormContent(
            state = state,
            onIntent = viewModel::handleIntent,
            focusManager = focusManager,
            passwordFocusRequester = passwordFocusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun AuthEffectHandler(
    effect: kotlinx.coroutines.flow.Flow<AuthEffect>,
    snackbarHostState: SnackbarHostState,
    onAuthSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToRecovery: () -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collect { effect ->
            when (effect) {
                is AuthEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                AuthEffect.NavigateToMain -> onAuthSuccess()
                AuthEffect.NavigateToSignUp -> onNavigateToSignUp()
                AuthEffect.NavigateToRecovery -> onNavigateToRecovery()
            }
        }
    }
}

@Composable
private fun AuthScaffold(
    snackbarHostState: SnackbarHostState,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        content(paddingValues)
    }
}
