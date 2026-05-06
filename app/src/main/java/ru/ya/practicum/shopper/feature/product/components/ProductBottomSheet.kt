package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.ui.graphics.Color
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.theme.GreenLight
import ru.ya.practicum.shopper.feature.main.components.IconView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductBottomSheet(
    sheetState: SheetState,
    callBacks: ProductBottomSheetCallBacks,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val currentSortString =
        "по алфавиту" // для демонстрации. после привязки стейта заменю на значение стейта

    ModalBottomSheet(
        onDismissRequest = callBacks.onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        ),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box() {
            Column() {
                ProductBottomSheetString(
                    R.drawable.sort,
                    R.string.sort,
                    currentSortString,
                    { menuExpanded = true },
                    R.drawable.arrow_right
                )
                ProductBottomSheetString(
                    R.drawable.delete,
                    R.string.deleteAll,
                    null,
                    callBacks.onDeleteAll
                )
                ProductBottomSheetString(
                    R.drawable.clear,
                    R.string.clearBought,
                    null,
                    callBacks.onClearBought
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.align(Alignment.TopEnd),
                offset = DpOffset(x = (96).dp, y = 96.dp)
            ) {
                SortMenuItem(R.string.sortByABC, R.drawable.sort_abc, true, callBacks.onSortByABC)
                SortMenuItem(
                    R.string.sortByUserPref,
                    R.drawable.sort_user,
                    false,
                    callBacks.onSortByUserPref
                )
            }
        }
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
        if (substring == null)
            Text(
                text = stringResource(stringRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
            ) else
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
        if (iconEndRes != null)
            Icon(
                painter = painterResource(iconEndRes),
                contentDescription = null,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 12.dp, end = 16.dp)
            )
    }
}

class ProductBottomSheetCallBacks(
    val onDismissRequest: () -> Unit,
    val onSortByABC: () -> Unit,
    val onSortByUserPref: () -> Unit,
    val onDeleteAll: () -> Unit,
    val onClearBought: () -> Unit,
)