package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.core.model.Product

@Composable
fun ProductItemsContent(
    products: List<Product>,
    onItemClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(
            items = products,
            key = { it.id }
        ) { product ->
            ProductItemCard(
                product = product,
                onItemClick = onItemClick
            )
        }
    }
}
