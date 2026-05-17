package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.model.Product
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ProductItemsContent(
    products: List<Product>,
    actions: SwipeItemActions,
    onMove: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
    isDragEnabled: Boolean = false
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onMove(from.index, to.index)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(0.dp)
    ) {
        items(
            items = products,
            key = { it.id }
        ) { product ->
            if (isDragEnabled) {
                ReorderableItem(reorderableLazyListState, key = product.id) { isDragging ->
                    val elevation by animateDpAsState(
                        targetValue = if (isDragging) 8.dp else 0.dp,
                        label = "elevation"
                    )

                    SwipeProductItemCard(
                        product = product,
                        actions = actions,
                        modifier = Modifier.shadow(elevation),
                        dragHandle = {
                            Image(
                                painter = painterResource(
                                    id = R.drawable.drag_handle
                                ),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                                contentDescription = null,
                                modifier = Modifier.draggableHandle()
                            )

                        }
                    )
                }
            } else {
                SwipeProductItemCard(
                    product = product,
                    actions = actions
                )
            }
        }
    }
}
