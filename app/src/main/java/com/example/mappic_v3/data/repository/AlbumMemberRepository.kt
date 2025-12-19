package com.example.mappic_v3.data.repository

import android.util.Log
import com.example.mappic_v3.data.model.Member.AddMemberRequest
import com.example.mappic_v3.data.model.auth.User
import com.example.mappic_v3.data.remote.ApiClient
import com.example.mappic_v3.data.remote.safeCall

class AlbumMemberRepository {
    suspend fun addMemberToAlbum(request: AddMemberRequest): User? {
        return try {
            val user = ApiClient.albumMemberApi.addAlbumMember(request)
            Log.d("ADD_MEMBER", "Usuario recibido: $user")
            user
        } catch (e: Exception) {
            Log.e("ADD_MEMBER", "Error al parsear usuario", e)
            null
        }
    }
    suspend fun getAlbumMembers(albumId: Int): List<User> =
        safeCall { ApiClient.albumMemberApi.getAlbumMembers(albumId) } ?: emptyList()
    suspend fun removeMember(albumId: Int, userId: Int): Boolean {
        return try {
            val response = ApiClient.albumMemberApi.deleteAlbumMember(albumId, userId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
