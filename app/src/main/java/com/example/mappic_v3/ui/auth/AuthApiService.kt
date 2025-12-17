package com.example.mappic_v3.ui.auth

import com.example.mappic_v3.data.model.auth.*
import retrofit2.http.*

interface AuthApiService {

    @POST("api/user/signup")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @DELETE("api/user")
    suspend fun deleteCurrentUser(
        @Header("Authorization") token: String
    ): AuthResponse
}
