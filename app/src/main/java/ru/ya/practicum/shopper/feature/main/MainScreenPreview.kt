package ru.ya.practicum.shopper.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.ya.practicum.shopper.core.ui.DefaultPreviewContainer

@Preview
@Composable
fun MainScreenPreviewLight() {
    DefaultPreviewContainer { MainScreen() }
}

@Preview
@Composable
fun MainScreenPreviewDark() {
    DefaultPreviewContainer(darkTheme = true) { MainScreen() }
}
