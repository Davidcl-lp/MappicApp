package com.example.mappic_v3.data.repository.auth

import com.example.mappic_v3.data.model.auth.*
import com.example.mappic_v3.data.remote.ApiClient

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): AuthResponse {
        return ApiClient.apiService.login(
            LoginRequest(email, password)
        )
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        profilePictureUrl: String? = null
    ): AuthResponse {
        return ApiClient.apiService.register(
            RegisterRequest(name, email, password)
        )
    }
}
