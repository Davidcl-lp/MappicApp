package com.example.mappic_v3.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val profile_picture_url: String? = null
)
