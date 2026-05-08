package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R

@Composable
fun QuantityField(
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
                    style = MaterialTheme.typography.bodyLarge,
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
): Pair<Color, Color> {
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
