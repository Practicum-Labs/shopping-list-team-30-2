package ru.ya.practicum.shopper.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.theme.Theme

@Composable
fun DeleteAllListsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    deleteOneList: Boolean = false,
    listName: String = ""
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            DeleteAllListsDialogContent(
                onDismiss = onDismiss,
                onConfirm = onConfirm,
                deleteOneList,
                listName
            )
        }
    }
}

@Composable
private fun DeleteAllListsDialogContent(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    deleteOneList: Boolean,
    listName: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.attention),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (deleteOneList) {
                stringResource(
                    R.string.delete_list_text,
                    listName
                )
            } else {
                stringResource(R.string.delete_all_lists_title)
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))
        DeleteAllListsDialogButtons(onDismiss = onDismiss, onConfirm = onConfirm)
    }
}

@Composable
private fun DeleteAllListsDialogButtons(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = stringResource(R.string.cancel_button_text),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onConfirm,
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Text(
                text = stringResource(R.string.delete_button_text),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun DeleteDialogLightPreview() {
    Theme(darkTheme = false) {
        DeleteAllListsDialog(onDismiss = {}, onConfirm = {})
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun DeleteDialogDarkPreview() {
    Theme(darkTheme = true) {
        DeleteAllListsDialog(onDismiss = {}, onConfirm = {})
    }
}
