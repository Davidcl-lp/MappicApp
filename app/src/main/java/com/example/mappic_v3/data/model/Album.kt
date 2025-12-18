package com.example.mappic_v3.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: Int,
    val title: String,
    val description: String? = null,
    val owner_id: Int,
    val role: String? = "viewer",
    val created_at: String,
    val updated_at: String,
    val location_name: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val is_global: Boolean = false
)

