package ru.ya.practicum.shopper.feature.product

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.model.Product
import ru.ya.practicum.shopper.core.util.Resource
import ru.ya.practicum.shopper.domain.model.ShopperItem
import ru.ya.practicum.shopper.domain.repository.ShopperItemRepository
import ru.ya.practicum.shopper.domain.usecase.product.AddProductParams
import ru.ya.practicum.shopper.domain.usecase.product.AddProductUseCase
import ru.ya.practicum.shopper.domain.usecase.product.ClearBoughtProductsParams
import ru.ya.practicum.shopper.domain.usecase.product.ClearBoughtProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.DeleteAllProductsParams
import ru.ya.practicum.shopper.domain.usecase.product.DeleteAllProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.DeleteProductParams
import ru.ya.practicum.shopper.domain.usecase.product.DeleteProductUseCase
import ru.ya.practicum.shopper.domain.usecase.product.ToggleProductBoughtParams
import ru.ya.practicum.shopper.domain.usecase.product.ToggleProductBoughtUseCase
import java.io.IOException
import java.sql.SQLException

data class ProductState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentListId: Int = 0,
    val sortingByName: Boolean = false,
)

sealed class ProductEvent {
    data class AddProduct(
        val name: String,
        val quantity: String,
        val unit: String,
        val listId: Int
    ) : ProductEvent()

    data class ToggleBought(val product: Product, val listId: Int) : ProductEvent()
    data class LoadProducts(val listId: Int) : ProductEvent()
    data class SwitchSorting(val byName: Boolean) : ProductEvent()
}

data class ProductDependencies(
    val application: Application,
    val itemRepository: ShopperItemRepository,
    val dataStore: ProductDataStore,
    val addProductUseCase: AddProductUseCase,
    val toggleProductBoughtUseCase: ToggleProductBoughtUseCase,
    val deleteProductUseCase: DeleteProductUseCase,
    val deleteAllProductsUseCase: DeleteAllProductsUseCase,
    val clearBoughtProductsUseCase: ClearBoughtProductsUseCase
)

@Suppress("TooManyFunctions")
class ProductViewModel(
    private val deps: ProductDependencies
) : ViewModel() {
    private val application = deps.application
    private val itemRepository = deps.itemRepository
    private val dataStore = deps.dataStore
    private val addProductUseCase = deps.addProductUseCase
    private val toggleProductBoughtUseCase = deps.toggleProductBoughtUseCase
    private val deleteProductUseCase = deps.deleteProductUseCase
    private val deleteAllProductsUseCase = deps.deleteAllProductsUseCase
    private val clearBoughtProductsUseCase = deps.clearBoughtProductsUseCase

    private val _state = MutableStateFlow(ProductState())
    val state: StateFlow<ProductState> = _state.asStateFlow()

    private val defaultUnit: String
        get() = application.getString(R.string.unit_pcs)

    private val defaultQuantity: String
        get() = application.getString(R.string.default_quantity)

    private val mapper = ProductMapper(defaultUnit, defaultQuantity)

    init {
        viewModelScope.launch {
            dataStore.isProductsSortByName.collect { res ->
                _state.update { it.copy(sortingByName = res) }
            }
        }
    }

    fun onEvent(event: ProductEvent) {
        when (event) {
            is ProductEvent.AddProduct -> handleAddProductWithUseCase(
                event.name,
                event.quantity,
                event.unit,
                event.listId
            )

            is ProductEvent.ToggleBought -> handleToggleBoughtWithUseCase(
                event.product,
                event.listId
            )

            is ProductEvent.LoadProducts -> loadProducts(event.listId)
            is ProductEvent.SwitchSorting -> switchSorting(event.byName)
        }
    }

    fun sortProductsByABC() = switchSorting(true)
    fun sortProductByUserPref() = switchSorting(false)

    fun deleteAllProducts() = performDeleteAll()
    fun clearBoughtProducts() = performClearBought()

    private fun switchSorting(byName: Boolean) {
        viewModelScope.launch { dataStore.setProductsSortByName(byName) }
        _state.update { it.copy(sortingByName = byName) }
        loadProducts(_state.value.currentListId)
    }

    private fun handleAddProductWithUseCase(
        name: String,
        quantity: String,
        unit: String,
        listId: Int
    ) {
        viewModelScope.launch {
            try {
                addProductUseCase(
                    AddProductParams(
                        name = name,
                        unit = unit.takeIf { it.isNotBlank() },
                        value = quantity.toFloatOrNull(),
                        listId = listId,
                        position = _state.value.products.size
                    )
                )
                loadProducts(listId)
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния: ${e.message}") }
            } catch (e: IllegalArgumentException) {
                _state.update { it.copy(error = "Ошибка валидации: ${e.message}") }
            }
        }
    }

    private fun handleToggleBoughtWithUseCase(product: Product, listId: Int) {
        viewModelScope.launch {
            try {
                toggleProductBoughtUseCase(
                    ToggleProductBoughtParams(
                        productId = product.id.toInt(),
                        listId = listId,
                        productName = product.name,
                        productUnit = product.unit,
                        productValue = product.amount.toFloatOrNull(),
                        currentIsBought = product.isBought
                    )
                )
                loadProducts(listId)
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния: ${e.message}") }
            }
        }
    }

    private fun loadProducts(listId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, currentListId = listId) }
            itemRepository.getAllItems(listId, _state.value.sortingByName)
                .catch { e -> handleLoadError(e) }
                .collect { resource -> handleLoadResult(resource) }
        }
    }

    private fun handleLoadError(e: Throwable) {
        _state.update {
            it.copy(isLoading = false, error = "Ошибка загрузки товаров: ${e.message}")
        }
    }

    private fun handleLoadResult(resource: Resource<List<ShopperItem>>) {
        when (resource) {
            is Resource.Success -> {
                val products = mapper.mapItemsToProducts(resource.data)
                _state.update { it.copy(isLoading = false, products = products, error = null) }
            }

            is Resource.Error -> {
                _state.update { it.copy(isLoading = false, error = "Ошибка загрузки товаров") }
            }
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            try {
                deleteProductUseCase(DeleteProductParams(productId))
                loadProducts(_state.value.currentListId)
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных при удалении: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода при удалении: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния при удалении: ${e.message}") }
            } catch (e: IllegalArgumentException) {
                _state.update { it.copy(error = "Ошибка валидации при удалении: ${e.message}") }
            }
        }
    }

    private fun performDeleteAll() {
        viewModelScope.launch {
            try {
                deleteAllProductsUseCase(
                    DeleteAllProductsParams(listId = _state.value.currentListId)
                )
                loadProducts(_state.value.currentListId)
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных при удалении всех: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода при удалении всех: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния при удалении всех: ${e.message}") }
            } catch (e: IllegalArgumentException) {
                _state.update { it.copy(error = "Ошибка валидации при удалении всех: ${e.message}") }
            }
        }
    }

    private fun performClearBought() {
        viewModelScope.launch {
            try {
                clearBoughtProductsUseCase(
                    ClearBoughtProductsParams(listId = _state.value.currentListId)
                )
                loadProducts(_state.value.currentListId)
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных при очистке: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода при очистке: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния при очистке: ${e.message}") }
            } catch (e: IllegalArgumentException) {
                _state.update { it.copy(error = "Ошибка валидации при очистке: ${e.message}") }
            }
        }
    }
}

private class ProductMapper(
    private val defaultUnit: String,
    private val defaultQuantity: String
) {
    fun mapItemsToProducts(items: List<ShopperItem>?): List<Product> {
        return items?.map { mapItemToProduct(it) } ?: emptyList()
    }

    fun mapItemToProduct(item: ShopperItem): Product {
        return Product(
            id = item.id.toLong(),
            name = item.name,
            amount = formatValue(item.value),
            unit = item.unit ?: defaultUnit,
            isBought = item.isBought
        )
    }

    private fun formatValue(value: Float?): String {
        return value?.let {
            if (it == it.toLong().toFloat()) {
                it.toLong().toString()
            } else {
                it.toString()
            }
        } ?: defaultQuantity
    }
}
