package ru.ya.practicum.shopper.feature.onboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.ya.practicum.shopper.feature.onboard.components.OnboardContent

@Composable
fun OnboardScreen(
    viewModel: OnboardViewModel,
    onNavigateToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        OnboardContent(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onStartClick() }
        )
    }
}
