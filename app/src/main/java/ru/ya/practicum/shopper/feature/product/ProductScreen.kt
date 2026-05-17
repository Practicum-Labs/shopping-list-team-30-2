package ru.ya.practicum.shopper.feature.product

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.koinViewModel
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.feature.product.components.ChangeProductBottomSheet
import ru.ya.practicum.shopper.feature.product.components.ConfirmDeleteDialog
import ru.ya.practicum.shopper.feature.product.components.ProductAddBottomSheet
import ru.ya.practicum.shopper.feature.product.components.ProductBottomSheet
import ru.ya.practicum.shopper.feature.product.components.ProductBottomSheetCallBacks
import ru.ya.practicum.shopper.feature.product.components.ProductBottomSheetConfig
import ru.ya.practicum.shopper.feature.product.components.ProductCreateItem
import ru.ya.practicum.shopper.feature.product.components.ProductEmptyContent
import ru.ya.practicum.shopper.feature.product.components.ProductItemsContent
import ru.ya.practicum.shopper.feature.product.components.ProductTopBar
import ru.ya.practicum.shopper.feature.product.components.SwipeItemActions

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod", "LongParameterList")
@Composable
fun ProductScreen(
    listId: Int,
    listName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showAddProductSheet by remember { mutableStateOf(false) }
    var showProductBottomSheet by remember { mutableStateOf(false) }
    var showDialogDeleteAll by remember { mutableStateOf(false) }
    var showDialogClearBought by remember { mutableStateOf(false) }
    var showDialogDeleteItem by remember { mutableStateOf(false) }
    var showChangeBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onIntent(ProductIntent.LoadProducts(listId))
    }

    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                is ProductEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            actions = SwipeItemActions(
                onItemClick = { product ->
                    viewModel.onIntent(ProductIntent.ToggleProductBought(product, listId))
                },
                onDelete = { product ->
                    viewModel.onIntent(ProductIntent.SetDeletedProduct(product))
                    showDialogDeleteItem = true
                },
                onRename = { product ->
                    viewModel.onIntent(ProductIntent.SetChangeProduct(product = product))
                    showChangeBottomSheet = true
                }
            ),
            onMove = { from, to ->
                viewModel.onIntent(ProductIntent.OnMove(from, to))
            }
        )
    }

    if (showChangeBottomSheet) {
        ChangeProductBottomSheet(
            onDismiss = { newData ->
                viewModel.onIntent(ProductIntent.ChangeProduct(newData = newData))
                showChangeBottomSheet = false
            },
            productState = state.productToChange
        )
    }

    if (showDialogDeleteItem) {
        ConfirmDeleteDialog(
            stringResource(
                R.string.delete_item_text,
                state.productToDelete?.name ?: ""
            ),
            { showDialogDeleteItem = false },
            {
                state.productToDelete?.let { viewModel.onIntent(ProductIntent.DeleteProduct(it.id)) }
                showDialogDeleteItem = false
            }
        )
    }

    if (showAddProductSheet) {
        ProductAddBottomSheet(
            onDismiss = { showAddProductSheet = false },
            onAddProduct = { name, quantity, unit ->
                viewModel.onIntent(
                    ProductIntent.AddProduct(
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
            config = ProductBottomSheetConfig(
                sheetState = sheetState,
                state = state,
                onDismissRequest = { showProductBottomSheet = false },
                onDeleteAll = {
                    showDialogDeleteAll = true
                    showProductBottomSheet = false
                },
                onClearBought = {
                    showDialogClearBought = true
                    showProductBottomSheet = false
                },
                callBacks = ProductBottomSheetCallBacks(
                    onSortByABC = { viewModel.onIntent(ProductIntent.ChangeSorting(true)) },
                    onSortByUserPref = { viewModel.onIntent(ProductIntent.ChangeSorting(false)) }
                )
            )
        )
    }

    if (showDialogDeleteAll) {
        ConfirmDeleteDialog(
            stringResource(R.string.deleteAllConfirm),
            { showDialogDeleteAll = false },
            {
                viewModel.onIntent(ProductIntent.DeleteAllProducts)
                showDialogDeleteAll = false
            }
        )
    }

    if (showDialogClearBought) {
        ConfirmDeleteDialog(
            stringResource(R.string.clearBoughtConfirm),
            { showDialogClearBought = false },
            {
                viewModel.onIntent(ProductIntent.ClearBoughtProducts)
                showDialogClearBought = false
            }
        )
    }
}

@Composable
private fun ProductScreenContent(
    state: ProductViewState,
    innerPadding: PaddingValues,
    actions: SwipeItemActions,
    onMove: (from: Int, to: Int) -> Unit,
) {
    if (state.products.isEmpty() && !state.isLoading) {
        ProductEmptyContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.dp16)
        )
    } else {
        ProductItemsContent(
            products = state.products,
            actions = actions,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onMove = onMove,
            isDragEnabled = !state.sortingByName
        )
    }
}
