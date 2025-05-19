package com.example.pdfassignment2.repository

import android.util.Log
import com.example.pdfassignment2.model.apiService.ProductApiService
import com.example.pdfassignment2.model.localDB.dao.ProductDao
import com.example.pdfassignment2.model.localDB.entity.ProductEntity
import com.google.gson.Gson
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val api: ProductApiService,
    private val dao: ProductDao
) {
    val allProducts = dao.getAllProducts()

    suspend fun fetchAndSaveProducts() {
        try {
            val response = api.getProducts()
            val entities = response.map {
                ProductEntity(
                    id = it.id,
                    name = it.name,
                    data = Gson().toJson(it.data)
                )
            }
            dao.insertAll(entities)
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching products", e)
        }
    }

    suspend fun updateProduct(product: ProductEntity) = dao.updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = dao.deleteProduct(product)
}
