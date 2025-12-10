package com.example.mappic_v3.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val id: Int,
    val album_id: Int,
    val uploader_id: Int? = null,
    val url: String,
    val description: String? = null,
    val created_at: String
)