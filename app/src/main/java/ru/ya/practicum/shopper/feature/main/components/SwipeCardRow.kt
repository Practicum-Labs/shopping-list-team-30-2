package ru.ya.practicum.shopper.feature.main.components

import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.core.ui.theme.Theme
import kotlin.math.roundToInt

private const val DELETE_PERCENT = 0.95f

@Composable
fun SwipeCardRow(
    shoppingList: ShoppingList,
    actions: SwipeCardActions
) {
    BoxWithConstraints {
        val state = rememberSwipeState(maxWidth, actions.onDelete, shoppingList)
        val isDeleteMode = state.currentValue == SwipeState.DELETED

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (isDeleteMode) {
                DeleteModeButton(onDelete = { actions.onDelete(shoppingList) })
            } else {
                ActionButtonsRow(
                    onRename = { actions.onRename(shoppingList) },
                    onCopy = { actions.onCopy(shoppingList) },
                    onDelete = { actions.onDelete(shoppingList) }
                )
            }

            DraggableShoppingListCard(
                shoppingList = shoppingList,
                onClick = actions.onClick,
                onIconClick = actions.onIconClick,
                state = state
            )
        }
    }
}

@Composable
private fun rememberSwipeState(
    maxWidth: Dp,
    onDelete: (ShoppingList) -> Unit,
    shoppingList: ShoppingList
): AnchoredDraggableState<SwipeState> {
    val buttonWidthPx = with(LocalDensity.current) { Dimens.dp180.toPx() }
    val fullWidthPx = with(LocalDensity.current) { (maxWidth * DELETE_PERCENT).toPx() }

    val state = remember {
        AnchoredDraggableState(
            initialValue = SwipeState.CLOSED,
            anchors = DraggableAnchors {
                SwipeState.CLOSED at 0f
                SwipeState.BUTTONS at -buttonWidthPx
                SwipeState.DELETED at -fullWidthPx
            }
        )
    }

    LaunchedEffect(state.currentValue) {
        if (state.currentValue == SwipeState.DELETED) {
            onDelete(shoppingList)
        }
    }

    return state
}

@Composable
private fun DeleteModeButton(
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.dp4),
        contentAlignment = Alignment.Center
    ) {
        FilledIconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(Dimens.dp40)
                .size(Dimens.dp40),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_swiped_delete),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiaryContainer),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    onRename: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = Dimens.dp20),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            onClick = onRename,
            iconRes = R.drawable.ic_swiped_rename
        )
        Spacer(modifier = Modifier.width(Dimens.dp4))
        ActionButton(
            onClick = onCopy,
            iconRes = R.drawable.ic_swiped_copy
        )
        Spacer(modifier = Modifier.width(Dimens.dp4))
        ActionButton(
            onClick = onDelete,
            iconRes = R.drawable.ic_swiped_delete
        )
    }
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

@Composable
private fun DraggableShoppingListCard(
    shoppingList: ShoppingList,
    onClick: (ShoppingList) -> Unit,
    onIconClick: ((ShoppingList) -> Unit)?,
    state: AnchoredDraggableState<SwipeState>
) {
    ShoppingListCard(
        shoppingList = shoppingList,
        onClick = onClick,
        onIconClick = onIconClick,
        modifier = Modifier
            .fillMaxWidth()
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

data class SwipeCardActions(
    val onClick: (ShoppingList) -> Unit,
    val onIconClick: ((ShoppingList) -> Unit)? = null,
    val onDelete: (ShoppingList) -> Unit,
    val onCopy: (ShoppingList) -> Unit,
    val onRename: (ShoppingList) -> Unit
)

enum class SwipeState { CLOSED, BUTTONS, DELETED }

@Preview(
    name = "Swipe Card Row - Closed",
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true,
    showBackground = true
)
@Composable
fun PreviewSwipeCardRow_Closed() {
    Theme(darkTheme = false) {
        SwipeCardRow(
            ShoppingList(
                id = 1,
                name = "Авто",
                iconResId = R.drawable.ic_car,
            ),
            actions = SwipeCardActions({}, {}, {}, {}, {})
        )
    }
}
