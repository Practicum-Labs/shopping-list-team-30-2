package ru.ya.practicum.shopper.feature.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.core.ui.theme.Dimens

@Composable
fun ShoppingListsContent(
    lists: List<ShoppingList>,
    onListClick: (ShoppingList) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.dp16)
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(Dimens.dp16)
    ) {
        items(
            items = lists,
            key = { it.id }
        ) { shoppingList ->
            ShoppingListCard(
                shoppingList = shoppingList,
                onClick = onListClick
            )
        }
    }
}