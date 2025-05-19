package com.example.pdfassignment2.model.apiService

import com.example.pdfassignment2.model.apiModel.ProductResponse
import retrofit2.http.GET

interface ProductApiService {
    @GET("objects")
    suspend fun getProducts(): List<ProductResponse>
}
