package ru.ya.practicum.shopper.feature.main

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.koinInject
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.core.ui.AddListDialog
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.feature.main.components.IconsModalBottomSheet
import ru.ya.practicum.shopper.feature.main.components.MainCreateList
import ru.ya.practicum.shopper.feature.main.components.MainEmptyContent
import ru.ya.practicum.shopper.feature.main.components.MainTopBar
import ru.ya.practicum.shopper.feature.main.components.ShoppingListsContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToProduct: (listId: Int, listName: String) -> Unit,
    modifier: Modifier = Modifier,
    listRepository: ShopperListRepository = koinInject(),
    itemRepository: ShopperItemRepository = koinInject()
) {
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(listRepository, itemRepository)
    )

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    ErrorHandler(state.error, snackbarHostState) {
        viewModel.onEvent(MainEvent.LoadLists)
    }

    MainScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateToProduct = onNavigateToProduct,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
private fun ErrorHandler(
    error: String?,
    snackbarHostState: SnackbarHostState,
    onErrorShown: () -> Unit
) {
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            onErrorShown()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    state: MainState,
    snackbarHostState: SnackbarHostState,
    onNavigateToProduct: (listId: Int, listName: String) -> Unit,
    onEvent: (MainEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MainTopBar(
                onSearchClick = { /* TODO */ },
                onDeleteClick = { /* TODO */ },
                onThemeClick = { /* TODO */ }
            )
        },
        floatingActionButton = {
            MainCreateList(onClick = { onEvent(MainEvent.ShowAddDialog) })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        MainScreenBody(
            state = state,
            innerPadding = innerPadding,
            onNavigateToProduct = onNavigateToProduct,
            onListIconClick = { shoppingList ->
                onEvent(MainEvent.ShowIconPickerForList(shoppingList.id))
            }
        )
    }

    MainScreenDialogs(
        state = state,
        sheetState = sheetState,
        onEvent = onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenDialogs(
    state: MainState,
    sheetState: androidx.compose.material3.SheetState,
    onEvent: (MainEvent) -> Unit
) {
    if (state.showIconPicker && state.editingListId != null) {
        IconsModalBottomSheet(
            bottomSheetState = sheetState,
            onDismissRequest = { onEvent(MainEvent.HideIconPicker) },
            onIconClick = { iconResId ->
                state.editingListId?.let { listId ->
                    onEvent(MainEvent.UpdateListIcon(listId, iconResId))
                }
            }
        )
    }

    if (state.showAddDialog) {
        AddListDialog(
            onDismiss = { onEvent(MainEvent.HideAddDialog) },
            onCreate = { listName ->
                onEvent(
                    MainEvent.CreateList(
                        name = listName,
                        iconId = state.selectedIconId
                    )
                )
            }
        )
    }
}

@Composable
private fun MainScreenBody(
    state: MainState,
    innerPadding: PaddingValues,
    onNavigateToProduct: (listId: Int, listName: String) -> Unit,
    onListIconClick: (ShoppingList) -> Unit
) {
    when {
        state.isLoading -> {}
        state.lists.isEmpty() -> {
            MainEmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.dp16),
            )
        }
        else -> {
            ShoppingListsContent(
                lists = state.lists,
                onListClick = { shoppingList ->
                    onNavigateToProduct(shoppingList.id, shoppingList.name)
                },
                onListIconClick = onListIconClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

class MainViewModelFactory(
    private val listRepository: ShopperListRepository,
    private val itemRepository: ShopperItemRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(listRepository, itemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
