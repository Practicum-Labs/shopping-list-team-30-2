package ru.ya.practicum.shopper.feature.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.feature.main.components.MainCreateList
import ru.ya.practicum.shopper.feature.main.components.MainEmptyContent
import ru.ya.practicum.shopper.feature.main.components.MainTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            MainTopBar(
                onSearchClick = { },
                onDeleteClick = { },
                onThemeClick = { }
            )
        },
        floatingActionButton = {
            MainCreateList(onClick = { })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        MainEmptyContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.dp16),
        )
    }
}
