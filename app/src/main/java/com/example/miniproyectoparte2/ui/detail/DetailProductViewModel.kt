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


    private val _deleteState = MutableLiveData<DeleteState>()
    val deleteState: LiveData<DeleteState> = _deleteState


    fun loadProduct(productId: String) {
        // TODO: implementar consulta por id en el repositorio si se necesita
        // viewModelScope.launch {
        //     val result = productRepository.getProductById(productId)
        //     if (result.isSuccess) {
        //         _product.value = result.getOrNull()
        //     } else {
        //         _product.value = null
        //     }
        // }
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


    sealed class DeleteState {
        object Idle : DeleteState()
        object Loading : DeleteState()
        object Success : DeleteState()
        data class Error(val message: String) : DeleteState()
    }
}
