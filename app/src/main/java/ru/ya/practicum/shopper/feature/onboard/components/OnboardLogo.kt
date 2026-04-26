package ru.ya.practicum.shopper.feature.onboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R

@Composable
fun OnboardLogo(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.onb_logo),
        contentDescription = null,
        modifier = modifier.padding(horizontal = 37.dp)
    )
}
