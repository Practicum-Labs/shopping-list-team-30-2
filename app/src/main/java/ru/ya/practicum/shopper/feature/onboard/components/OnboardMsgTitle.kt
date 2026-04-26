package ru.ya.practicum.shopper.feature.onboard.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardMsgTitle(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Добро пожаловать в Список покупок!",
        modifier = modifier
    )
}
