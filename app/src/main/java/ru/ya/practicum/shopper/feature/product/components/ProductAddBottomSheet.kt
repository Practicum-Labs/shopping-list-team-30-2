package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAddBottomSheet(
    onDismiss: () -> Unit,
    onAddProduct: (name: String, quantity: String, unit: String) -> Unit
) {
    var state by remember { mutableStateOf(ProductAddBottomSheetState()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = { BottomSheetDragHandle() },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        BottomSheetContent(
            state = state,
            onStateChange = { state = it },
            onAddProduct = onAddProduct
        )
    }
}

@Composable
private fun BottomSheetContent(
    state: ProductAddBottomSheetState,
    onStateChange: (ProductAddBottomSheetState) -> Unit,
    onAddProduct: (name: String, quantity: String, unit: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        ProductNameField(
            productName = state.productName,
            onProductNameChange = { onStateChange(state.copy(productName = it)) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        QuantityAndUnitRow(state = state, onStateChange = onStateChange)
        Spacer(modifier = Modifier.height(16.dp))
        AddButton(
            enabled = state.productName.isNotBlank() && state.quantity.isNotBlank() && (state.quantity.toIntOrNull()
                ?: 0) > 0,
            onClick = {
                if (state.productName.isNotBlank() && state.quantity.isNotBlank() && (state.quantity.toIntOrNull()
                        ?: 0) > 0
                ) {
                    onAddProduct(state.productName, state.quantity, state.selectedUnit)
                }
            }
        )
    }
}

@Composable
private fun QuantityAndUnitRow(
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
            onQuantityChange = { onStateChange(state.copy(quantity = it)) }
        )
        QuantityButton(
            quantity = state.quantity,
            isIncrement = true,
            onQuantityChange = { onStateChange(state.copy(quantity = it)) }
        )
    }
}

@Composable
fun UnitDropdownField(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var unitDropdownExpanded by remember { mutableStateOf(false) }
    val units = listOf(
        stringResource(R.string.unit_pcs),
        stringResource(R.string.unit_kg),
        stringResource(R.string.unit_l),
        stringResource(R.string.unit_pack),
        stringResource(R.string.unit_g),
        stringResource(R.string.unit_ml)
    )

    Box(modifier = modifier) {
        UnitTextField(selectedUnit = selectedUnit, isExpanded = unitDropdownExpanded)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { unitDropdownExpanded = true }
        )
        UnitDropdownMenu(
            expanded = unitDropdownExpanded,
            units = units,
            onDismiss = { unitDropdownExpanded = false },
            onUnitSelected = {
                onUnitSelected(it)
                unitDropdownExpanded = false
            }
        )
    }
}

@Composable
private fun QuantityField(
    quantity: String,
    onQuantityChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val colors = rememberQuantityFieldColors(isFocused, quantity)

    OutlinedTextField(
        value = quantity,
        onValueChange = { newValue ->
            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                val sanitized = if (newValue.length > 1 && newValue.startsWith("0")) {
                    newValue.trimStart('0').ifEmpty { "0" }
                } else {
                    newValue
                }
                onQuantityChange(sanitized)
            }
        },
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
        label = {
            Surface(color = colors.first) {
                Text(
                    text = stringResource(R.string.quantity),
                    color = colors.second,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
private fun rememberQuantityFieldColors(
    isFocused: Boolean,
    quantity: String
): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> {
    val surfaceColor = when {
        isFocused -> MaterialTheme.colorScheme.surface
        quantity.isEmpty() -> MaterialTheme.colorScheme.surfaceContainerLow
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isFocused -> MaterialTheme.colorScheme.secondary
        quantity.isEmpty() -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.secondary
    }
    return Pair(surfaceColor, textColor)
}

@Composable
private fun BottomSheetDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .width(32.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(MaterialTheme.colorScheme.outline)
    )
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
private fun ProductBottomSheetPreview() {
    Theme {
        ProductAddBottomSheet(
            onDismiss = {},
            onAddProduct = { _, _, _ -> }
        )
    }
}
