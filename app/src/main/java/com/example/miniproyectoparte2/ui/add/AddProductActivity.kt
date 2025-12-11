package com.example.miniproyectoparte2.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.miniproyectoparte2.databinding.ActivityAddProductBinding
import com.example.miniproyectoparte2.ui.widget.InventoryWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: AddProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        supportActionBar?.hide()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.saveState.observe(this) { state ->
            handleSaveState(state)
        }

        viewModel.isFormValid.observe(this) { isValid ->
            updateButtonState(isValid)
        }
    }

    private fun setupListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        }

        binding.codeEditText.addTextChangedListener(textWatcher)
        binding.nameEditText.addTextChangedListener(textWatcher)
        binding.priceEditText.addTextChangedListener(textWatcher)
        binding.quantityEditText.addTextChangedListener(textWatcher)

        binding.saveButton.setOnClickListener {
            saveProduct()
        }
    }

    private fun validateForm() {
        val code = binding.codeEditText.text.toString()
        val name = binding.nameEditText.text.toString()
        val price = binding.priceEditText.text.toString()
        val quantity = binding.quantityEditText.text.toString()

        viewModel.validateForm(code, name, price, quantity)
    }

    private fun updateButtonState(isValid: Boolean) {
        binding.saveButton.isEnabled = isValid

        val textColor = if (isValid) {
            ContextCompat.getColor(this, android.R.color.white)
        } else {
            ContextCompat.getColor(this, android.R.color.darker_gray)
        }

        binding.saveButton.setTextColor(textColor)
    }

    private fun saveProduct() {
        val code = binding.codeEditText.text.toString()
        val name = binding.nameEditText.text.toString()
        val price = binding.priceEditText.text.toString()
        val quantity = binding.quantityEditText.text.toString()

        viewModel.saveProduct(code, name, price, quantity)
    }

    private fun handleSaveState(state: AddProductViewModel.SaveState) {
        when (state) {
            is AddProductViewModel.SaveState.Loading -> {
                showLoading(true)
            }
            is AddProductViewModel.SaveState.Success -> {
                showLoading(false)
                Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show()
                InventoryWidget.updateWidget(this)
                finish()
            }
            is AddProductViewModel.SaveState.Error -> {
                showLoading(false)
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
            }
            is AddProductViewModel.SaveState.Idle -> {
                showLoading(false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.saveButton.isEnabled = !show
        binding.codeEditText.isEnabled = !show
        binding.nameEditText.isEnabled = !show
        binding.priceEditText.isEnabled = !show
        binding.quantityEditText.isEnabled = !show
    }
}