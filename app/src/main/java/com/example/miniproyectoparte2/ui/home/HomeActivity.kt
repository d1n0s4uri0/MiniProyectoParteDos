package com.example.miniproyectoparte2.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.miniproyectoparte2.databinding.ActivityHomeBinding
import com.example.miniproyectoparte2.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()

        // Verificar que el usuario esté logueado (Criterio 1 HU 3.0)
        checkUserSession()
    }

    private fun setupUI() {
        // Ocultar ActionBar por defecto (usamos toolbar personalizado)
        supportActionBar?.hide()
    }

    private fun setupListeners() {
        // Criterio 3 HU 3.0: Logout
        binding.logoutIcon.setOnClickListener {
            logout()
        }
    }

    private fun checkUserSession() {
        // Criterio 1 HU 3.0: Si no hay sesión, redirigir a Login
        if (auth.currentUser == null) {
            navigateToLogin()
        }
    }

    private fun logout() {
        // Cerrar sesión
        auth.signOut()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Limpiar el back stack para que no pueda volver con el botón atrás
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Criterio 4 HU 3.0: Manejar botón atrás del teléfono
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Enviar a home del sistema, no al login
        moveTaskToBack(true)
    }
}