package ru.ya.practicum.shopper.feature.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices.PIXEL_6
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.DefaultPreviewContainer

private const val GRID_COLUMNS = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconsModalBottomSheet(
    bottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
    onIconClick: (Int) -> Unit,
) {
    val context = LocalContext.current
    val icons = remember(context) {
        val typedArray = context.resources.obtainTypedArray(R.array.category_icons)
        IntArray(typedArray.length()) { typedArray.getResourceId(it, 0) }
            .also { typedArray.recycle() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = { BottomSheetDragHandle() },
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        ),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLUMNS),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 48.dp, end = 48.dp, top = 28.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(icons.size) { index ->
                val icon = icons[index]
                IconView(
                    icon = icon,
                    onClick = {
                        onIconClick(icon)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomSheetDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .width(32.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(100))
            .background(MaterialTheme.colorScheme.outline)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, device = PIXEL_6)
@Composable
private fun IconsModalBottomSheetPreview() {
    DefaultPreviewContainer {
        IconsModalBottomSheet(
            bottomSheetState = rememberModalBottomSheetState(),
            onDismissRequest = {},
            onIconClick = {}
        )
    }
}