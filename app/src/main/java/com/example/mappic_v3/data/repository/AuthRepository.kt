package com.example.mappic_v3.data.repository

import com.example.mappic_v3.data.model.auth.AuthResponse
import com.example.mappic_v3.data.model.auth.LoginRequest
import com.example.mappic_v3.data.model.auth.RegisterRequest
import com.example.mappic_v3.data.remote.ApiClient

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): AuthResponse {
        return ApiClient.authApi.login(
            LoginRequest(email, password)
        )
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        profilePictureUrl: String? = null
    ): AuthResponse {
        return ApiClient.authApi.register(
            RegisterRequest(name, email, password)
        )
    }
}