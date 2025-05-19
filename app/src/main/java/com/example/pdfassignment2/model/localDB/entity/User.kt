package com.example.pdfassignment2.model.localDB.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String, // Firebase user ID
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "user_email") val userEmail: String,
)