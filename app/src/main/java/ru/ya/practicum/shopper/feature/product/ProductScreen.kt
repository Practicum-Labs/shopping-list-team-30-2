package ru.ya.practicum.shopper.feature.product

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.feature.product.components.ProductBottomSheet
import ru.ya.practicum.shopper.feature.product.components.ProductBottomSheetCallBacks
import ru.ya.practicum.shopper.feature.product.components.ProductCreateItem
import ru.ya.practicum.shopper.feature.product.components.ProductEmptyContent
import ru.ya.practicum.shopper.feature.product.components.ProductItemsContent
import ru.ya.practicum.shopper.feature.product.components.ProductTopBar

val tmpList = listOf(
    ru.ya.practicum.shopper.core.model.Product(
        id = 1,
        name = "Молоко",
        amount = "1",
        unit = "л",
        isBought = false
    ),
    ru.ya.practicum.shopper.core.model.Product(
        id = 2,
        name = "Хлеб",
        amount = "2",
        unit = "шт",
        isBought = false
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UnusedParameter") // Подавлено на текущий момент не требуется
@Composable
fun ProductScreen(
    listId: Int,
    listName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddProductDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val products = remember { tmpList }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            ProductTopBar(
                title = listName,
                onBackClick = onBackClick,
                onMenuClick = { }
            )
        },
        floatingActionButton = {
            ProductCreateItem(onClick = { showAddProductDialog = true })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        if (products.isEmpty()) {
            ProductEmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.dp16)
            )
        } else {
            ProductItemsContent(
                products = products,
                onItemClick = { product ->
                    {}
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)

            )
        }
        ProductBottomSheet(
            sheetState,
            ProductBottomSheetCallBacks(
                onDismissRequest = {},
                onSortByABC = {},
                onSortByUserPref = {},
                onDeleteAll = {},
                onClearBought = {}
            )
        )
    }
}
