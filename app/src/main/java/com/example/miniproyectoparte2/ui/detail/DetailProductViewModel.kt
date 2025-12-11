package com.example.miniproyectoparte2.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyectoparte2.data.model.Product
import com.example.miniproyectoparte2.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _deleteState = MutableLiveData<DeleteState>(DeleteState.Idle)
    val deleteState: LiveData<DeleteState> = _deleteState

    private val _updateState = MutableLiveData<UpdateState>(UpdateState.Idle)
    val updateState: LiveData<UpdateState> = _updateState

    fun loadProduct(productId: String) {
        // Si luego agregas getProductById al repositorio, lo llamas aquí.
        // Por ahora puedes usar setProductFromIntent desde la Activity.
    }

    fun setProductFromIntent(product: Product) {
        _product.value = product
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _deleteState.value = DeleteState.Loading

            val result = productRepository.deleteProduct(productId)

            _deleteState.value = if (result.isSuccess) {
                DeleteState.Success
            } else {
                DeleteState.Error("Error al eliminar el producto")
            }
        }
    }

    fun updateProduct(id: String, name: String, price: Double, quantity: Int) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading

            val current = _product.value
            if (current == null) {
                _updateState.value = UpdateState.Error("Producto no cargado")
                return@launch
            }

            val updated = current.copy(
                id = id,
                name = name,
                price = price,
                quantity = quantity
            )

            val result = productRepository.updateProduct(updated)

            _updateState.value = if (result.isSuccess) {
                _product.value = updated
                UpdateState.Success
            } else {
                UpdateState.Error("Error al actualizar el producto")
            }
        }
    }

    sealed class DeleteState {
        object Idle : DeleteState()
        object Loading : DeleteState()
        object Success : DeleteState()
        data class Error(val message: String) : DeleteState()
    }

    sealed class UpdateState {
        object Idle : UpdateState()
        object Loading : UpdateState()
        object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
    }
}
