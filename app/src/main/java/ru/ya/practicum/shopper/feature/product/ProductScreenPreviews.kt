package ru.ya.practicum.shopper.feature.product

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.ya.practicum.shopper.core.model.Product
import ru.ya.practicum.shopper.core.ui.DefaultPreviewContainer
import ru.ya.practicum.shopper.core.ui.theme.Theme
import ru.ya.practicum.shopper.feature.product.components.ProductItemCard
import ru.ya.practicum.shopper.feature.product.components.ProductItemsContent
import ru.ya.practicum.shopper.feature.product.components.SwipeItemActions

@Preview
@Composable
private fun ProductScreenPreviewLight() {
    DefaultPreviewContainer(darkTheme = false) {
        ProductScreen(
            listId = 1,
            listName = "Продукты",
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun ProductScreenPreviewDark() {
    DefaultPreviewContainer(darkTheme = true) {
        ProductScreen(
            listId = 1,
            listName = "Продукты",
            onBackClick = {}
        )
    }
}

private val previewProducts = listOf(
    Product(
        id = 1,
        name = "Роман Муромец",
        amount = "1",
        unit = "чел",
        isBought = false
    ),
    Product(
        id = 2,
        name = "Роман Никитич",
        amount = "1",
        unit = "чел",
        isBought = false
    ),
    Product(
        id = 3,
        name = "Василий Попович",
        amount = "1",
        unit = "чел",
        isBought = false
    ),
    Product(
        id = 4,
        name = "Анастасия Премудрая",
        amount = "1",
        unit = "чел",
        isBought = false
    )
)

private val previewProductsOff = listOf(
    Product(
        id = 1,
        name = "Роман Муромец",
        amount = "1",
        unit = "чел",
        isBought = false
    ),
    Product(
        id = 2,
        name = "Роман Никитич",
        amount = "1",
        unit = "чел",
        isBought = false
    ),
    Product(
        id = 3,
        name = "Василий Попович",
        amount = "1",
        unit = "чел",
        isBought = true
    ),
    Product(
        id = 4,
        name = "Анастасия Премудрая",
        amount = "1",
        unit = "чел",
        isBought = false
    )
)

@Preview
@Composable
private fun ProductContentPreviewLight() {
    Theme(darkTheme = false) {
        ProductItemsContent(
            products = previewProducts,
            actions = SwipeItemActions(
                onItemClick = {},
                onDelete = {},
                onRename = {}
            )
        )
    }
}

@Preview
@Composable
private fun ProductItemsContentPreviewDark() {
    Theme(darkTheme = true) {
        ProductItemsContent(
            products = previewProducts,
            actions = SwipeItemActions(
                onItemClick = {},
                onDelete = {},
                onRename = {}
            )
        )
    }
}

@Preview
@Composable
private fun ProductItemsContentBoughtPreviewLight() {
    Theme(darkTheme = false) {
        ProductItemsContent(
            products = previewProductsOff,
            actions = SwipeItemActions(
                onItemClick = {},
                onDelete = {},
                onRename = {}
            )
        )
    }
}

@Preview
@Composable
private fun ProductItemsContentBoughtPreviewDark() {
    Theme(darkTheme = true) {
        ProductItemsContent(
            products = previewProductsOff,
            actions = SwipeItemActions(
                onItemClick = {},
                onDelete = {},
                onRename = {}
            )
        )
    }
}

@Preview
@Composable
private fun ProductItemCardPreviewLight() {
    Theme(darkTheme = false) {
        ProductItemCard(
            product = previewProducts.first(),
            onItemClick = {}
        )
    }
}

@Preview
@Composable
private fun ProductItemCardPreviewDark() {
    Theme(darkTheme = true) {
        ProductItemCard(
            product = previewProducts.first(),
            onItemClick = {}
        )
    }
}

@Preview
@Composable
private fun ProductItemCardBoughtPreviewLight() {
    Theme(darkTheme = false) {
        ProductItemCard(
            product = previewProducts.first().copy(isBought = true),
            onItemClick = {}
        )
    }
}

@Preview
@Composable
private fun ProductItemCardBoughtPreviewDark() {
    Theme(darkTheme = true) {
        ProductItemCard(
            product = previewProducts.first().copy(isBought = true),
            onItemClick = {}
        )
    }
}
