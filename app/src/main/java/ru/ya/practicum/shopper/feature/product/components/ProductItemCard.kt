package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.model.Product
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import kotlin.math.roundToInt

@Composable
fun ProductItemCard(
    product: Product,
    onItemClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    dragHandle: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        ProductItem(product, onItemClick, dragHandle)
    }
}

@Composable
fun SwipeProductItemCard(
    product: Product,
    actions: SwipeItemActions,
    modifier: Modifier = Modifier,
    dragHandle: (@Composable () -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val state = rememberSwipeState()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        ActionButtonsRow(
            state = state,
            onRename = { actions.onRename(product) },
            onDelete = { actions.onDelete(product) },
            modifier = Modifier.matchParentSize()
        )
        ProductItemCard(
            product = product,
            onItemClick = actions.onItemClick,
            dragHandle = dragHandle,
            modifier = Modifier
                .offset { IntOffset(state.offset.roundToInt(), 0) }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal,
                    flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                        state = state,
                        positionalThreshold = { it * 0.5f },
                        animationSpec = spring()
                    )
                )
        )
    }

}

private fun formatAmount(amount: String, unit: String): String {
    return if (unit.isNotBlank()) "$amount $unit" else amount
}

@Composable
fun ProductItem(
    product: Product,
    onItemClick: (Product) -> Unit,
    dragHandle: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(product) }
            .padding(
                horizontal = Dimens.dp16,
                vertical = Dimens.dp16
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(
                id = if (product.isBought) R.drawable.check_on else R.drawable.check_off
            ),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(Dimens.dp16))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (product.isBought) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textDecoration = if (product.isBought) {
                    TextDecoration.LineThrough
                } else {
                    null
                }
            )

            Text(
                text = formatAmount(product.amount, product.unit),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(Dimens.dp16))

        dragHandle?.invoke()
    }
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

@Composable
private fun ActionButtonsRow(
    state: AnchoredDraggableState<ProductSwipeState>,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()

    fun resetAndAction(action: () -> Unit) {
        scope.launch {
            state.animateTo(ProductSwipeState.CLOSED)
        }
        action()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = Dimens.dp20),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            onClick = { resetAndAction(onRename) },
            iconRes = R.drawable.ic_swiped_rename
        )
        Spacer(modifier = Modifier.width(Dimens.dp4))
        ActionButton(
            onClick = { resetAndAction(onDelete) },
            iconRes = R.drawable.ic_swiped_delete
        )
    }
}

data class SwipeItemActions(
    val onItemClick: (Product) -> Unit,
    val onDelete: (Product) -> Unit,
    val onRename: (Product) -> Unit,
)

enum class ProductSwipeState { CLOSED, BUTTONS }

@Composable
private fun rememberSwipeState(): AnchoredDraggableState<ProductSwipeState> {
    val buttonWidthPx = with(LocalDensity.current) { Dimens.dp128.toPx() }

    val state = remember {
        AnchoredDraggableState(
            initialValue = ProductSwipeState.CLOSED,
            anchors = DraggableAnchors {
                ProductSwipeState.CLOSED at 0f
                ProductSwipeState.BUTTONS at -buttonWidthPx
            }
        )
    }
    return state
}

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    iconRes: Int
) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Image(
            painter = painterResource(id = iconRes),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiaryContainer),
            contentDescription = null,
        )
    }
}
