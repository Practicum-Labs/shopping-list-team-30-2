package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.theme.Dimens

@Composable
fun QuantityButton(
    quantity: String,
    isIncrement: Boolean,
    onQuantityChange: (String) -> Unit
) {
    val currentQuantity = quantity.toIntOrNull() ?: 0
    val isEnabled = if (isIncrement) true else currentQuantity > 0

    FloatingActionButton(
        onClick = {
            if (isEnabled) {
                val newQuantity = if (isIncrement) {
                    currentQuantity + 1
                } else {
                    currentQuantity - 1
                }
                onQuantityChange(newQuantity.toString())
            }
        },
        modifier = Modifier
            .size(48.dp)
            .offset(y = 4.dp),
        shape = RoundedCornerShape(Dimens.ROUNDED_CORNER_SHAPE_100_P),
        containerColor = if (isEnabled) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        },
        contentColor = if (isEnabled) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        },
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Icon(
            painter = painterResource(
                id = if (isIncrement) R.drawable.plus else R.drawable.minus
            ),
            contentDescription = null,
            modifier = Modifier.size(if (isIncrement) 24.dp else 12.dp)
        )
    }
}
