package ru.ya.practicum.shopper.feature.product

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import ru.ya.practicum.shopper.core.model.Product
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.feature.product.components.ProductAddBottomSheet
import ru.ya.practicum.shopper.feature.product.components.ProductBottomSheet
import ru.ya.practicum.shopper.feature.product.components.ProductBottomSheetCallBacks
import ru.ya.practicum.shopper.feature.product.components.ProductCreateItem
import ru.ya.practicum.shopper.feature.product.components.ProductEmptyContent
import ru.ya.practicum.shopper.feature.product.components.ProductItemsContent
import ru.ya.practicum.shopper.feature.product.components.ProductTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    listId: Int,
    listName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = koinViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddProductDialog by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    var showAddProductSheet by remember { mutableStateOf(false) }
    var showProductBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(listId) {
        viewModel.onEvent(ProductEvent.LoadProducts(listId))
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            ProductTopBar(
                title = listName,
                onBackClick = onBackClick,
                onMenuClick = { showProductBottomSheet = true }
            )
        },
        floatingActionButton = {
            ProductCreateItem(onClick = { showAddProductSheet = true })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        ProductScreenContent(
            state = state,
            innerPadding = innerPadding,
            onProductClick = { product ->
                viewModel.onEvent(ProductEvent.ToggleBought(product, listId))
            }
        )
    }

    if (showAddProductSheet) {
        ProductAddBottomSheet(
            onDismiss = { showAddProductSheet = false },
            onAddProduct = { name, quantity, unit ->
                viewModel.onEvent(
                    ProductEvent.AddProduct(
                        name = name,
                        quantity = quantity,
                        unit = unit,
                        listId = listId
                    )
                )
                showAddProductSheet = false
            }
        )
    }

    if (showProductBottomSheet) {
        ProductBottomSheet(
            sheetState,
            {showProductBottomSheet = false},
            bottomSheetCallBacks(viewModel)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductScreenContent(
    state: ProductState,
    innerPadding: PaddingValues,
    onProductClick: (Product) -> Unit
) {
    if (state.products.isEmpty()) {
        ProductEmptyContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.dp16)
        )
    } else {
        ProductItemsContent(
            products = state.products,
            onItemClick = onProductClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

fun bottomSheetCallBacks(viewModel: ProductViewModel): ProductBottomSheetCallBacks {
    return ProductBottomSheetCallBacks(
        viewModel::sortProductsByABC,
        viewModel::sortProductByUserPref,
        viewModel::deleteAllProducts,
        viewModel::clearBoughtProducts
    )
}
