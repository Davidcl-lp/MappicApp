package com.example.mappic_v3.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val profile_picture_url: String? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
