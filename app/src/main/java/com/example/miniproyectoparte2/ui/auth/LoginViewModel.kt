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


    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    private val _isFormValid = MutableLiveData<Boolean>(false)
    val isFormValid: LiveData<Boolean> = _isFormValid

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError


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


    fun validateForm(email: String, password: String) {
        val isEmailValid = email.isNotEmpty() && email.length <= 40

        val isPasswordValid = password.length in 6..10 && password.all { it.isDigit() }

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