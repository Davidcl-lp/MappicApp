package com.example.mappic_v3.data.remote.api

import com.example.mappic_v3.data.model.album.Album
import com.example.mappic_v3.data.model.album.CreateAlbumRequest
import com.example.mappic_v3.data.model.album.UpdateAlbumRequest
import retrofit2.http.*

interface AlbumApi {

    @GET("api/user/albums/{id}")
    suspend fun getUserAlbums(@Path("id") userId: Int): List<Album>

    @GET("api/user/albums/member/{id}")
    suspend fun getSharedAlbums(@Path("id") userId: Int): List<Album>

    @POST("api/album")
    suspend fun createAlbum(@Body body: CreateAlbumRequest): Album

    @PUT("api/album/{id}")
    suspend fun updateAlbum(
        @Path("id") id: Int,
        @Body body: UpdateAlbumRequest
    ): Album

    @DELETE("api/album/{id}")
    suspend fun deleteAlbum(@Path("id") id: Int)
}
