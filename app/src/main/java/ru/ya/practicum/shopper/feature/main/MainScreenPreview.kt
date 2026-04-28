package ru.ya.practicum.shopper.feature.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.ui.DefaultPreviewContainer
import ru.ya.practicum.shopper.core.ui.theme.Theme
import ru.ya.practicum.shopper.feature.main.components.IconView
import ru.ya.practicum.shopper.feature.main.components.IconsModalBottomSheet

@Preview
@Composable
private fun MainScreenPreviewLight() {
    DefaultPreviewContainer(
        darkTheme = false
    ) {
        MainScreen()
    }
}

@Preview
@Composable
private fun MainScreenPreviewDark() {
    DefaultPreviewContainer(
        darkTheme = true
    ) {
        MainScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun IconsModalBottomSheetPreviewLight() {
    Theme(darkTheme = false) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

        IconsModalBottomSheet(
            bottomSheetState = sheetState,
            onDismissRequest = {},
            onIconClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun IconsModalBottomSheetPreviewDark() {
    Theme(darkTheme = true) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

        IconsModalBottomSheet(
            bottomSheetState = sheetState,
            onDismissRequest = {},
            onIconClick = {}
        )
    }
}

@Preview
@Composable
private fun IconLight() {
    Theme(darkTheme = false) {
        Row {
            IconView(
                icon = R.drawable.ic_car
            )
            Spacer(modifier = Modifier.width(5.dp))
            IconView(
                icon = R.drawable.ic_pet
            )
        }
    }
}

@Preview
@Composable
private fun IconDark() {
    Theme(darkTheme = true) {
        Row {
            IconView(
                icon = R.drawable.ic_car
            )
            Spacer(modifier = Modifier.width(5.dp))
            IconView(
                icon = R.drawable.ic_pet
            )
        }
    }
}
