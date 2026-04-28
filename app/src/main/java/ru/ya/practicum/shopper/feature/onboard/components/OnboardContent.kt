package ru.ya.practicum.shopper.feature.onboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.ya.practicum.shopper.core.ui.theme.Dimens

@Composable
fun OnboardContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.dp16))
        OnboardLogo()
        Spacer(modifier = Modifier.height(Dimens.dp94))
        OnboardContentImage()
        Spacer(modifier = Modifier.height(Dimens.dp48))
        OnboardMsgTitle()
        Spacer(modifier = Modifier.height(Dimens.dp8))
        OnboardMsgFirstLine()
        OnboardMsgSecondLine()
    }
}
