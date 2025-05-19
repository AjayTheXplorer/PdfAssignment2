package com.example.pdfassignment2.model.localDB.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val data: String? // Store Map as JSON string
)