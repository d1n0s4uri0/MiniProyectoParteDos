package com.example.miniproyectoparte2.data.repository

import com.google.firebase.auth.FirebaseUser
import com.example.miniproyectoparte2.data.source.AuthDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return authDataSource.login(email, password)
    }

    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return authDataSource.register(email, password)
    }

    fun getCurrentUser(): FirebaseUser? {
        return authDataSource.getCurrentUser()
    }

    fun logout() {
        authDataSource.logout()
    }

    fun isUserLoggedIn(): Boolean {
        return authDataSource.isUserLoggedIn()
    }

    fun getCurrentUserId(): String? {
        return authDataSource.getCurrentUser()?.uid
    }
}