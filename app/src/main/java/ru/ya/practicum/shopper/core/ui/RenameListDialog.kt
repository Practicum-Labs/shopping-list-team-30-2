package ru.ya.practicum.shopper.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.components.buttons.PlainButton
import ru.ya.practicum.shopper.core.ui.theme.Theme

@Composable
fun RenameListDialog(
    onDismiss: () -> Unit,
    onCreate: (listName: String) -> Unit
) {
    var listName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DialogTitle()
                Spacer(modifier = Modifier.height(24.dp))
                ListNameTextField(
                    value = listName,
                    onValueChange = { listName = it }
                )
                Spacer(modifier = Modifier.height(24.dp))
                DialogButtons(
                    onDismiss = onDismiss,
                    onCreate = { onCreate(listName) }
                )
            }
        }
    }
}

@Composable
private fun DialogTitle() {
    Text(
        text = stringResource(R.string.rename_list),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun ListNameTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val labelSurfaceColor =
        when {
            isFocused -> MaterialTheme.colorScheme.surface
            value.isEmpty() -> MaterialTheme.colorScheme.surfaceContainerHigh
            else -> MaterialTheme.colorScheme.surface
        }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        label = {
            Surface(color = labelSurfaceColor) {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = stringResource(R.string.list_title),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        placeholder = {
            Text(
                stringResource(R.string.new_list),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun DialogButtons(
    onDismiss: () -> Unit,
    onCreate: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlainButton(
            buttonTitle = R.string.cancel_button_text,
            onClick = onDismiss
        )
        Spacer(modifier = Modifier.width(8.dp))
        PlainButton(
            buttonTitle = R.string.create_button_text,
            onClick = onCreate
        )
    }
}

@Preview(showSystemUi = true, showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun AddListDialogLightPreview() {
    Theme(darkTheme = false) {
        RenameListDialog(
            onDismiss = {},
            onCreate = {}
        )
    }
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true, showBackground = true)
@Composable
private fun AddListDialogDarkPreview() {
    Theme(darkTheme = true) {
        RenameListDialog(
            onDismiss = {},
            onCreate = {}
        )
    }
}
