package com.example.miniproyectoparte2.ui.edit

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.miniproyectoparte2.data.model.Product
import com.example.miniproyectoparte2.databinding.ActivityEditProductBinding
import com.example.miniproyectoparte2.ui.detail.DetailProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private val viewModel: DetailProductViewModel by viewModels()

    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbarEditar)
        binding.toolbarEditar.setNavigationOnClickListener { finish() }

        productId = intent.getStringExtra("productId")

        // Cargar el producto con ese id
        productId?.let { id ->
            viewModel.loadProduct(id)
        }

        setupObservers()
        setupListeners()
    }


    private fun setupObservers() {
        viewModel.product.observe(this) { product ->
            product ?: return@observe


            binding.edtNombre.setText(product.name)
            binding.edtPrecio.setText(product.price.toString())
            binding.edtCantidad.setText(product.quantity.toString())
        }

        viewModel.updateState.observe(this) { state ->
            when (state) {
                is DetailProductViewModel.UpdateState.Loading -> {
                    binding.btnEditar.isEnabled = false
                }
                is DetailProductViewModel.UpdateState.Success -> {
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is DetailProductViewModel.UpdateState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    binding.btnEditar.isEnabled = true
                }
                is DetailProductViewModel.UpdateState.Idle -> Unit
            }
        }
    }

    private fun setupListeners() {
        fun validate() {
            val ok = binding.edtNombre.text!!.isNotBlank() &&
                    binding.edtPrecio.text!!.isNotBlank() &&
                    binding.edtCantidad.text!!.isNotBlank()
            binding.btnEditar.isEnabled = ok
        }

        listOf(binding.edtNombre, binding.edtPrecio, binding.edtCantidad).forEach {
            it.addTextChangedListener { validate() }
        }

        binding.btnEditar.setOnClickListener {
            val id = productId ?: return@setOnClickListener

            val nombre = binding.edtNombre.text.toString()
            val precio = binding.edtPrecio.text.toString().toDouble()
            val cantidad = binding.edtCantidad.text.toString().toInt()

            viewModel.updateProduct(id, nombre, precio, cantidad)
        }
    }
}
