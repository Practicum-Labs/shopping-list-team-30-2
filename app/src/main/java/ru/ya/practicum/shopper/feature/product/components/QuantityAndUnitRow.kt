package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuantityAndUnitRow(
    state: ProductAddBottomSheetState,
    onStateChange: (ProductAddBottomSheetState) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuantityField(
            quantity = state.quantity,
            onQuantityChange = { onStateChange(state.copy(quantity = it)) },
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        UnitDropdownField(
            selectedUnit = state.selectedUnit,
            onUnitSelected = { onStateChange(state.copy(selectedUnit = it)) },
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        QuantityButton(
            quantity = state.quantity,
            isIncrement = false,
            onQuantityChange = { onStateChange(state.copy(quantity = it)) },
        )
        QuantityButton(
            quantity = state.quantity,
            isIncrement = true,
            onQuantityChange = { onStateChange(state.copy(quantity = it)) }
        )
    }
}
