package com.example.pdfassignment2.views

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdfassignment2.R
import com.example.pdfassignment2.databinding.ActivityShowProductBinding
import com.example.pdfassignment2.model.localDB.entity.ProductEntity
import com.example.pdfassignment2.preferences.PreferencesManager
import com.example.pdfassignment2.repository.FcmRepository
import com.example.pdfassignment2.viewModel.ProductViewModel
import com.example.pdfassignment2.views.adapter.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.CustomInjection.inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ShowProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowProductBinding
    private lateinit var adapter: ProductAdapter
    private val productViewModel: ProductViewModel by viewModels()
    @Inject
    lateinit var fcmRepository: FcmRepository
    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = ProductAdapter(
            onUpdateClick = { product -> showUpdateDialog(this, product, productViewModel) },
            onDeleteClick = { product -> onDeleteProduct(product) }  // Trigger delete and FCM notification
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe products and submit them to the adapter
        productViewModel.products.observe(this) { productList ->
            adapter.submitList(productList)
        }

        productViewModel.fetchProducts() // Initial API call

        setupNotificationToggle() // Handle notification preference toggle
    }

    private fun onDeleteProduct(product: ProductEntity) {
        lifecycleScope.launch {
            // Before deleting, check notification toggle
            if (preferencesManager.isNotificationEnabled.first()) {
                // Send FCM notification about deletion
                fcmRepository.sendNotification(
                    title = "Product Deleted",
                    body = "${product.name} has been deleted"
                )
            }

            // Now proceed with the deletion
            productViewModel.deleteProduct(product)
        }
    }

    private fun setupNotificationToggle() {
        lifecycleScope.launch {
            preferencesManager.isNotificationEnabled.collect { isEnabled ->
                binding.notificationSwitch.isChecked = isEnabled
            }
        }

        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                preferencesManager.setNotificationEnabled(isChecked)
            }
        }
    }

    private fun updateProduct(product: ProductEntity) {
        val updated = product.copy(name = "${product.name} (Updated)")
        productViewModel.updateProduct(updated)
        Toast.makeText(this, "Updated ${product.name}", Toast.LENGTH_SHORT).show()
    }

    private fun showUpdateDialog(context: Context, product: ProductEntity, viewModel: ProductViewModel) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_product, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editProductName)
        val dataEditText = dialogView.findViewById<EditText>(R.id.editProductData)
        val updateButton = dialogView.findViewById<Button>(R.id.updateButton)

        nameEditText.setText(product.name)
        dataEditText.setText(product.data)

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        updateButton.setOnClickListener {
            val updatedName = nameEditText.text.toString().trim()
            val updatedData = dataEditText.text.toString().trim()

            if (updatedName.isNotEmpty()) {
                val updatedProduct = product.copy(
                    name = updatedName,
                    data = if (updatedData.isNotEmpty()) updatedData else null  // Save updated data properly
                )
                viewModel.updateProduct(updatedProduct)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Product name can't be empty", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }
}

