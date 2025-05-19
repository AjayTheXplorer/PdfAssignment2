package com.example.pdfassignment2.views

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pdfassignment2.R
import com.example.pdfassignment2.databinding.ActivityFullScreenImageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = imageUriString?.let { Uri.parse(it) }

        imageUri?.let {
            binding.fullScreenImageView.setImageURI(it)
        }

    }
}