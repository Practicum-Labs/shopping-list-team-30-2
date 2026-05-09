package ru.ya.practicum.shopper.feature.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.koinInject
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.core.ui.AddListDialog
import ru.ya.practicum.shopper.core.ui.DeleteAllListsDialog
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.repository.ShopperListRepository
import ru.ya.practicum.shopper.feature.main.components.IconsModalBottomSheet
import ru.ya.practicum.shopper.feature.main.components.MainCreateList
import ru.ya.practicum.shopper.feature.main.components.MainEmptyContent
import ru.ya.practicum.shopper.feature.main.components.MainTopBar
import ru.ya.practicum.shopper.feature.main.components.SearchResultsContent
import ru.ya.practicum.shopper.feature.main.components.SearchScreen
import ru.ya.practicum.shopper.feature.main.components.ShoppingListsContent
import ru.ya.practicum.shopper.feature.main.components.SwipeCardActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToProduct: (listId: Int, listName: String) -> Unit,
    onThemeToggle: () -> Unit,
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
        callbacks = MainScreenCallbacks(
            onNavigateToProduct = onNavigateToProduct,
            onThemeToggle = onThemeToggle,
            onEvent = viewModel::onEvent
        ),
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

data class MainScreenCallbacks(
    val onNavigateToProduct: (listId: Int, listName: String) -> Unit,
    val onThemeToggle: () -> Unit,
    val onEvent: (MainEvent) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    state: MainState,
    snackbarHostState: SnackbarHostState,
    callbacks: MainScreenCallbacks,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (state.isSearchActive) {
        SearchScreen(
            query = state.searchInput,
            onQueryChange = { callbacks.onEvent(MainEvent.UpdateSearchQuery(it)) },
            onClose = { callbacks.onEvent(MainEvent.CloseSearch) },
            onSearch = { callbacks.onEvent(MainEvent.PerformSearch) }
        ) {
            SearchScreenBody(
                state = state,
                onNavigateToProduct = callbacks.onNavigateToProduct,
                onListIconClick = { shoppingList ->
                    callbacks.onEvent(MainEvent.ShowIconPickerForList(shoppingList.id))
                }
            )
        }
    } else {
        MainScaffold(
            state = state,
            snackbarHostState = snackbarHostState,
            callbacks = callbacks,
            modifier = modifier
        )
        MainScreenDialogs(
            state = state,
            sheetState = sheetState,
            onEvent = callbacks.onEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(
    state: MainState,
    snackbarHostState: SnackbarHostState,
    callbacks: MainScreenCallbacks,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MainTopBar(
                onSearchClick = { callbacks.onEvent(MainEvent.ToggleSearch) },
                onDeleteClick = { callbacks.onEvent(MainEvent.ShowDeleteAllDialog) },
                onThemeClick = callbacks.onThemeToggle
            )
        },
        floatingActionButton = {
            MainCreateList(onClick = { callbacks.onEvent(MainEvent.ShowAddDialog) })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MainScreenBody(
                state = state,
                onNavigateToProduct = callbacks.onNavigateToProduct,
                onListIconClick = { shoppingList ->
                    callbacks.onEvent(MainEvent.ShowIconPickerForList(shoppingList.id))
                }
            )
        }
    }
}

@Composable
private fun SearchScreenBody(
    state: MainState,
    onNavigateToProduct: (listId: Int, listName: String) -> Unit,
    onListIconClick: (ShoppingList) -> Unit
) {
    val filteredLists = if (state.searchQuery.isBlank()) {
        state.lists
    } else {
        state.lists.filter {
            it.name.contains(state.searchQuery, ignoreCase = true)
        }
    }

    when {
        state.searchQuery.isNotBlank() && filteredLists.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                contentAlignment = Alignment.Center
            ) {
                SearchEmptyContent(
                    modifier = Modifier
                        .padding(horizontal = Dimens.dp16)
                )
            }
        }

        state.searchQuery.isNotBlank() -> {
            SearchResultsContent(
                lists = filteredLists,
                onListClick = { shoppingList ->
                    onNavigateToProduct(shoppingList.id, shoppingList.name)
                }
            )
        }

        else -> {
            ShoppingListsContent(
                lists = filteredLists,
                listActions = SwipeCardActions(
                    onClick = { shoppingList ->
                        onNavigateToProduct(shoppingList.id, shoppingList.name)
                    },
                    onIconClick = onListIconClick,
                    onDelete = {},
                    onCopy = {},
                    onRename = {}
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenDialogs(
    state: MainState,
    sheetState: androidx.compose.material3.SheetState,
    onEvent: (MainEvent) -> Unit
) {
    if (state.showDeleteAllDialog) {
        DeleteAllListsDialog(
            onDismiss = { onEvent(MainEvent.HideDeleteAllDialog) },
            onConfirm = { onEvent(MainEvent.ConfirmDeleteAll) }
        )
    }
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
    onNavigateToProduct: (listId: Int, listName: String) -> Unit,
    onListIconClick: (ShoppingList) -> Unit
) {
    val filteredLists = if (state.searchQuery.isBlank()) {
        state.lists
    } else {
        state.lists.filter {
            it.name.contains(state.searchQuery, ignoreCase = true)
        }
    }
    when {
        state.isLoading -> {}
        state.lists.isEmpty() -> {
            MainEmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.dp16),
            )
        }

        state.searchQuery.isNotBlank() && filteredLists.isEmpty() -> {
            SearchEmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.dp16),
            )
        }

        else -> {
            ShoppingListsContent(
                lists = filteredLists,
                listActions = SwipeCardActions(
                    onClick = { shoppingList ->
                        onNavigateToProduct(shoppingList.id, shoppingList.name)
                    },
                    onIconClick = onListIconClick,
                    onDelete = {},
                    onCopy = {},
                    onRename = {}
                )
            )
        }
    }
}

@Composable
private fun SearchEmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.no_list),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.search_not_found_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.search_not_found_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
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
