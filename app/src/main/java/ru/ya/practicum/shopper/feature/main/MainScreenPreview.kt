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
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.core.ui.DefaultPreviewContainer
import ru.ya.practicum.shopper.core.ui.theme.Theme
import ru.ya.practicum.shopper.feature.main.components.IconView
import ru.ya.practicum.shopper.feature.main.components.IconsModalBottomSheet
import ru.ya.practicum.shopper.feature.main.components.ShoppingListCard
import ru.ya.practicum.shopper.feature.main.components.ShoppingListsContent
import ru.ya.practicum.shopper.feature.main.components.SwipeCardActions

@Preview
@Composable
private fun MainScreenPreviewLight() {
    DefaultPreviewContainer(
        darkTheme = false
    ) {
        MainScreen(
            onNavigateToProduct = { _, _ -> },
            onThemeToggle = {}
        )
    }
}

@Preview
@Composable
private fun MainScreenPreviewDark() {
    DefaultPreviewContainer(
        darkTheme = true
    ) {
        MainScreen(
            onNavigateToProduct = { _, _ -> },
            onThemeToggle = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true, showBackground = true)
@Composable
private fun IconsModalBottomSheetPreviewLight() {
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
@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true, showBackground = true)
@Composable
private fun IconsModalBottomSheetPreviewDark() {
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

private val previewList = listOf(
    ShoppingList(
        id = 1,
        name = "Авто",
        iconResId = R.drawable.ic_car,
    ),
    ShoppingList(
        id = 2,
        name = "Мото",
        iconResId = R.drawable.ic_aid_kit,
    ),
    ShoppingList(
        id = 3,
        name = "Вело",
        iconResId = R.drawable.ic_cracker,
    ),
    ShoppingList(
        id = 4,
        name = "Фото",
        iconResId = R.drawable.ic_photocamera,
    )
)

@Preview
@Composable
private fun ShoppingListsContentPreviewLight() {
    Theme(darkTheme = false) {
        ShoppingListsContent(
            lists = previewList,
            listActions = SwipeCardActions({}, {}, {}, {}, {})

        )
    }
}

@Preview
@Composable
private fun ShoppingListsContentPreviewDark() {
    Theme(darkTheme = true) {
        ShoppingListsContent(
            lists = previewList,
            listActions = SwipeCardActions({}, {}, {}, {}, {})

        )
    }
}

@Preview
@Composable
private fun ShoppingListCardPreviewLight() {
    Theme(darkTheme = false) {
        ShoppingListCard(
            shoppingList = previewList.first(),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun ShoppingListCardPreviewDark() {
    Theme(darkTheme = true) {
        ShoppingListCard(
            shoppingList = previewList.first(),
            onClick = {}
        )
    }
}
