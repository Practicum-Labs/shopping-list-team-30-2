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
import ru.ya.practicum.shopper.domain.usecase.product.AddProductParams
import ru.ya.practicum.shopper.domain.usecase.product.AddProductUseCase
import ru.ya.practicum.shopper.domain.usecase.product.ClearBoughtProductsParams
import ru.ya.practicum.shopper.domain.usecase.product.ClearBoughtProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.DeleteAllProductsParams
import ru.ya.practicum.shopper.domain.usecase.product.DeleteAllProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.DeleteProductUseCase
import ru.ya.practicum.shopper.domain.usecase.product.GetProductsParams
import ru.ya.practicum.shopper.domain.usecase.product.GetProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.GetSortingSettingUseCase
import ru.ya.practicum.shopper.domain.usecase.product.MapProductsParams
import ru.ya.practicum.shopper.domain.usecase.product.MapProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.SaveSortingSettingParams
import ru.ya.practicum.shopper.domain.usecase.product.SaveSortingSettingUseCase
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
    val addProductUseCase: AddProductUseCase,
    val toggleProductBoughtUseCase: ToggleProductBoughtUseCase,
    val deleteProductUseCase: DeleteProductUseCase,
    val deleteAllProductsUseCase: DeleteAllProductsUseCase,
    val clearBoughtProductsUseCase: ClearBoughtProductsUseCase,
    val getProductsUseCase: GetProductsUseCase,
    val getSortingSettingUseCase: GetSortingSettingUseCase,
    val saveSortingSettingUseCase: SaveSortingSettingUseCase,
    val mapProductsUseCase: MapProductsUseCase
)

@Suppress("TooManyFunctions")
class ProductViewModel(
    private val deps: ProductDependencies
) : ViewModel() {

    private val _state = MutableStateFlow(ProductState())
    val state: StateFlow<ProductState> = _state.asStateFlow()

    private val defaultUnit: String
        get() = deps.application.getString(R.string.unit_pcs)

    private val defaultQuantity: String
        get() = deps.application.getString(R.string.default_quantity)

    init {
        viewModelScope.launch {
            deps.getSortingSettingUseCase(Unit).collect { res ->
                _state.update { it.copy(sortingByName = res) }
            }
        }
    }

    fun onEvent(event: ProductEvent) {
        when (event) {
            is ProductEvent.AddProduct -> handleAddProduct(event)
            is ProductEvent.ToggleBought -> handleToggleBought(event)
            is ProductEvent.LoadProducts -> loadProducts(event.listId)
            is ProductEvent.SwitchSorting -> switchSorting(event.byName)
        }
    }

    fun sortProductsByABC() = switchSorting(true)
    fun sortProductByUserPref() = switchSorting(false)

    fun deleteAllProducts() = performDeleteAll()
    fun clearBoughtProducts() = performClearBought()

    private fun switchSorting(byName: Boolean) {
        viewModelScope.launch {
            deps.saveSortingSettingUseCase(SaveSortingSettingParams(byName))
            _state.update { it.copy(sortingByName = byName) }
            loadProducts(_state.value.currentListId)
        }
    }

    private fun handleAddProduct(event: ProductEvent.AddProduct) {
        viewModelScope.launch {
            try {
                deps.addProductUseCase(
                    AddProductParams(
                        name = event.name,
                        unit = event.unit.takeIf { it.isNotBlank() },
                        value = event.quantity.toFloatOrNull(),
                        listId = event.listId,
                        position = _state.value.products.size
                    )
                )
                loadProducts(event.listId)
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

    private fun handleToggleBought(event: ProductEvent.ToggleBought) {
        viewModelScope.launch {
            try {
                deps.toggleProductBoughtUseCase(
                    ToggleProductBoughtParams(
                        productId = event.product.id.toInt(),
                        listId = event.listId,
                        productName = event.product.name,
                        productUnit = event.product.unit,
                        productValue = event.product.amount.toFloatOrNull(),
                        currentIsBought = event.product.isBought
                    )
                )
                loadProducts(event.listId)
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
            deps.getProductsUseCase(GetProductsParams(listId, _state.value.sortingByName))
                .catch { e ->
                    _state.update {
                        it.copy(isLoading = false, error = "Ошибка загрузки товаров: ${e.message}")
                    }
                }
                .collect { shopperItems ->
                    val products = deps.mapProductsUseCase(
                        MapProductsParams(
                            items = shopperItems,
                            defaultUnit = defaultUnit,
                            defaultQuantity = defaultQuantity
                        )
                    )
                    _state.update {
                        it.copy(isLoading = false, products = products, error = null)
                    }
                }
        }
    }

    private fun performDeleteAll() {
        viewModelScope.launch {
            try {
                deps.deleteAllProductsUseCase(
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
                deps.clearBoughtProductsUseCase(
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
