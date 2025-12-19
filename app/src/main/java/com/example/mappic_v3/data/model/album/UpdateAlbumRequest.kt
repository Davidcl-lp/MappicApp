package com.example.mappic_v3.data.model.album

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAlbumRequest(
    val title: String? = null,
    val description: String? = null,
    val location_name: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val is_global: Boolean? = null
)
