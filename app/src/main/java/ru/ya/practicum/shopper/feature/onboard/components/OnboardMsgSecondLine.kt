package ru.ya.practicum.shopper.feature.onboard.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardMsgSecondLine(
    modifier: Modifier = Modifier
) {
    Text(
        text = "отмечайте, что уже куплено",
        modifier = modifier
    )
}
