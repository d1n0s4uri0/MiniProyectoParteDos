package com.example.miniproyectoparte2.ui.detail

import android.content.Intent
import android.os.Bundle
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

        //setSupportActionBar(binding.toolbarDetalle)
        binding.toolbarDetalle.setNavigationOnClickListener { finish() }

        productId = intent.getStringExtra("productId")

        setupObservers()
        setupListeners()

        productId?.let { id ->
            viewModel.loadProduct(id)
        }
    }

    private fun setupObservers() {
        viewModel.product.observe(this) { product ->
            product ?: return@observe


            binding.txtNombre.text = product.name
            binding.txtPrecio.text = product.price.toString()
            binding.txtCantidad.text = product.quantity.toString()
        }

        viewModel.deleteState.observe(this) { state ->
            when (state) {
                is DetailProductViewModel.DeleteState.Loading -> {
                    binding.btnEliminar.isEnabled = false
                }
                is DetailProductViewModel.DeleteState.Success -> {
                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is DetailProductViewModel.DeleteState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    binding.btnEliminar.isEnabled = true
                }
                is DetailProductViewModel.DeleteState.Idle -> Unit
            }
        }
    }

    private fun setupListeners() {
        binding.btnEliminar.setOnClickListener {
            productId?.let { id ->
                viewModel.deleteProduct(id)
            }
        }

        // FAB Editar: abre EditProductActivity con el producto actual
        binding.fabEditar.setOnClickListener {
            val current = viewModel.product.value ?: return@setOnClickListener

            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra("productId", current.id)
            startActivity(intent)
        }


    }
}
