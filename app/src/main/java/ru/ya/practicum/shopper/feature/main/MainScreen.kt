package ru.ya.practicum.shopper.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.ya.practicum.shopper.core.ui.theme.Dimens

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.dp16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Мои списки",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(Dimens.dp32))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(Dimens.dp4)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.dp24),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "У вас пока нет списков",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(Dimens.dp16))

                Text(
                    text = "Нажмите на кнопку + ниже, чтобы создать свой первый список",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(
            onClick = { },

            modifier = Modifier.fillMaxWidth(Dimens.ZERO_FIVE)
        ) {
            Text("+", fontSize = Dimens.sp32)
        }
    }
}
