package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.theme.GreenLight
import ru.ya.practicum.shopper.feature.product.ProductViewState

data class ProductBottomSheetCallBacks(
    val onSortByABC: () -> Unit,
    val onSortByUserPref: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
data class ProductBottomSheetConfig(
    val sheetState: SheetState,
    val state: ProductViewState,
    val onDismissRequest: () -> Unit,
    val onDeleteAll: () -> Unit,
    val onClearBought: () -> Unit,
    val callBacks: ProductBottomSheetCallBacks
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductBottomSheet(config: ProductBottomSheetConfig) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val currentSortString = if (config.state.sortingByName) "по алфавиту" else "пользовательская"

    ModalBottomSheet(
        onDismissRequest = config.onDismissRequest,
        sheetState = config.sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box {
            BottomSheetMenuItems(
                currentSortString = currentSortString,
                onSortClick = { sortMenuExpanded = true },
                onDeleteAll = config.onDeleteAll,
                onClearBought = config.onClearBought
            )

            SortMenu(
                expanded = sortMenuExpanded,
                sortingByName = config.state.sortingByName,
                onDismiss = { sortMenuExpanded = false },
                onSortByABC = {
                    config.callBacks.onSortByABC()
                    config.onDismissRequest()
                },
                onSortByUserPref = {
                    config.callBacks.onSortByUserPref()
                    config.onDismissRequest()
                }
            )
        }
    }
}

@Composable
private fun BottomSheetMenuItems(
    currentSortString: String,
    onSortClick: () -> Unit,
    onDeleteAll: () -> Unit,
    onClearBought: () -> Unit
) {
    Column {
        ProductBottomSheetString(
            iconRes = R.drawable.sort,
            stringRes = R.string.sort,
            substring = currentSortString,
            onStringClick = onSortClick,
            iconEndRes = R.drawable.arrow_right
        )
        ProductBottomSheetString(
            iconRes = R.drawable.delete,
            stringRes = R.string.deleteAll,
            substring = null,
            onStringClick = onDeleteAll
        )
        ProductBottomSheetString(
            iconRes = R.drawable.clear,
            stringRes = R.string.clearBought,
            substring = null,
            onStringClick = onClearBought
        )
    }
}

@Composable
private fun SortMenu(
    expanded: Boolean,
    sortingByName: Boolean,
    onDismiss: () -> Unit,
    onSortByABC: () -> Unit,
    onSortByUserPref: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        offset = DpOffset(x = 96.dp, y = 96.dp)
    ) {
        SortMenuItem(
            stringRes = R.string.sortByABC,
            iconRes = R.drawable.sort_abc,
            isSelected = sortingByName,
            onClick = onSortByABC
        )
        SortMenuItem(
            stringRes = R.string.sortByUserPref,
            iconRes = R.drawable.sort_user,
            isSelected = !sortingByName,
            onClick = onSortByUserPref
        )
    }
}

@Composable
private fun SortMenuItem(stringRes: Int, iconRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(280.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(stringRes))
                RadioButton(selected = isSelected, onClick = null)
            }
        },
        onClick = onClick,
        leadingIcon = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun ProductBottomSheetString(
    iconRes: Int,
    stringRes: Int,
    substring: String? = null,
    onStringClick: () -> Unit,
    iconEndRes: Int? = null,
) {
    Row(modifier = Modifier.clickable(onClick = onStringClick)) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 12.dp)
        )

        if (substring == null) {
            Text(
                text = stringResource(stringRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
            ) {
                Text(
                    text = stringResource(stringRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = substring,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GreenLight
                )
            }
        }

        if (iconEndRes != null) {
            Icon(
                painter = painterResource(iconEndRes),
                contentDescription = null,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 12.dp, end = 16.dp)
            )
        }
    }
}
