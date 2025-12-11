package com.example.miniproyectoparte2

import com.example.miniproyectoparte2.data.model.Product
import com.example.miniproyectoparte2.data.source.ProductDataSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever


@ExperimentalCoroutinesApi
class ProductDataSourceTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Mock
    private lateinit var collectionReference: CollectionReference

    @Mock
    private lateinit var documentReference: DocumentReference

    @Mock
    private lateinit var documentSnapshot: DocumentSnapshot

    private lateinit var productDataSource: ProductDataSource

    private val testUserId = "test_user_123"
    private val testProductId = "product_456"

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(firestore.collection("products")).thenReturn(collectionReference)
        productDataSource = ProductDataSource(firestore)
    }

    // ========== ADD PRODUCT TESTS ==========

    @Test
    fun `addProduct with valid data returns success`() = runTest {
        // Given
        val product = Product(
            code = "1234",
            name = "Test Product",
            price = 100.0,
            quantity = 10
        )
        whenever(collectionReference.add(any())).thenReturn(Tasks.forResult(documentReference))

        // When
        val result = productDataSource.addProduct(product, testUserId)

        // Then
        assertTrue("Should add product successfully", result.isSuccess)
        verify(collectionReference).add(any())
    }

    @Test
    fun `addProduct with firestore error returns failure`() = runTest {
        // Given
        val product = Product(
            code = "1234",
            name = "Test Product",
            price = 100.0,
            quantity = 10
        )
        val exception = Exception("Firestore error")
        whenever(collectionReference.add(any())).thenReturn(Tasks.forException(exception))

        // When
        val result = productDataSource.addProduct(product, testUserId)

        // Then
        assertTrue("Should fail when Firestore fails", result.isFailure)
        assertNotNull("Should contain exception", result.exceptionOrNull())
    }

    @Test
    fun `addProduct includes userId in data`() = runTest {
        // Given
        val product = Product(
            code = "1234",
            name = "Test Product",
            price = 100.0,
            quantity = 10
        )
        whenever(collectionReference.add(any())).thenReturn(Tasks.forResult(documentReference))

        // When
        productDataSource.addProduct(product, testUserId)

        // Then
        verify(collectionReference).add(argThat { map ->
            map is Map<*, *> && map["userId"] == testUserId
        })
    }

    // ========== UPDATE PRODUCT TESTS ==========

    @Test
    fun `updateProduct with valid data returns success`() = runTest {
        // Given
        val product = Product(
            id = testProductId,
            code = "1234",
            name = "Updated Product",
            price = 150.0,
            quantity = 20
        )
        whenever(collectionReference.document(testProductId)).thenReturn(documentReference)
        whenever(documentReference.update(any<Map<String, Any>>())).thenReturn(Tasks.forResult(null))

        // When
        val result = productDataSource.updateProduct(product)

        // Then
        assertTrue("Should update product successfully", result.isSuccess)
        verify(documentReference).update(any<Map<String, Any>>())
    }

    @Test
    fun `updateProduct with firestore error returns failure`() = runTest {
        // Given
        val product = Product(
            id = testProductId,
            code = "1234",
            name = "Updated Product",
            price = 150.0,
            quantity = 20
        )
        val exception = Exception("Update failed")
        whenever(collectionReference.document(testProductId)).thenReturn(documentReference)
        whenever(documentReference.update(any<Map<String, Any>>())).thenReturn(Tasks.forException(exception))

        // When
        val result = productDataSource.updateProduct(product)

        // Then
        assertTrue("Should fail when update fails", result.isFailure)
        assertNotNull("Should contain exception", result.exceptionOrNull())
    }

    // ========== DELETE PRODUCT TESTS ==========

    @Test
    fun `deleteProduct with valid id returns success`() = runTest {
        // Given
        whenever(collectionReference.document(testProductId)).thenReturn(documentReference)
        whenever(documentReference.delete()).thenReturn(Tasks.forResult(null))

        // When
        val result = productDataSource.deleteProduct(testProductId)

        // Then
        assertTrue("Should delete product successfully", result.isSuccess)
        verify(documentReference).delete()
    }

    @Test
    fun `deleteProduct with firestore error returns failure`() = runTest {
        // Given
        val exception = Exception("Delete failed")
        whenever(collectionReference.document(testProductId)).thenReturn(documentReference)
        whenever(documentReference.delete()).thenReturn(Tasks.forException(exception))

        // When
        val result = productDataSource.deleteProduct(testProductId)

        // Then
        assertTrue("Should fail when delete fails", result.isFailure)
        assertNotNull("Should contain exception", result.exceptionOrNull())
    }

    @Test
    fun `deleteProduct calls correct document`() = runTest {
        // Given
        val specificId = "specific_product_789"
        whenever(collectionReference.document(specificId)).thenReturn(documentReference)
        whenever(documentReference.delete()).thenReturn(Tasks.forResult(null))

        // When
        productDataSource.deleteProduct(specificId)

        // Then
        verify(collectionReference).document(specificId)
        verify(documentReference).delete()
    }
}