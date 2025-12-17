package com.example.mappic_v3.ui.auth

import android.content.Context
import retrofit2.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mappic_v3.data.model.auth.*
import com.example.mappic_v3.data.remote.ApiClient
import com.example.mappic_v3.data.remote.ApiClient.apiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.reflect.Modifier

class AuthViewModel(private val context: Context) : ViewModel() {

    var token: String? = null

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> get() = _isRegistered

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _userId = MutableStateFlow<Int?>(null)
    val userId: StateFlow<Int?> get() = _userId
    fun clearError() {
        _errorMessage.value = null
    }

    fun register(username: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val request = RegisterRequest(username, email, password)
                val response: AuthResponse = ApiClient.authApiService.register(request)
                token = response.token
                _userId.value = response.user.id
                _isRegistered.value = true
                onResult(true, null)
            } catch (e: HttpException) {
                val message = try {
                    val errorBody = e.response()?.errorBody()
                    val errorString = errorBody?.string()
                    if (!errorString.isNullOrEmpty()) {
                        val json = JSONObject(errorString)
                        json.optString("message", "Unknown error")
                    } else {
                        "Unknown error"
                    }
                } catch (ex: Exception) {
                    "Unknown error"
                }
                onResult(false, message)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ApiClient.authApiService.login(LoginRequest(email, password))
                token = response.token
                _userId.value = response.user.id
                _isLoggedIn.value = true
                _errorMessage.value = null
                onResult(true, null)
            } catch (e: HttpException) {
                val errorJsonString = e.response()?.errorBody()?.string()
                val message = try {
                    if (!errorJsonString.isNullOrEmpty()) {
                        JSONObject(errorJsonString).optString("message", "Error en el servidor")
                    } else {
                        "Error: ${e.code()}"
                    }
                } catch (ex: Exception) {
                    "Error de respuesta"
                }

                _errorMessage.value = message
                onResult(false, message)
            } catch (e: Exception) {
                val message = e.message ?: "Error de conexión"
                _errorMessage.value = message
                onResult(false, message)
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
