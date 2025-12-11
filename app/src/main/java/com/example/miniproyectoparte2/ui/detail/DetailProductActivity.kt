package com.example.miniproyectoparte2.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.miniproyectoparte2.databinding.ActivityDetailProductBinding
import com.example.miniproyectoparte2.ui.edit.EditProductActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private val viewModel: DetailProductViewModel by viewModels()

    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        setupListeners()

        productId = intent.getStringExtra("productId")
        productId?.let { viewModel.loadProduct(it) }
    }

    private fun setupUI() {
        supportActionBar?.hide()

        // No tienes toolbarTitle → el título ya está en el XML
        // No tienes toolbarBack → solo usa el navigationIcon del MaterialToolbar
        binding.toolbarDetalle.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.product.observe(this) { product ->
            if (product != null) {

                binding.txtNombre.text = product.name
                binding.txtPrecio.text = "Precio unitario: $${product.price}"
                binding.txtCantidad.text = "Cantidad: ${product.quantity}"

                val total = product.price * product.quantity
                binding.txtTotal.text = "Total: $$total"
            }
        }

        viewModel.deleteState.observe(this) { state ->
            handleDeleteState(state)
        }
    }

    private fun setupListeners() {

        binding.btnEliminar.setOnClickListener {
            productId?.let { id ->
                viewModel.deleteProduct(id)
            }
        }

        // FAB Editar
        binding.fabEditar.setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra("productId", productId)
            startActivity(intent)
        }
    }

    private fun handleDeleteState(state: DetailProductViewModel.DeleteState) {
        when (state) {
            is DetailProductViewModel.DeleteState.Loading -> {
                showLoading(true)
            }
            is DetailProductViewModel.DeleteState.Success -> {
                showLoading(false)
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                finish()
            }
            is DetailProductViewModel.DeleteState.Error -> {
                showLoading(false)
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
            }
            is DetailProductViewModel.DeleteState.Idle -> {
                showLoading(false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
       binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE

        binding.btnEliminar.isEnabled = !show
        binding.fabEditar.isEnabled = !show
    }
}
