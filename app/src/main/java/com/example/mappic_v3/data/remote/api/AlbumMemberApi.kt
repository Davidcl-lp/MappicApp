package com.example.mappic_v3.data.remote.api

import com.example.mappic_v3.data.model.Member.AddMemberRequest
import com.example.mappic_v3.data.model.auth.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AlbumMemberApi {

    @POST("api/album/member")
    suspend fun addAlbumMember(
        @Body request: AddMemberRequest
    ): User

   // @GET("api/album/{id}/members")
  //  suspend fun getAlbumMembers(@Path("id") albumId: Int): List<User>

    @GET("api/album/member/{id}")
    suspend fun getAlbumMembers(@Path("id") albumId: Int): List<User>
    @GET("api/user/email/{email}")
    suspend fun searchUserByEmail(@Path("email") email: String): User?

    @HTTP(method = "DELETE", path = "api/album/member/{id}", hasBody = true)
    suspend fun deleteAlbumMember(
        @Query("albumId") albumId: Int,
        @Query("userId") userId: Int
    ): Response<ResponseBody>
}
