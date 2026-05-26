package ru.ya.practicum.shopper.feature.onboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.ya.practicum.shopper.core.ui.DefaultPreviewContainer

@Preview
@Composable
private fun ScreenPreviewLight() {
    DefaultPreviewContainer() {
        OnboardScreenContent(
            onStartClick = {}
        )
    }
}

@Preview
@Composable
private fun ScreenPreviewLightDark() {
    DefaultPreviewContainer(
        darkTheme = true
    ) {
        OnboardScreenContent(
            onStartClick = {}
        )
    }
}
