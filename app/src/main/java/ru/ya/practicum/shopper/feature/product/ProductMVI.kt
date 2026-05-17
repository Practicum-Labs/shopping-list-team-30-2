package ru.ya.practicum.shopper.feature.product

import ru.ya.practicum.shopper.core.model.Product
import ru.ya.practicum.shopper.feature.product.components.ProductAddBottomSheetState

data class ProductViewState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentListId: Int = 0,
    val sortingByName: Boolean = false,
    val productToDelete: Product? = null,
    val productToChange: Product? = null
)

sealed class ProductIntent {
    data class LoadProducts(val listId: Int) : ProductIntent()
    data class AddProduct(
        val name: String,
        val quantity: String,
        val unit: String,
        val listId: Int
    ) : ProductIntent()

    data class ToggleProductBought(val product: Product, val listId: Int) : ProductIntent()
    data class DeleteProduct(val productId: Long) : ProductIntent()
    data class ChangeSorting(val byName: Boolean) : ProductIntent()
    object DeleteAllProducts : ProductIntent()
    object ClearBoughtProducts : ProductIntent()
    data class SetDeletedProduct(val product: Product) : ProductIntent()
    data class SetChangeProduct(val product: Product) : ProductIntent()
    data class ChangeProduct(val newData: ProductAddBottomSheetState) : ProductIntent()
    data class OnMove(val from: Int, val to: Int) : ProductIntent()
}

sealed class ProductResult {
    data class ProductsLoaded(val items: List<Product>) : ProductResult()
    data class ProductAdded(val products: List<Product>) : ProductResult()
    data class ProductToggled(val products: List<Product>) : ProductResult()
    data class ProductDeleted(val products: List<Product>) : ProductResult()
    data class SortingChanged(val byName: Boolean, val products: List<Product>) : ProductResult()
    data class ProductsCleaned(val products: List<Product>) : ProductResult()
    data class Error(val message: String) : ProductResult()
    object StateUpdated : ProductResult()
}

sealed class ProductEffect {
    data class ShowError(val message: String) : ProductEffect()
}
