package com.example.miniproyectoparte2.data.source

import com.example.miniproyectoparte2.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val productsCollection = firestore.collection("products")

    fun getProducts(userId: String): Flow<List<Product>> = callbackFlow {
        val subscription = productsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(products)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addProduct(product: Product, userId: String): Result<Unit> {
        return try {
            val productData = hashMapOf(
                "code" to product.code,
                "name" to product.name,
                "price" to product.price,
                "quantity" to product.quantity,
                "userId" to userId
            )
            productsCollection.add(productData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            val productData = hashMapOf(
                "code" to product.code,
                "name" to product.name,
                "price" to product.price,
                "quantity" to product.quantity
            )
            productsCollection.document(product.id).update(productData as Map<String, Any>).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}