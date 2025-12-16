package com.example.mappic_v3.data.remote

import com.example.mappic_v3.data.model.Album
import com.example.mappic_v3.data.model.CreateAlbumRequest
import com.example.mappic_v3.data.model.Photo
import com.example.mappic_v3.data.model.UpdateAlbumRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @GET("api/user/albums/{id}")
    suspend fun getUserAlbums(@Path("id") userId: Int): List<Album>
    @POST("api/album")
    suspend fun createAlbum(@Body body: CreateAlbumRequest): Album
    @DELETE("api/album/{id}")
    suspend fun deleteAlbum(@Path("id") id: Int)
    @PUT("api/album/{id}")
    suspend fun updateAlbum(
        @Path("id") id: Int,
        @Body body: UpdateAlbumRequest
    ): Album
    @GET("/api/album/{albumId}/photos")
    suspend fun getPhotosByAlbum(
        @Path("albumId") albumId: Int
    ): List<Photo>
    @Multipart
    @POST("api/photo/upload")
    suspend fun uploadPhotos(
        @Part images: List<MultipartBody.Part>,
        @Part("album_id") albumId: RequestBody,
        @Part("uploader_id") uploaderId: RequestBody,
        @Part("description") description: RequestBody?
    )
    @DELETE("api/photo/{id}")
    suspend fun deletePhoto(@Path("id") id: Int)
}
