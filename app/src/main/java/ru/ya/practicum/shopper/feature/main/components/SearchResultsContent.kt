package ru.ya.practicum.shopper.feature.main.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.ya.practicum.shopper.core.model.ShoppingList

@Composable
fun SearchResultsContent(
    lists: List<ShoppingList>,
    onListClick: (ShoppingList) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            count = lists.size,
            key = { lists[it].id }
        ) { index ->
            SearchResultItem(
                shoppingList = lists[index],
                onClick = onListClick
            )
        }
    }
}
