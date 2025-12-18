package com.example.mappic_v3.data.remote

import com.example.mappic_v3.data.model.AddMemberRequest
import com.example.mappic_v3.data.model.Album
import com.example.mappic_v3.data.model.CreateAlbumRequest
import com.example.mappic_v3.data.model.Photo
import com.example.mappic_v3.data.model.UpdateAlbumRequest
import com.example.mappic_v3.data.model.auth.AlbumMemberResponse
import com.example.mappic_v3.data.model.auth.AuthResponse
import com.example.mappic_v3.data.model.auth.LoginRequest
import com.example.mappic_v3.data.model.auth.RegisterRequest
import com.example.mappic_v3.data.model.auth.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.Response

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

    @POST("user/login")
    suspend fun login(
        @Body body: LoginRequest
    ): AuthResponse

    @POST("user/signup")
    suspend fun register(
        @Body body: RegisterRequest
    ): AuthResponse

    @DELETE("user")
    suspend fun deleteUser(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("api/album/member")
    suspend fun addAlbumMember(@Body request: AddMemberRequest): AlbumMemberResponse

    @GET("api/user/email/{email}")
    suspend fun searchUserByEmail(@Path("email") email: String): User?

    @GET("api/album/{id}/members")
    suspend fun getAlbumMembers(@Path("id") albumId: Int): List<User>

    @GET("api/user/albums/member/{id}")
    suspend fun getSharedAlbums(@Path("id") userId: Int): List<Album>

    @HTTP(method = "DELETE", path = "api/album/member/{id}", hasBody = true)
    suspend fun deleteAlbumMember(
        @Path("id") albumId: Int,
        @Body body: Map<String, Int>
    ): Any
}
