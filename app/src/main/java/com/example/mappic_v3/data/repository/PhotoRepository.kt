package com.example.mappic_v3.data.repository


import com.example.mappic_v3.data.model.Photo
import com.example.mappic_v3.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepository {

    suspend fun getPhotos(albumId: Int): List<Photo> =
        safeCall { ApiClient.apiService.getPhotosByAlbum(albumId) } ?: emptyList()
}

private suspend fun <T> safeCall(block: suspend () -> T): T? {
    return try {
        withContext(Dispatchers.IO) { block() }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
