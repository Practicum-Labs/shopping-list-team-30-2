package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAddBottomSheet(
    onDismiss: () -> Unit,
    onAddProduct: (name: String, quantity: String, unit: String) -> Unit
) {
    var state by remember { mutableStateOf(ProductAddBottomSheetState()) }

    val isButtonEnabled =
        state.productName.isNotBlank() && state.quantity.isNotBlank() && (state.quantity.toIntOrNull()
            ?: 0) > 0

    val density = LocalDensity.current
    var sheetTopPx by remember { mutableStateOf(0f) }
    val offsetPx = with(density) { 100.dp.toPx() }

    val buttonConfig = AddButtonConfig(
        sheetTopPx = sheetTopPx,
        offsetPx = offsetPx,
        isButtonEnabled = isButtonEnabled
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        DimmedBackground(onDismiss)

        BottomSheetContent(
            onSheetPositioned = { sheetTopPx = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ProductFormFields(
                state = state,
                onStateChange = { state = it }
            )
        }

        AddButtonOverlay(
            visible = sheetTopPx > 0f,
            config = buttonConfig,
            onClick = {
                onAddProduct(
                    state.productName,
                    state.quantity,
                    state.selectedUnit
                )
            },
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}
