package ru.ya.practicum.shopper.feature.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.model.Product
import ru.ya.practicum.shopper.core.resource.ResourceProvider
import ru.ya.practicum.shopper.domain.usecase.product.AddProductParams
import ru.ya.practicum.shopper.domain.usecase.product.AddProductUseCase
import ru.ya.practicum.shopper.domain.usecase.product.ChangeProductParams
import ru.ya.practicum.shopper.domain.usecase.product.ClearBoughtProductsParams
import ru.ya.practicum.shopper.domain.usecase.product.ClearBoughtProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.DeleteAllProductsParams
import ru.ya.practicum.shopper.domain.usecase.product.DeleteAllProductsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.DeleteProductParams
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
import ru.ya.practicum.shopper.domain.usecase.product.UpdateProductPositionsUseCase
import ru.ya.practicum.shopper.domain.usecase.product.UpdateProductUseCase
import ru.ya.practicum.shopper.feature.product.components.ProductAddBottomSheetState

data class ProductDependencies(
    val defaultUnit: String,
    val defaultQuantity: String,
    val addProductUseCase: AddProductUseCase,
    val toggleProductBoughtUseCase: ToggleProductBoughtUseCase,
    val deleteProductUseCase: DeleteProductUseCase,
    val deleteAllProductsUseCase: DeleteAllProductsUseCase,
    val clearBoughtProductsUseCase: ClearBoughtProductsUseCase,
    val getProductsUseCase: GetProductsUseCase,
    val getSortingSettingUseCase: GetSortingSettingUseCase,
    val saveSortingSettingUseCase: SaveSortingSettingUseCase,
    val mapProductsUseCase: MapProductsUseCase,
    val updateProductUseCase: UpdateProductUseCase,
    val updateProductPositionsUseCase: UpdateProductPositionsUseCase
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Suppress("TooManyFunctions", "TooGenericExceptionCaught")
class ProductViewModel(
    private val deps: ProductDependencies,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _state = MutableStateFlow(ProductViewState())
    val state: StateFlow<ProductViewState> = _state.asStateFlow()

    private val _effect = Channel<ProductEffect>()
    val effect: Flow<ProductEffect> = _effect.receiveAsFlow()

    private val actions = MutableSharedFlow<ProductIntent>()

    init {
        setupSortingListener()
        processActions()
    }

    fun onMove(from: Int, to: Int): ProductResult {
        val reordered = _state.value.products
            .toMutableList()
            .apply { add(to, removeAt(from)) }
            .mapIndexed { index, product -> product.copy(position = index) }

        _state.update { it.copy(products = reordered) }
        viewModelScope.launch {
            deps.updateProductPositionsUseCase(reordered)
            loadProducts(_state.value.currentListId)
        }
        return ProductResult.StateUpdated
    }

    fun onIntent(intent: ProductIntent) {
        viewModelScope.launch {
            actions.emit(intent)
        }
    }

    private fun setupSortingListener() {
        viewModelScope.launch {
            deps.getSortingSettingUseCase(Unit).collect { sortByName ->
                onIntent(ProductIntent.ChangeSorting(sortByName))
            }
        }
    }

    private fun processActions() {
        viewModelScope.launch {
            actions
                .onEach { _state.update { it.copy(isLoading = true, errorMessage = null) } }
                .flatMapConcat { intent -> toResult(intent) }
                .collect { result -> reduce(result) }
        }
    }

    private fun toResult(intent: ProductIntent): Flow<ProductResult> = flow {
        val result = when (intent) {
            is ProductIntent.LoadProducts -> loadProducts(intent.listId)
            is ProductIntent.AddProduct -> addProduct(intent)
            is ProductIntent.ToggleProductBought -> toggleProduct(intent)
            is ProductIntent.DeleteProduct -> deleteProduct(intent.productId)
            is ProductIntent.ChangeSorting -> changeSorting(intent.byName)
            ProductIntent.DeleteAllProducts -> deleteAllProducts()
            ProductIntent.ClearBoughtProducts -> clearBoughtProducts()
            is ProductIntent.SetDeletedProduct -> setDeletedProduct(intent.product)
            is ProductIntent.SetChangeProduct -> setChangeProduct(intent.product)
            is ProductIntent.ChangeProduct -> changeProduct(intent.newData)
            is ProductIntent.OnMove -> onMove(intent.from, intent.to)
        }
        emit(result)
    }

    private suspend fun reduce(result: ProductResult) {
        when (result) {
            is ProductResult.ProductsLoaded -> reduceProductsLoaded(result)
            is ProductResult.ProductAdded -> reduceProductAdded(result)
            is ProductResult.ProductToggled -> reduceProductToggled(result)
            is ProductResult.ProductDeleted -> reduceProductDeleted(result)
            is ProductResult.SortingChanged -> reduceSortingChanged(result)
            is ProductResult.ProductsCleaned -> reduceProductsCleaned(result)
            is ProductResult.Error -> reduceError(result)
            ProductResult.StateUpdated -> {}
        }
    }

    private fun setDeletedProduct(product: Product): ProductResult {
        _state.update { it.copy(productToDelete = product) }
        return ProductResult.StateUpdated
    }

    private fun setChangeProduct(product: Product): ProductResult {
        _state.update { it.copy(productToChange = product) }
        return ProductResult.StateUpdated
    }

    private suspend fun loadProducts(listId: Int): ProductResult {
        return try {
            _state.update { it.copy(currentListId = listId) }
            val shopperItems = deps.getProductsUseCase(
                GetProductsParams(listId, _state.value.sortingByName)
            ).first()
            val products = deps.mapProductsUseCase(
                MapProductsParams(
                    items = shopperItems,
                    defaultUnit = deps.defaultUnit,
                    defaultQuantity = deps.defaultQuantity
                )
            )
            ProductResult.ProductsLoaded(products)
        } catch (e: Exception) {
            ProductResult.Error(resourceProvider.getString(R.string.error_unknown, e.message ?: ""))
        }
    }

    private suspend fun addProduct(intent: ProductIntent.AddProduct): ProductResult {
        return try {
            deps.addProductUseCase(
                AddProductParams(
                    name = intent.name,
                    unit = intent.unit.takeIf { it.isNotBlank() },
                    value = intent.quantity.toFloatOrNull(),
                    listId = intent.listId,
                    position = _state.value.products.size
                )
            )
            loadProducts(intent.listId)
        } catch (e: Exception) {
            ProductResult.Error(
                resourceProvider.getString(
                    R.string.error_add_product,
                    e.message ?: ""
                )
            )
        }
    }

    private suspend fun toggleProduct(intent: ProductIntent.ToggleProductBought): ProductResult {
        return try {
            deps.toggleProductBoughtUseCase(
                ToggleProductBoughtParams(
                    productId = intent.product.id.toInt(),
                    listId = intent.listId,
                    productName = intent.product.name,
                    productUnit = intent.product.unit,
                    productValue = intent.product.amount.toFloatOrNull(),
                    currentIsBought = intent.product.isBought,
                    position = intent.product.position
                )
            )
            loadProducts(intent.listId)
        } catch (e: Exception) {
            ProductResult.Error(
                resourceProvider.getString(
                    R.string.error_toggle_product,
                    e.message ?: ""
                )
            )
        }
    }

    private suspend fun deleteProduct(productId: Long): ProductResult {
        return try {
            deps.deleteProductUseCase(DeleteProductParams(productId.toInt()))
            loadProducts(_state.value.currentListId)
        } catch (e: Exception) {
            ProductResult.Error(
                resourceProvider.getString(
                    R.string.error_delete_product,
                    e.message ?: ""
                )
            )
        }
    }

    private suspend fun changeProduct(newData: ProductAddBottomSheetState): ProductResult {
        return try {
            if (state.value.productToChange != null) {
                deps.updateProductUseCase(
                    ChangeProductParams(
                        id = state.value.productToChange!!.id.toInt(),
                        name = newData.productName,
                        unit = newData.selectedUnit,
                        value = newData.quantity.toFloatOrNull(),
                        listId = _state.value.currentListId,
                        position = state.value.productToChange!!.position,
                        isBought = state.value.productToChange!!.isBought
                    )
                )
            }
            loadProducts(_state.value.currentListId)
        } catch (e: Exception) {
            ProductResult.Error(
                resourceProvider.getString(
                    R.string.error_change_product,
                    e.message ?: ""
                )
            )
        }
    }

    private suspend fun changeSorting(byName: Boolean): ProductResult {
        return try {
            deps.saveSortingSettingUseCase(SaveSortingSettingParams(byName))
            _state.update { it.copy(sortingByName = byName) }
            loadProducts(_state.value.currentListId)
        } catch (e: Exception) {
            ProductResult.Error(
                resourceProvider.getString(
                    R.string.error_change_sorting,
                    e.message ?: ""
                )
            )
        }
    }

    private suspend fun deleteAllProducts(): ProductResult {
        return try {
            deps.deleteAllProductsUseCase(
                DeleteAllProductsParams(listId = _state.value.currentListId)
            )
            loadProducts(_state.value.currentListId)
        } catch (e: Exception) {
            ProductResult.Error(
                resourceProvider.getString(
                    R.string.error_delete_all_products,
                    e.message ?: ""
                )
            )
        }
    }

    private suspend fun clearBoughtProducts(): ProductResult {
        return try {
            deps.clearBoughtProductsUseCase(
                ClearBoughtProductsParams(listId = _state.value.currentListId)
            )
            loadProducts(_state.value.currentListId)
        } catch (e: Exception) {
            ProductResult.Error(
                resourceProvider.getString(
                    R.string.error_clear_bought,
                    e.message ?: ""
                )
            )
        }
    }

    private fun reduceProductsLoaded(result: ProductResult.ProductsLoaded) {
        _state.update {
            it.copy(
                isLoading = false,
                products = result.items,
                errorMessage = null
            )
        }
    }

    private fun reduceProductAdded(result: ProductResult.ProductAdded) {
        _state.update {
            it.copy(
                isLoading = false,
                products = result.products,
                errorMessage = null
            )
        }
    }

    private fun reduceProductToggled(result: ProductResult.ProductToggled) {
        _state.update {
            it.copy(
                isLoading = false,
                products = result.products,
                errorMessage = null
            )
        }
    }

    private fun reduceProductDeleted(result: ProductResult.ProductDeleted) {
        _state.update {
            it.copy(
                isLoading = false,
                products = result.products,
                errorMessage = null
            )
        }
    }

    private fun reduceSortingChanged(result: ProductResult.SortingChanged) {
        _state.update {
            it.copy(
                isLoading = false,
                sortingByName = result.byName,
                products = result.products,
                errorMessage = null
            )
        }
    }

    private fun reduceProductsCleaned(result: ProductResult.ProductsCleaned) {
        _state.update {
            it.copy(
                isLoading = false,
                products = result.products,
                errorMessage = null
            )
        }
    }

    private suspend fun reduceError(result: ProductResult.Error) {
        _effect.send(ProductEffect.ShowError(result.message))
        _state.update {
            it.copy(
                isLoading = false,
                errorMessage = result.message
            )
        }
    }
}
