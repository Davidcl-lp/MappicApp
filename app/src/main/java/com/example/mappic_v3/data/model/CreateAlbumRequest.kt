package com.example.mappic_v3.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateAlbumRequest(
    val title: String,
    val description: String?,
    val owner_id: Int,
    val location_name: String?,
    val latitude: String?,
    val longitude: String?,
    val is_global: Boolean
)
