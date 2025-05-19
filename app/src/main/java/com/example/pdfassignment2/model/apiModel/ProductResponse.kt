package com.example.pdfassignment2.model.apiModel

data class ProductResponse(
    val id: String,
    val name: String,
    val data: Map<String, Any>?
)