package ru.ya.practicum.shopper.feature.auth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.koinViewModel
import ru.ya.practicum.shopper.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }

    SignUpEffectHandler(
        effect = viewModel.effect,
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onRegistrationSuccess = onRegistrationSuccess
    )

    SignUpScaffold(
        onBackClick = { viewModel.handleIntent(SignUpIntent.NavigateBack) },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        SignUpFormContent(
            deps = SignUpFormDependencies(
                state = state,
                onIntent = viewModel::handleIntent,
                focusManager = focusManager,
                passwordFocusRequester = passwordFocusRequester,
                confirmPasswordFocusRequester = confirmPasswordFocusRequester,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        )
    }
}

@Composable
private fun SignUpEffectHandler(
    effect: kotlinx.coroutines.flow.Flow<SignUpEffect>,
    state: SignUpState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collect { effect ->
            when (effect) {
                is SignUpEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                SignUpEffect.NavigateToAuth -> onBackClick()
                SignUpEffect.RegistrationSuccess -> onRegistrationSuccess()
            }
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegistrationSuccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpScaffold(
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.registration)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = stringResource(R.string.cd_back)
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
