package com.example.mappic_v3.data.repository
import com.example.mappic_v3.data.model.album.Album
import com.example.mappic_v3.data.model.album.CreateAlbumRequest
import com.example.mappic_v3.data.model.album.UpdateAlbumRequest
import com.example.mappic_v3.data.remote.ApiClient
import com.example.mappic_v3.data.remote.safeCall

class AlbumRepository {

    suspend fun getUserAlbums(userId: Int): List<Album> =
        safeCall { ApiClient.albumApi.getUserAlbums(userId) } ?: emptyList()
    suspend fun createAlbum(body: CreateAlbumRequest): Album? =
        safeCall { ApiClient.albumApi.createAlbum(body) }
    suspend fun updateAlbum(id: Int, body: UpdateAlbumRequest): Album? =
        safeCall { ApiClient.albumApi.updateAlbum(id, body) }
    suspend fun deleteAlbum(id: Int): Boolean =
        safeCall { ApiClient.albumApi.deleteAlbum(id) } != null
    suspend fun getSharedAlbums(userId: Int): List<Album> =
        safeCall { ApiClient.albumApi.getSharedAlbums(userId) } ?: emptyList()

}