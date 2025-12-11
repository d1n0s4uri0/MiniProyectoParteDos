package com.example.miniproyectoparte2.data.repository

import com.example.miniproyectoparte2.data.model.Product
import com.example.miniproyectoparte2.data.source.ProductDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDataSource: ProductDataSource
) {

    fun getProducts(userId: String): Flow<List<Product>> {
        return productDataSource.getProducts(userId)
    }

    suspend fun addProduct(product: Product, userId: String): Result<Unit> {
        return productDataSource.addProduct(product, userId)
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        return productDataSource.updateProduct(product)
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return productDataSource.deleteProduct(productId)
    }

    fun calculateTotalInventory(products: List<Product>): Double {
        return products.sumOf { it.getTotal() }
    }
}
