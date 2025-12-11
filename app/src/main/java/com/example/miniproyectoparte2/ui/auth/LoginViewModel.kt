package com.example.miniproyectoparte2.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyectoparte2.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // ========== Estados de UI ==========

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    private val _isFormValid = MutableLiveData<Boolean>(false)
    val isFormValid: LiveData<Boolean> = _isFormValid

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    // ========== Login ==========

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val result = authRepository.login(email, password)

            _loginState.value = if (result.isSuccess) {
                LoginState.Success
            } else {
                LoginState.Error("Login incorrecto")
            }
        }
    }

    // ========== Register ==========

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            val result = authRepository.register(email, password)

            _registerState.value = if (result.isSuccess) {
                RegisterState.Success
            } else {
                RegisterState.Error("Error en el registro")
            }
        }
    }

    // ========== Validaciones (HU 2.0) ==========

    fun validateForm(email: String, password: String) {
        // Criterio 3: Email máximo 40 caracteres
        val isEmailValid = email.isNotEmpty() && email.length <= 40

        // Criterio 5: Password 6-10 números
        val isPasswordValid = password.length in 6..10 && password.all { it.isDigit() }

        // Criterio 5: Mostrar error si password < 6
        _passwordError.value = when {
            password.isEmpty() -> null
            password.length < 6 -> "Mínimo 6 dígitos"
            !password.all { it.isDigit() } -> "Solo números"
            else -> null
        }

        _isFormValid.value = isEmailValid && isPasswordValid
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    // ========== Estados Sellados ==========

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}