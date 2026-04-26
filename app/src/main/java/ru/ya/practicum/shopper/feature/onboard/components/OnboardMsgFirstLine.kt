package ru.ya.practicum.shopper.feature.onboard.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardMsgFirstLine(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Создавайте списки, добавляйте товары,",
        modifier = modifier
    )
}
