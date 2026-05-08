package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
fun ProductNameField(
    productName: String,
    onProductNameChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val labelSurfaceColor =
        when {
            isFocused -> MaterialTheme.colorScheme.surface
            productName.isEmpty() -> MaterialTheme.colorScheme.surfaceContainerLow
            else -> MaterialTheme.colorScheme.surface
        }

    val labelTextColor = when {
        isFocused -> MaterialTheme.colorScheme.secondary
        productName.isEmpty() -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.secondary
    }

    OutlinedTextField(
        value = productName,
        onValueChange = onProductNameChange,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        label = {
            Surface(color = labelSurfaceColor) {
                Text(
                    text = stringResource(R.string.product),
                    style = MaterialTheme.typography.bodyLarge,
                    color = labelTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        placeholder = {
            Text(
                stringResource(R.string.add_new_product),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
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
fun AddButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = {
            if (enabled) {
                onClick()
            }
        },
        containerColor = if (enabled) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        },
        contentColor = if (enabled) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f)
        },
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = if (enabled) 6.dp else 0.dp,
            pressedElevation = if (enabled) 3.dp else 0.dp
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(56.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_done),
            contentDescription = stringResource(R.string.cd_add),
            modifier = Modifier.size(24.dp)
        )
    }
}
