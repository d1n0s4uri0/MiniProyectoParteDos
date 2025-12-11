package com.example.miniproyectoparte2.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyectoparte2.data.model.Product
import com.example.miniproyectoparte2.data.repository.AuthRepository
import com.example.miniproyectoparte2.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _saveState = MutableLiveData<SaveState>()
    val saveState: LiveData<SaveState> = _saveState

    private val _isFormValid = MutableLiveData<Boolean>(false)
    val isFormValid: LiveData<Boolean> = _isFormValid


    fun saveProduct(code: String, name: String, price: String, quantity: String) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _saveState.value = SaveState.Error("Usuario no autenticado")
                return@launch
            }

            val product = Product(
                code = code,
                name = name,
                price = price.toDoubleOrNull() ?: 0.0,
                quantity = quantity.toIntOrNull() ?: 0
            )

            val result = productRepository.addProduct(product, userId)

            _saveState.value = if (result.isSuccess) {
                SaveState.Success
            } else {
                SaveState.Error("Error al guardar producto")
            }
        }
    }


    fun validateForm(code: String, name: String, price: String, quantity: String) {
        val isCodeValid = code.isNotEmpty() &&
                code.length <= 4 &&
                code.all { it.isDigit() }

        val isNameValid = name.isNotEmpty() && name.length <= 40

        val isPriceValid = price.isNotEmpty() &&
                price.length <= 20 &&
                price.all { it.isDigit() }

        val isQuantityValid = quantity.isNotEmpty() &&
                quantity.length <= 4 &&
                quantity.all { it.isDigit() }

        _isFormValid.value = isCodeValid && isNameValid && isPriceValid && isQuantityValid
    }

    sealed class SaveState {
        object Idle : SaveState()
        object Loading : SaveState()
        object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }
}