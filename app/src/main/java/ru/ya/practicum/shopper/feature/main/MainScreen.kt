package ru.ya.practicum.shopper.feature.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.ya.practicum.shopper.core.ui.AddListDialog
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.feature.main.components.MainCreateList
import ru.ya.practicum.shopper.feature.main.components.MainEmptyContent
import ru.ya.practicum.shopper.feature.main.components.MainTopBar
import ru.ya.practicum.shopper.feature.main.components.ShoppingListsContent

val tmpList = listOf(
    ru.ya.practicum.shopper.core.model.ShoppingList(
        id = 1,
        name = "Продукты",
        iconResId = ru.ya.practicum.shopper.R.drawable.ic_car
    ),
    ru.ya.practicum.shopper.core.model.ShoppingList(
        id = 2,
        name = "Аптечка",
        iconResId = ru.ya.practicum.shopper.R.drawable.ic_aid_kit
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToProduct: (listId: Int, listName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddListDialog by remember { mutableStateOf(false) }

    // Временные данные для демонстрации
    val shoppingLists = remember {
        tmpList
    }

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
            MainCreateList(onClick = { showAddListDialog = true })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        if (shoppingLists.isEmpty()) {
            MainEmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.dp16),
            )
        } else {
            ShoppingListsContent(
                lists = shoppingLists,
                onListClick = { shoppingList ->
                    onNavigateToProduct(shoppingList.id, shoppingList.name)
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (showAddListDialog) {
        AddListDialog(
            onDismiss = { showAddListDialog = false },
            onCreate = { listName ->
                showAddListDialog = false
            }
        )
    }
}
