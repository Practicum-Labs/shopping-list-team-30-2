package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProductFormFields(
    state: ProductAddBottomSheetState,
    onStateChange: (ProductAddBottomSheetState) -> Unit
) {
    ProductNameField(
        productName = state.productName,
        onProductNameChange = { onStateChange(state.copy(productName = it)) }
    )

    Spacer(modifier = Modifier.height(24.dp))

    QuantityAndUnitRow(
        state = state,
        onStateChange = onStateChange
    )

    Spacer(modifier = Modifier.height(32.dp))
}
