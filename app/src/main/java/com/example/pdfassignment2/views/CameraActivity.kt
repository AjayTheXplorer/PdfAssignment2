package com.example.pdfassignment2.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pdfassignment2.databinding.ActivityCameraBinding
import com.example.pdfassignment2.repository.ImageRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    @Inject
    lateinit var imageRepository: ImageRepository

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>

    private var selectedImageUri: Uri? = null
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLaunchers()

        binding.btnCapture.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        binding.btnGallery.setOnClickListener {
            openGallery()
        }

        binding.imageView.setOnClickListener {
            selectedImageUri?.let { uri ->
                val intent = Intent(this, FullScreenImageActivity::class.java)
                intent.putExtra("imageUri", uri.toString())
                startActivity(intent)
            }
        }
    }

    private fun setupLaunchers() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUri != null) {
                selectedImageUri = photoUri
                val bitmap = imageRepository.getBitmapFromUri(photoUri!!)
                binding.imageView.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                val bitmap = imageRepository.getBitmapFromUri(uri)
                binding.imageView.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val photoFile = imageRepository.createImageFile()
        if (photoFile != null) {
            photoUri = imageRepository.getPhotoUri(photoFile)
            cameraLauncher.launch(photoUri!!)
        } else {
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
}
