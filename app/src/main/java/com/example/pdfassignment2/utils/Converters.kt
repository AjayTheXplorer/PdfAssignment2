package com.example.pdfassignment2.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromMap(map: Map<String, Any>?): String? {
        return Gson().toJson(map)
    }

    @TypeConverter
    fun toMap(json: String?): Map<String, Any>? {
        return json?.let {
            Gson().fromJson(it, object : TypeToken<Map<String, Any>>() {}.type)
        }
    }
}
