package com.example.mappic_v3.data.model.Member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddMemberRequest(
    @SerialName("album_id") val albumId: Int,
    @SerialName("user_id") val userId: Int,
    val role: String
)