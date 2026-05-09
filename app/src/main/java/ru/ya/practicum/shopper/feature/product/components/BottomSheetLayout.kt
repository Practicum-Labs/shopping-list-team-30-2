package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun DimmedBackground(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
    )
}

@Composable
fun BottomSheetContent(
    onSheetPositioned: (Float) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coords ->
                    onSheetPositioned(coords.boundsInRoot().top)
                }
        ) {
            BottomSheetDragHandle()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
fun AddButtonOverlay(
    visible: Boolean,
    config: AddButtonConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (visible) {
        Box(
            modifier = modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = (config.sheetTopPx - config.offsetPx).toInt()
                    )
                }
                .padding(horizontal = 28.dp)
        ) {
            AddButton(
                enabled = config.isButtonEnabled,
                onClick = onClick
            )
        }
    }
}

@Composable
fun BottomSheetDragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}

data class AddButtonConfig(
    val sheetTopPx: Float,
    val offsetPx: Float,
    val isButtonEnabled: Boolean
)
