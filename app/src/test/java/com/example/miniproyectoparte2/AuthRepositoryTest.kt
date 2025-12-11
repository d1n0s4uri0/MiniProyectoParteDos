package com.example.miniproyectoparte2



import com.example.miniproyectoparte2.data.repository.AuthRepository
import com.example.miniproyectoparte2.data.source.AuthDataSource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AuthRepositoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var authDataSource: AuthDataSource

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var authRepository: AuthRepository

    private val testEmail = "test@example.com"
    private val testPassword = "123456"
    private val testUserId = "user_123"

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepository(authDataSource)
    }

    // ========== LOGIN TESTS ==========

    @Test
    fun `login successful delegates to datasource and returns success`() = runTest {
        // Given
        val expectedResult = Result.success(firebaseUser)
        whenever(authDataSource.login(testEmail, testPassword)).thenReturn(expectedResult)

        // When
        val result = authRepository.login(testEmail, testPassword)

        // Then
        assertTrue("Login should be successful", result.isSuccess)
        assertEquals("Should return the user", firebaseUser, result.getOrNull())
        verify(authDataSource).login(testEmail, testPassword)
    }

    @Test
    fun `login failure delegates to datasource and returns failure`() = runTest {
        // Given
        val exception = Exception("Login failed")
        val expectedResult = Result.failure<FirebaseUser>(exception)
        whenever(authDataSource.login(testEmail, testPassword)).thenReturn(expectedResult)

        // When
        val result = authRepository.login(testEmail, testPassword)

        // Then
        assertTrue("Login should fail", result.isFailure)
        assertEquals("Should return same exception", exception, result.exceptionOrNull())
        verify(authDataSource).login(testEmail, testPassword)
    }

    // ========== REGISTER TESTS ==========

    @Test
    fun `register successful delegates to datasource and returns success`() = runTest {
        // Given
        val expectedResult = Result.success(firebaseUser)
        whenever(authDataSource.register(testEmail, testPassword)).thenReturn(expectedResult)

        // When
        val result = authRepository.register(testEmail, testPassword)

        // Then
        assertTrue("Registration should be successful", result.isSuccess)
        assertEquals("Should return the user", firebaseUser, result.getOrNull())
        verify(authDataSource).register(testEmail, testPassword)
    }

    @Test
    fun `register failure delegates to datasource and returns failure`() = runTest {
        // Given
        val exception = Exception("Registration failed")
        val expectedResult = Result.failure<FirebaseUser>(exception)
        whenever(authDataSource.register(testEmail, testPassword)).thenReturn(expectedResult)

        // When
        val result = authRepository.register(testEmail, testPassword)

        // Then
        assertTrue("Registration should fail", result.isFailure)
        verify(authDataSource).register(testEmail, testPassword)
    }

    // ========== GET CURRENT USER TESTS ==========

    @Test
    fun `getCurrentUser delegates to datasource and returns user`() {
        // Given
        whenever(authDataSource.getCurrentUser()).thenReturn(firebaseUser)

        // When
        val result = authRepository.getCurrentUser()

        // Then
        assertEquals("Should return current user", firebaseUser, result)
        verify(authDataSource).getCurrentUser()
    }

    @Test
    fun `getCurrentUser returns null when no user logged in`() {
        // Given
        whenever(authDataSource.getCurrentUser()).thenReturn(null)

        // When
        val result = authRepository.getCurrentUser()

        // Then
        assertNull("Should return null when no user", result)
        verify(authDataSource).getCurrentUser()
    }

    // ========== GET CURRENT USER ID TESTS ==========

    @Test
    fun `getCurrentUserId returns user id when user is logged in`() {
        // Given
        whenever(firebaseUser.uid).thenReturn(testUserId)
        whenever(authDataSource.getCurrentUser()).thenReturn(firebaseUser)

        // When
        val result = authRepository.getCurrentUserId()

        // Then
        assertEquals("Should return user ID", testUserId, result)
        verify(authDataSource).getCurrentUser()
    }

    @Test
    fun `getCurrentUserId returns null when no user logged in`() {
        // Given
        whenever(authDataSource.getCurrentUser()).thenReturn(null)

        // When
        val result = authRepository.getCurrentUserId()

        // Then
        assertNull("Should return null when no user", result)
        verify(authDataSource).getCurrentUser()
    }

    // ========== LOGOUT TESTS ==========

    @Test
    fun `logout delegates to datasource`() {
        // When
        authRepository.logout()

        // Then
        verify(authDataSource).logout()
    }

    @Test
    fun `logout can be called multiple times`() {
        // When
        authRepository.logout()
        authRepository.logout()

        // Then
        verify(authDataSource, times(2)).logout()
    }

    // ========== IS USER LOGGED IN TESTS ==========

    @Test
    fun `isUserLoggedIn returns true when user exists`() {
        // Given
        whenever(authDataSource.isUserLoggedIn()).thenReturn(true)

        // When
        val result = authRepository.isUserLoggedIn()

        // Then
        assertTrue("Should return true when logged in", result)
        verify(authDataSource).isUserLoggedIn()
    }

    @Test
    fun `isUserLoggedIn returns false when no user`() {
        // Given
        whenever(authDataSource.isUserLoggedIn()).thenReturn(false)

        // When
        val result = authRepository.isUserLoggedIn()

        // Then
        assertFalse("Should return false when not logged in", result)
        verify(authDataSource).isUserLoggedIn()
    }
}