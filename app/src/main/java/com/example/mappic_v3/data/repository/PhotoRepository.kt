package com.example.mappic_v3.data.repository

import android.content.Context
import android.net.Uri
import com.example.mappic_v3.data.model.Photo
import com.example.mappic_v3.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class PhotoRepository {

    suspend fun getPhotos(albumId: Int): List<Photo> =
        safeCall { ApiClient.apiService.getPhotosByAlbum(albumId) } ?: emptyList()

    suspend fun uploadPhotos(
        context: Context,
        uris: List<Uri>,
        albumId: Int,
        uploaderId: Int,
        description: String?
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver

                val imageParts = uris.map { uri ->
                    val inputStream = contentResolver.openInputStream(uri)!!
                    val bytes = inputStream.readBytes()
                    inputStream.close()

                    val requestBody = bytes.toRequestBody("image/*".toMediaType())

                    MultipartBody.Part.createFormData(
                        name = "images",
                        filename = "photo_${System.currentTimeMillis()}.jpg",
                        body = requestBody
                    )
                }

                ApiClient.apiService.uploadPhotos(
                    images = imageParts,
                    albumId = albumId.toString().toRequestBody("text/plain".toMediaType()),
                    uploaderId = uploaderId.toString().toRequestBody("text/plain".toMediaType()),
                    description = description?.toRequestBody("text/plain".toMediaType())
                )

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun deletePhoto(photoId: Int): Boolean =
        safeCall { ApiClient.apiService.deletePhoto(photoId) } != null

}

private suspend fun <T> safeCall(block: suspend () -> T): T? {
    return try {
        withContext(Dispatchers.IO) { block() }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}