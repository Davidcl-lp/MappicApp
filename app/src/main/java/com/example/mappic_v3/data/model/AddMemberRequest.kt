package com.example.mappic_v3.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddMemberRequest(
    val album_id: Int,
    val user_id: Int,
    val role: String
)