package com.example.mappic_v3.data.repository

import android.content.Context
import android.net.Uri
import com.example.mappic_v3.data.model.Photo.Photo
import com.example.mappic_v3.data.remote.ApiClient
import com.example.mappic_v3.data.remote.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream

class PhotoRepository {

    suspend fun getPhotos(albumId: Int): List<Photo> =
        safeCall { ApiClient.photoApi.getPhotosByAlbum(albumId) } ?: emptyList()

    suspend fun uploadPhotos(
        context: Context,
        uris: List<Uri>,
        albumId: Int,
        uploaderId: Int,
        description: String?
    ): List<Photo>? {
        return try {
            val parts = uris.mapNotNull { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@mapNotNull null
                val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())

                MultipartBody.Part.createFormData(
                    "images",
                    "photo_${System.currentTimeMillis()}.jpg",
                    requestFile
                )
            }

            val albumIdBody = albumId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val uploaderIdBody = uploaderId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val descBody = (description ?: "").toRequestBody("text/plain".toMediaTypeOrNull())

            val response = ApiClient.photoApi.uploadPhotos(
                parts,
                albumIdBody,
                uploaderIdBody,
                descBody
            )

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun deletePhoto(photoId: Int): Boolean =
        safeCall { ApiClient.photoApi.deletePhoto(photoId) } != null

}
