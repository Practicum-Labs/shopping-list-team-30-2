package ru.ya.practicum.shopper.feature.onboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.theme.Dimens

@Composable
fun OnboardLogo(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.onb_logo),
        contentDescription = null,
        modifier = modifier.padding(horizontal = Dimens.dp38)
    )
}
