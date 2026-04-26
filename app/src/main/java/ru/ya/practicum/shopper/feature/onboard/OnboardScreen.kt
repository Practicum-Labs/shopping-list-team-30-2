package ru.ya.practicum.shopper.feature.onboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import ru.ya.practicum.shopper.feature.onboard.components.OnboardContent

const val AUTO_MAIN_SCREEN = 3000L

@Composable
fun OnboardScreen(
    viewModel: OnboardViewModel,
    onNavigateToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(AUTO_MAIN_SCREEN)
        if (!state.isNavigatingToMain) {
            viewModel.onEvent(OnboardEvent.OnStartClicked)
        }
    }

    LaunchedEffect(state.isNavigatingToMain) {
        if (state.isNavigatingToMain) {
            onNavigateToMain()
        }
    }

    OnboardScreenContent(
        onStartClick = { viewModel.onEvent(OnboardEvent.OnStartClicked) },
        modifier = modifier
    )
}

@Composable
fun OnboardScreenContent(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardContent(
        modifier = modifier
            .fillMaxSize()
            .clickable { onStartClick() }
    )
}
