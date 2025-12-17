package com.example.mappic_v3.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mappic_v3.data.model.auth.*
import com.example.mappic_v3.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val context: Context) : ViewModel() {

    var token: String? = null

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> get() = _isRegistered

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun clearError() {
        _errorMessage.value = null
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val request = RegisterRequest(username, email, password)
                val response: AuthResponse = ApiClient.authApiService.register(request)
                token = response.token
                _isRegistered.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Registration failed: ${e.message}"
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response: AuthResponse = ApiClient.authApiService.login(request)
                token = response.token
                _isLoggedIn.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Login failed: ${e.message}"
            }
        }
    }


    fun logout() {
        token = null
        _isLoggedIn.value = false
    }

    fun deleteAccount(onResult: (Boolean) -> Unit) {
        val currentToken = token ?: run {
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                ApiClient.authApiService.deleteCurrentUser("Bearer $currentToken")
                logout()
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}
