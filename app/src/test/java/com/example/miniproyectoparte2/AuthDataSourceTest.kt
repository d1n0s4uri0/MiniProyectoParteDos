package com.example.miniproyectoparte2


import com.example.miniproyectoparte2.TestCoroutineRule
import com.example.miniproyectoparte2.data.source.AuthDataSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
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
class AuthDataSourceTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var authResult: AuthResult

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var authDataSource: AuthDataSource

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authDataSource = AuthDataSource(firebaseAuth)
    }

    // ========== LOGIN TESTS ==========

    @Test
    fun `login with valid credentials returns success result`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "123456"
        whenever(authResult.user).thenReturn(firebaseUser)
        whenever(firebaseAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(authResult))

        // When
        val result = authDataSource.login(email, password)

        // Then
        assertTrue("Login should be successful", result.isSuccess)
        assertEquals("Should return the FirebaseUser", firebaseUser, result.getOrNull())
        verify(firebaseAuth).signInWithEmailAndPassword(email, password)
    }

    @Test
    fun `login with invalid credentials returns failure result`() = runTest {
        // Given
        val email = "wrong@example.com"
        val password = "wrongpass"
        val exception = Exception("Invalid credentials")
        whenever(firebaseAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forException(exception))

        // When
        val result = authDataSource.login(email, password)

        // Then
        assertTrue("Login should fail", result.isFailure)
        assertNotNull("Should contain exception", result.exceptionOrNull())
    }

    // ========== REGISTER TESTS ==========

    @Test
    fun `register with valid data returns success result`() = runTest {
        // Given
        val email = "newuser@example.com"
        val password = "123456"
        whenever(authResult.user).thenReturn(firebaseUser)
        whenever(firebaseAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(authResult))

        // When
        val result = authDataSource.register(email, password)

        // Then
        assertTrue("Registration should be successful", result.isSuccess)
        assertEquals("Should return the FirebaseUser", firebaseUser, result.getOrNull())
        verify(firebaseAuth).createUserWithEmailAndPassword(email, password)
    }

    @Test
    fun `register with existing email returns failure result`() = runTest {
        // Given
        val email = "existing@example.com"
        val password = "123456"
        val exception = Exception("Email already in use")
        whenever(firebaseAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forException(exception))

        // When
        val result = authDataSource.register(email, password)

        // Then
        assertTrue("Registration should fail", result.isFailure)
        assertNotNull("Should contain exception", result.exceptionOrNull())
    }

    // ========== GET CURRENT USER TESTS ==========

    @Test
    fun `getCurrentUser returns user when logged in`() {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        // When
        val result = authDataSource.getCurrentUser()

        // Then
        assertEquals("Should return current user", firebaseUser, result)
        verify(firebaseAuth).currentUser
    }

    @Test
    fun `getCurrentUser returns null when not logged in`() {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)

        // When
        val result = authDataSource.getCurrentUser()

        // Then
        assertNull("Should return null when no user", result)
    }

    // ========== IS USER LOGGED IN TESTS ==========

    @Test
    fun `isUserLoggedIn returns true when user exists`() {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        // When
        val result = authDataSource.isUserLoggedIn()

        // Then
        assertTrue("Should return true when user exists", result)
    }

    @Test
    fun `isUserLoggedIn returns false when user is null`() {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)

        // When
        val result = authDataSource.isUserLoggedIn()

        // Then
        assertFalse("Should return false when no user", result)
    }

    // ========== LOGOUT TEST ==========

    @Test
    fun `logout calls firebase signOut`() {
        // When
        authDataSource.logout()

        // Then
        verify(firebaseAuth).signOut()
    }

    @Test
    fun `logout can be called multiple times`() {
        // When
        authDataSource.logout()
        authDataSource.logout()
        authDataSource.logout()

        // Then
        verify(firebaseAuth, times(3)).signOut()
    }
}