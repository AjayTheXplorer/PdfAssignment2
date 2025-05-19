package com.example.pdfassignment2

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pdfassignment2.databinding.ActivityMainBinding
import com.example.pdfassignment2.viewModel.AuthViewModel
import com.example.pdfassignment2.views.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()

    // Activity result launcher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account = task.result
                viewModel.handleGoogleSignInResult(account)
                showToast("Google Signin Successfully")
            } else {
                showToast("Google sign in failed")
            }
        } else {
            showToast("Google sign in cancelled")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // token
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "Current token: ${task.result}")
            } else {
                Log.e("FCM", "Token failed", task.exception)
            }
        }

        // notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Subscribe to topic (call this once)
        Firebase.messaging.subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to topic")
                } else {
                    Log.e("FCM", "Topic subscription failed")
                }
            }



        setupClickListeners()
        observeViewModel()

    }

    private fun setupClickListeners() {
        // Email/Password sign up button
        binding.button.setOnClickListener {
            val email = binding.email.text?.toString() ?: ""
            val password = binding.pass.text?.toString() ?: ""
            viewModel.createUserWithEmailAndPassword(email, password)
        }

        // Google sign in button
        binding.google.setOnClickListener {
            val signInIntent = viewModel.getGoogleSignInIntent()
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun observeViewModel() {
        // Observe authentication state
        viewModel.authState.observe(this) { state ->
            when (state) {
                AuthViewModel.AuthState.LOADING -> showLoading(true)
                AuthViewModel.AuthState.AUTHENTICATED -> handleAuthenticated()
                AuthViewModel.AuthState.UNAUTHENTICATED -> showLoading(false)
                AuthViewModel.AuthState.ERROR -> showLoading(false)
            }
        }

        // Observe user messages
        viewModel.userMessage.observe(this) { message ->
            message?.let {
                showToast(it)
                viewModel.clearUserMessage()
            }
        }
    }

    private fun handleAuthenticated() {
        showLoading(false)
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
        // Don't finish() here if you want the user to be able to go back to the login screen
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        // Disable input fields and buttons during loading
        binding.email.isEnabled = !isLoading
        binding.pass.isEnabled = !isLoading
        binding.button.isEnabled = !isLoading
        binding.google.isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
