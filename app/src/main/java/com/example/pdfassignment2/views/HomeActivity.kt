package com.example.pdfassignment2.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfassignment2.MainActivity
import com.example.pdfassignment2.databinding.ActivityHomeBinding
import com.example.pdfassignment2.model.localDB.entity.User
import com.example.pdfassignment2.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Check if a user is already signed in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // No user is signed in, redirect to the sign-in screen (e.g., MainActivity)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return // Exit early since the user is not authenticated
        }

        displayUserDetails(currentUser)
        addUserToDatabase(currentUser)

        binding.btnLogOut.setOnClickListener {
            startActivity(Intent(this, SignOutActivity::class.java))
            finish()
        }
    }

    private fun displayUserDetails(user: FirebaseUser) {
        val name = user.displayName
        val email = user.email

        binding.tvUserName.text = "Hi $name"
        binding.tvUserEmail.text = email
    }

    private fun addUserToDatabase(user: FirebaseUser) {
        val userId = user.uid
        val name = user.displayName ?: ""
        val email = user.email ?: ""

        userViewModel.getUser(userId)

        userViewModel.userFromDb.observe(this) { userFromDb ->
            userViewModel.userFromDb.removeObservers(this) // avoid multiple triggers

            if (userFromDb != null) {
                Toast.makeText(this, "User already in database.", Toast.LENGTH_SHORT).show()
                Log.i("HomeActivity", "User found in the database, nothing to do.")
            } else {
                Toast.makeText(this, "Adding new user to database.", Toast.LENGTH_SHORT).show()
                Log.i("HomeActivity", "User not found in the database, adding it.")
                val newUser = User(userId = userId, userName = name, userEmail = email)
                userViewModel.addUser(newUser)
            }
        }

        binding.btProductList.setOnClickListener{
            startActivity(Intent(this, ShowProductActivity::class.java))
        }

        binding.btCameraLink.setOnClickListener{
            startActivity(Intent(this, CameraActivity::class.java))
        }

        binding.btPdfView.setOnClickListener {
            val pdfUrl = "https://fssservices.bookxpert.co/GeneratedPDF/Companies/nadc/2024-2025/BalanceSheet.pdf"
            PdfViewerActivity.start(this, pdfUrl)
        }



    }
}