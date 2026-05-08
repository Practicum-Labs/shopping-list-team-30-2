package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun UnitDropdownMenu(
    expanded: Boolean,
    units: List<String>,
    onDismiss: () -> Unit,
    onUnitSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(8.dp)
            ),
        shadowElevation = 0.dp
    ) {
        Box(modifier = Modifier.heightIn(max = 144.dp)) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(end = 18.dp)
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit, color = MaterialTheme.colorScheme.onSurface) },
                        onClick = { onUnitSelected(unit) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                    )
                }
            }

            if (scrollState.maxValue > 0) {
                Scrollbar(
                    scrollState = scrollState,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
private fun Scrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val trackHeight = 130.dp
    val thumbHeight = 14.dp
    val thumbOffset = calculateThumbOffset(scrollState, trackHeight, thumbHeight)

    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .width(14.dp)
            .height(trackHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(thumbOffset))
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                .align(Alignment.CenterHorizontally)
        )
    }
}

private fun calculateThumbOffset(
    scrollState: ScrollState,
    trackHeight: Dp,
    thumbHeight: Dp
): Dp {
    if (scrollState.maxValue == 0) return 0.dp
    val scrollRange = scrollState.maxValue.toFloat()
    val currentScroll = scrollState.value.toFloat()
    val trackScrollRange = trackHeight.value - thumbHeight.value
    return ((currentScroll / scrollRange) * trackScrollRange).dp
}
