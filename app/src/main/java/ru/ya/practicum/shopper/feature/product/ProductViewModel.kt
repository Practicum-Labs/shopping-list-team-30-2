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

class ProductViewModel(
    private val application: Application,
    private val itemRepository: ShopperItemRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductState())
    val state: StateFlow<ProductState> = _state.asStateFlow()

    private val defaultUnit: String
        get() = application.getString(R.string.unit_pcs)

    private val defaultQuantity: String
        get() = application.getString(R.string.default_quantity)

    fun onEvent(event: ProductEvent) {
        when (event) {
            is ProductEvent.AddProduct -> addProduct(
                event.name,
                event.quantity,
                event.unit,
                event.listId
            )

            is ProductEvent.ToggleBought -> toggleBought(event.product, event.listId)
            is ProductEvent.LoadProducts -> loadProducts(event.listId)
            is ProductEvent.SwitchSorting -> switchSorting(event.byName)
        }
    }

    fun sortProductsByABC() {
        switchSorting(true)
    }

    fun sortProductByUserPref() {
        switchSorting(false)
    }

    private fun switchSorting(byName: Boolean) {
        //todo сохранение настройки сортировки в шаред преференс
        _state.update { it.copy(sortingByName = byName) }
        loadProducts(_state.value.currentListId)
    }

    private fun addProduct(name: String, quantity: String, unit: String, listId: Int) {
        viewModelScope.launch {
            try {
                val item = ShopperItem(
                    name = name,
                    unit = unit,
                    value = quantity.toFloatOrNull(),
                    isBought = false,
                    position = _state.value.products.size
                )
                itemRepository.addItem(item, listId)
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния: ${e.message}") }
            }
        }
    }

    private fun toggleBought(product: Product, listId: Int) {
        viewModelScope.launch {
            try {
                val item = ShopperItem(
                    id = product.id.toInt(),
                    name = product.name,
                    unit = product.unit,
                    value = product.amount.toFloatOrNull(),
                    isBought = !product.isBought,
                    position = 0
                )
                itemRepository.updateItem(item, listId)
            } catch (e: SQLException) {
                _state.update { it.copy(error = "Ошибка базы данных: ${e.message}") }
            } catch (e: IOException) {
                _state.update { it.copy(error = "Ошибка ввода-вывода: ${e.message}") }
            } catch (e: IllegalStateException) {
                _state.update { it.copy(error = "Ошибка состояния: ${e.message}") }
            }
        }
    }

    fun deleteAllProducts() {
        viewModelScope.launch {
            _state.value.products.forEach {
                itemRepository.deleteItemById(it.id.toInt())
            }
        }
        loadProducts(_state.value.currentListId)
    }

    fun clearBoughtProducts() {
        viewModelScope.launch {
            _state.value.products.filter{it.isBought}.forEach {
                itemRepository.deleteItemById(it.id.toInt())
            }
        }
        loadProducts(_state.value.currentListId)
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
            it.copy(
                isLoading = false,
                error = "Ошибка загрузки товаров: ${e.message}"
            )
        }
    }

    private fun handleLoadResult(resource: Resource<List<ShopperItem>>) {
        when (resource) {
            is Resource.Success -> {
                val products = mapItemsToProducts(resource.data)
                _state.update {
                    it.copy(isLoading = false, products = products, error = null)
                }
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(isLoading = false, error = "Ошибка загрузки товаров")
                }
            }
        }
    }

    private fun mapItemsToProducts(items: List<ShopperItem>?): List<Product> {
        return items?.map { item -> mapItemToProduct(item) } ?: emptyList()
    }

    private fun mapItemToProduct(item: ShopperItem): Product {
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
