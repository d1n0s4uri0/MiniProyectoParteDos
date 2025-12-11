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

        binding.toolbarDetalle.setNavigationOnClickListener { finish() }

        productId = intent.getStringExtra("productId")

        // 1. Recibir el Product completo que mandas desde HomeActivity
        val productFromIntent =
            intent.getSerializableExtra("product") as? com.example.miniproyectoparte2.data.model.Product
        productFromIntent?.let { viewModel.setProductFromIntent(it) }

        // 2. Observar y listeners
        setupObservers()
        setupListeners()

        // 3. loadProduct ya no hace falta para mostrar datos, puedes quitar esto
        // productId?.let { id ->
        //     viewModel.loadProduct(id)
        // }
    }


    private fun setupObservers() {
        viewModel.product.observe(this) { product ->
            if (product == null) return@observe

            // Nombre en grande
            binding.txtNombre.text = product.name

            // Formato de precio, cantidad y total como en el ejemplo
            val precioTexto = "Precio Unidad :    $ ${product.price}"
            val cantidadTexto = "Cantidad Disponible:    ${product.quantity}"
            val total = product.price * product.quantity
            val totalTexto = "Total:    $ $total"

            binding.txtPrecio.text = precioTexto
            binding.txtCantidad.text = cantidadTexto
            binding.txtTotal.text = totalTexto
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
            intent.putExtra("product", current)   // enviar el objeto completo
            startActivity(intent)
        }



    }
}
