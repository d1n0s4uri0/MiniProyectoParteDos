package com.example.miniproyectoparte2.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miniproyectoparte2.databinding.ActivityHomeBinding
import com.example.miniproyectoparte2.ui.auth.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        checkUserSession()
    }

    private fun setupUI() {
        supportActionBar?.hide()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            // Criterio 8 HU 3.0: Click en item va a detalle
            navigateToDetail(product.id)
        }

        binding.productsRecyclerView.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(this@HomeActivity)
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            productAdapter.submitList(products)
            updateEmptyState(products.isEmpty())
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                updateEmptyState(true)
            }
        }
    }

    private fun setupListeners() {
        // Criterio 3 HU 3.0: Logout
        binding.logoutIcon.setOnClickListener {
            logout()
        }

        // Criterio 7 HU 3.0: Agregar producto
        binding.addProductFab.setOnClickListener {
            navigateToAddProduct()
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.productsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun checkUserSession() {
        // Criterio 1 HU 3.0: Redirigir a login si no hay sesión
        if (!viewModel.isUserLoggedIn()) {
            navigateToLogin()
        }
    }

    private fun logout() {
        viewModel.logout()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToAddProduct() {
    }

    private fun navigateToDetail(productId: String) {
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}