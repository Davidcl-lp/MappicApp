package com.example.mappic_v3.data.remote.api
import com.example.mappic_v3.data.model.auth.AuthResponse
import com.example.mappic_v3.data.model.auth.LoginRequest
import com.example.mappic_v3.data.model.auth.RegisterRequest
import com.example.mappic_v3.data.model.auth.User
import retrofit2.http.*

interface AuthApi {

    @POST("api/user/signup")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @DELETE("api/user")
    suspend fun deleteCurrentUser(
        @Header("Authorization") token: String
    ): AuthResponse
    @GET("api/user/email/{email}")
    suspend fun searchUserByEmail(@Path("email") email: String): User?
}
