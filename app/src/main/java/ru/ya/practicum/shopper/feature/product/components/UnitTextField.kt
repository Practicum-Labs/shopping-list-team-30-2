package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R

@Composable
fun UnitTextField(
    selectedUnit: String,
    isExpanded: Boolean
) {
    var isFocused by remember { mutableStateOf(false) }
    val colors = rememberTextFieldColors(isFocused, selectedUnit.isEmpty())

    OutlinedTextField(
        value = selectedUnit,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        label = {
            Surface(color = colors.first) {
                Text(
                    text = stringResource(R.string.units),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.second,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        trailingIcon = {
            Icon(
                painter = painterResource(
                    id = if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
                ),
                contentDescription = if (isExpanded) {
                    stringResource(R.string.collapse)
                } else {
                    stringResource(R.string.extract)
                },
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun rememberTextFieldColors(
    isFocused: Boolean,
    isEmpty: Boolean
): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> {
    val surfaceColor = when {
        isFocused -> MaterialTheme.colorScheme.surface
        isEmpty -> MaterialTheme.colorScheme.surfaceContainerLow
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isFocused -> MaterialTheme.colorScheme.secondary
        isEmpty -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.secondary
    }
    return Pair(surfaceColor, textColor)
}
