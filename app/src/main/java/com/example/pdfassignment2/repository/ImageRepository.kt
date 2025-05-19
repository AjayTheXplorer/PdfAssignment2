package com.example.pdfassignment2.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    var currentPhotoPath: String? = null

    fun createImageFile(): File? {
        return try {
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(
                "JPEG_${System.currentTimeMillis()}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).apply {
                currentPhotoPath = absolutePath
            }
        } catch (ex: IOException) {
            null
        }
    }

    fun getPhotoUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            null
        }
    }
}