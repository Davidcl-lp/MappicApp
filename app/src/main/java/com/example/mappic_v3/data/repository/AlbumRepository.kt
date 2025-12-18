package com.example.mappic_v3.data.repository

import com.example.mappic_v3.data.model.*
import com.example.mappic_v3.data.model.auth.User
import com.example.mappic_v3.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumRepository {

    suspend fun getUserAlbums(userId: Int): List<Album> =
        safeCall { ApiClient.apiService.getUserAlbums(userId) } ?: emptyList()

    suspend fun addMemberToAlbum(request: AddMemberRequest): Boolean =
        safeCall { ApiClient.apiService.addAlbumMember(request) } != null

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
            val body = mapOf("userId" to userId)
            ApiClient.apiService.deleteAlbumMember(albumId, body)
            true
        } catch (e: Exception) {
            e.printStackTrace()
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
