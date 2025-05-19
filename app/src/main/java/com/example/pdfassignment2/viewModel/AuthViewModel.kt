package com.example.pdfassignment2.viewModel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdfassignment2.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel  @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel()  {

    // LiveData for authentication state
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    // LiveData for the current user
    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    // State for user messages (errors, success)
    private val _userMessage = MutableLiveData<String?>()
    val userMessage: LiveData<String?> = _userMessage

    init {
        checkCurrentUser()
    }

    /**
     * Check if a user is already logged in
     */
    fun checkCurrentUser() {
        val user = authRepository.getCurrentUser()
        _currentUser.value = user
        if (user != null) {
            _authState.value = AuthState.AUTHENTICATED
        } else {
            _authState.value = AuthState.UNAUTHENTICATED
        }
    }

    /**
     * Create a new user with email and password
     */
    fun createUserWithEmailAndPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _userMessage.value = "Email and password cannot be empty"
            return
        }

        _authState.value = AuthState.LOADING

        viewModelScope.launch {
            try {
                val result = authRepository.createUserWithEmailAndPassword(email, password)
                result.onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.AUTHENTICATED
                    _userMessage.value = "User created successfully"
                    Log.d("TAG", "User created: ${user.email}")
                }.onFailure { exception ->
                    _authState.value = AuthState.ERROR
                    _userMessage.value = exception.message
                    Log.e("TAG", "User creation failed", exception)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.ERROR
                _userMessage.value = e.message
                Log.e("TAG", "Exception during user creation", e)
            }
        }
    }

    /**
     * Get intent for Google sign-in
     */
    fun getGoogleSignInIntent(): Intent {
        return authRepository.getGoogleSignInIntent()
    }

    /**
     * Handle Google sign-in result
     */
    fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        if (account == null) {
            _authState.value = AuthState.ERROR
            _userMessage.value = "Google sign-in failed"
            return
        }

        _authState.value = AuthState.LOADING

        viewModelScope.launch {
            try {
                val result = authRepository.signInWithGoogle(account)
                result.onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.AUTHENTICATED
                    _userMessage.value = "Google sign-in successful"
                    Log.d("TAG", "Google sign-in: ${user.email}")
                }.onFailure { exception ->
                    _authState.value = AuthState.ERROR
                    _userMessage.value = exception.message
                    Log.e("TAG", "Google sign-in failed", exception)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.ERROR
                _userMessage.value = e.message
                Log.e("TAG", "Exception during Google sign-in", e)
            }
        }
    }

    /**
     * Clear any user messages
     */
    fun clearUserMessage() {
        _userMessage.value = null
    }



    enum class AuthState {
        LOADING,
        AUTHENTICATED,
        UNAUTHENTICATED,
        ERROR
    }
}