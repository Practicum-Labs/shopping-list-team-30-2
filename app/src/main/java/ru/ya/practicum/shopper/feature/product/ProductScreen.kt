package ru.ya.practicum.shopper.feature.product

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.feature.product.components.ProductCreateItem
import ru.ya.practicum.shopper.feature.product.components.ProductEmptyContent
import ru.ya.practicum.shopper.feature.product.components.ProductTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            ProductTopBar(
                title = stringResource(R.string.product_title),
                onBackClick = { },
                onMenuClick = { }
            )
        },
        floatingActionButton = {
            ProductCreateItem(onClick = { })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        ProductEmptyContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.dp16)
        )
    }
}
