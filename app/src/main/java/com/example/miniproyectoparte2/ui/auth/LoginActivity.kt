package com.example.miniproyectoparte2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.miniproyectoparte2.R
import com.example.miniproyectoparte2.databinding.ActivityLoginBinding
import com.example.miniproyectoparte2.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        setupListeners()

        // Verificar si ya está logueado (Criterio 1 HU 3.0)
        if (viewModel.isUserLoggedIn()) {
            navigateToHome()
        }
    }

    private fun setupUI() {
        // Criterio 1: Fondo negro, sin toolbar
        supportActionBar?.hide()
    }

    private fun setupObservers() {
        // Observar estado de login
        viewModel.loginState.observe(this) { state ->
            handleLoginState(state)
        }

        // Observar estado de registro
        viewModel.registerState.observe(this) { state ->
            handleRegisterState(state)
        }

        // Observar validación del formulario
        viewModel.isFormValid.observe(this) { isValid ->
            updateButtonsState(isValid)
        }

        // Observar error de password
        viewModel.passwordError.observe(this) { error ->
            binding.passwordTextInputLayout.error = error

            // Criterio 5: Borde rojo si hay error
            val borderColor = if (error != null) {
                ContextCompat.getColor(this, R.color.error_red)
            } else {
                ContextCompat.getColor(this, android.R.color.white)
            }
            // Aquí aplicarías el color al borde
        }
    }

    private fun setupListeners() {
        // TextWatchers para validación en tiempo real
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        }

        binding.emailEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)

        // Botón Login (Criterio 9)
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(email, password)
        }

        // Botón Registrarse (Criterio 13, 14)
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.register(email, password)
        }

        // Toggle visibilidad password (Criterio 6)
        binding.passwordToggleIcon.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun validateForm() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        viewModel.validateForm(email, password)
    }

    private fun updateButtonsState(isValid: Boolean) {
        // Criterio 7, 11, 12: Habilitar/deshabilitar botones
        binding.loginButton.isEnabled = isValid
        binding.registerButton.isEnabled = isValid

        val textColor = if (isValid) {
            ContextCompat.getColor(this, android.R.color.white)
        } else {
            ContextCompat.getColor(this, R.color.gray)
        }

        binding.loginButton.setTextColor(textColor)
        binding.registerButton.setTextColor(textColor)
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            binding.passwordEditText.inputType =
                InputType.TYPE_CLASS_NUMBER
            binding.passwordToggleIcon.setImageResource(R.drawable.ic_eye_closed)
        } else {
            binding.passwordEditText.inputType =
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD
            binding.passwordToggleIcon.setImageResource(R.drawable.ic_eye_open)
        }

        // Mover cursor al final
        binding.passwordEditText.setSelection(binding.passwordEditText.text?.length ?: 0)
    }

    private fun handleLoginState(state: LoginViewModel.LoginState) {
        when (state) {
            is LoginViewModel.LoginState.Loading -> {
                showLoading(true)
            }
            is LoginViewModel.LoginState.Success -> {
                showLoading(false)
                navigateToHome()
            }
            is LoginViewModel.LoginState.Error -> {
                showLoading(false)
                // Criterio 9: Toast con error
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
            }
            is LoginViewModel.LoginState.Idle -> {
                showLoading(false)
            }
        }
    }

    private fun handleRegisterState(state: LoginViewModel.RegisterState) {
        when (state) {
            is LoginViewModel.RegisterState.Loading -> {
                showLoading(true)
            }
            is LoginViewModel.RegisterState.Success -> {
                showLoading(false)
                // Criterio 14: Ir a Home después de registro exitoso
                navigateToHome()
            }
            is LoginViewModel.RegisterState.Error -> {
                showLoading(false)
                // Criterio 13: Toast con error
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
            }
            is LoginViewModel.RegisterState.Idle -> {
                showLoading(false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !show
        binding.registerButton.isEnabled = !show
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}