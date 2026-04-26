package ru.ya.practicum.shopper.feature.onboard.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.ya.practicum.shopper.R

@Composable
fun OnboardMsgTitle(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.onboard_body_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
    )
}
