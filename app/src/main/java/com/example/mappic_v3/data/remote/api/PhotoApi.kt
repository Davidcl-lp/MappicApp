package com.example.mappic_v3.data.remote.api

import com.example.mappic_v3.data.model.Photo.Photo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PhotoApi {

    @GET("api/album/{albumId}/photos")
    suspend fun getPhotosByAlbum(
        @Path("albumId") albumId: Int
    ): List<Photo>

    @Multipart
    @POST("api/photo/upload")
    suspend fun uploadPhotos(
        @Part images: List<MultipartBody.Part>,
        @Part("album_id") albumId: RequestBody,
        @Part("uploader_id") uploaderId: RequestBody,
        @Part("description") description: RequestBody
    ): Response<List<Photo>>

    @DELETE("api/photo/{id}")
    suspend fun deletePhoto(@Path("id") id: Int)
}
