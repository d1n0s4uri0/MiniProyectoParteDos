package com.example.miniproyectoparte2.ui.home

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
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error


    init {
        loadProducts()
    }


    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val userId = authRepository.getCurrentUserId()

            if (userId == null) {
                _error.value = "Usuario no autenticado"
                _isLoading.value = false
                return@launch
            }

            try {
                productRepository.getProducts(userId).collect { productList ->
                    _products.value = productList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar productos"
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}