package com.example.mappic_v3.data.repository

import android.util.Log
import com.example.mappic_v3.data.model.*
import com.example.mappic_v3.data.model.auth.User
import com.example.mappic_v3.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumRepository {

    suspend fun getUserAlbums(userId: Int): List<Album> =
        safeCall { ApiClient.apiService.getUserAlbums(userId) } ?: emptyList()

    suspend fun addMemberToAlbum(request: AddMemberRequest): User? {
        return try {
            val user = ApiClient.apiService.addAlbumMember(request)
            Log.d("ADD_MEMBER", "Usuario recibido: $user")
            user
        } catch (e: Exception) {
            Log.e("ADD_MEMBER", "Error al parsear usuario", e)
            null
        }
    }


    suspend fun createAlbum(body: CreateAlbumRequest): Album? =
        safeCall { ApiClient.apiService.createAlbum(body) }

    suspend fun updateAlbum(id: Int, body: UpdateAlbumRequest): Album? =
        safeCall { ApiClient.apiService.updateAlbum(id, body) }

    suspend fun deleteAlbum(id: Int): Boolean =
        safeCall { ApiClient.apiService.deleteAlbum(id) } != null

    suspend fun getAlbumMembers(albumId: Int): List<User> =
        safeCall { ApiClient.apiService.getAlbumMembers(albumId) } ?: emptyList()

    suspend fun getSharedAlbums(userId: Int): List<Album> =
        safeCall { ApiClient.apiService.getSharedAlbums(userId) } ?: emptyList()

    suspend fun removeMember(albumId: Int, userId: Int): Boolean {
        return try {
            val response = ApiClient.apiService.deleteAlbumMember(albumId, userId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

private suspend fun <T> safeCall(block: suspend () -> T): T? {
    return try {
        withContext(Dispatchers.IO) { block() }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
