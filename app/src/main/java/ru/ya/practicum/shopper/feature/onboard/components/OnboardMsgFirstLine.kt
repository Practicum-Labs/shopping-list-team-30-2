package ru.ya.practicum.shopper.feature.onboard.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.ya.practicum.shopper.R

@Composable
fun OnboardMsgFirstLine(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.onboard_body_first_line),
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )
}
