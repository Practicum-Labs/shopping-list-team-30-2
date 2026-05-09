package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.ya.practicum.shopper.R

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
