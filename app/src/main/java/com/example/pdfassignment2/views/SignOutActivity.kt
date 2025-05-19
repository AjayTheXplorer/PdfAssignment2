package com.example.pdfassignment2.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pdfassignment2.MainActivity
import com.example.pdfassignment2.R
import com.example.pdfassignment2.databinding.ActivitySignOutBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class SignOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignOutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signout.setOnClickListener {
            signOutUser()
        }

    }

    private fun signOutUser() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut() // Sign out from Firebase

        // Sign out from Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            // After signing out, redirect to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}