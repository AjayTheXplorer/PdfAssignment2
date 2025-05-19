package com.example.pdfassignment2.views.adapter

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pdfassignment2.R
import com.example.pdfassignment2.model.localDB.entity.ProductEntity
import com.example.pdfassignment2.viewModel.ProductViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductAdapter(
    private val onUpdateClick: (ProductEntity) -> Unit,
    private val onDeleteClick: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductAdapter.ProductViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTxt = itemView.findViewById<TextView>(R.id.productName)
        private val dataTxt = itemView.findViewById<TextView>(R.id.productData)
        val updateBtn = itemView.findViewById<Button>(R.id.btnUpdate)
        val deleteBtn = itemView.findViewById<Button>(R.id.btnDelete)

        fun bind(product: ProductEntity) {
            nameTxt.text = product.name
            val json = product.data
            if (!json.isNullOrEmpty()) {
                try {
                    val map: Map<String, Any> = Gson().fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
                    dataTxt.text = map.entries.joinToString("\n \n") { "${it.key}: ${it.value}" }
                } catch (e: Exception) {
                    e.printStackTrace()
                    dataTxt.text = "Data not found"
                }
            } else {
                dataTxt.text = "No product details available"
            }

            // Set update listener
            updateBtn.setOnClickListener { onUpdateClick(product) }

            // Set delete listener with AlertDialog and notification
            deleteBtn.setOnClickListener {
                val context = itemView.context

                // Check notification permission for Android 13 and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            1001
                        )
                        return@setOnClickListener
                    }
                }

                // Show confirmation dialog
                AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete '${product.name}'?")
                    .setPositiveButton("Yes") { _, _ ->

                        //  local notification stopped

//                                    // Trigger local notification
//                                    val notificationManager =
//                                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//                                    // Create Notification Channel (for Android O+)
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                        val channel = NotificationChannel(
//                                            "delete_channel",
//                                            "Delete Notifications",
//                                            NotificationManager.IMPORTANCE_DEFAULT
//                                        ).apply {
//                                            description = "Notifies when a product is deleted"
//                                        }
//                                        notificationManager.createNotificationChannel(channel)
//                                    }
//
//                                    // Build and show notification
//                                    val notification = NotificationCompat.Builder(context, "delete_channel")
//                                        .setSmallIcon(android.R.drawable.ic_delete) // Use system delete icon for testing
//                                        .setContentTitle("Product Deleted")
//                                        .setContentText("The product '${product.name}' has been deleted.")
//                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                                        .build()
//
//                                    notificationManager.notify(product.id.hashCode(), notification)

                        // Call delete callback to trigger deletion
                        onDeleteClick(product)
                    }
                    .setNegativeButton("No", null) // Do nothing on "No"
                    .show()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductEntity>() {
        override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity) =
            oldItem == newItem
    }
}


