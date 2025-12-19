package com.example.mappic_v3.data.remote

import com.example.mappic_v3.data.remote.api.*
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
    val albumApi: AlbumApi by lazy {
        retrofit.create(AlbumApi::class.java)
    }
    val photoApi: PhotoApi by lazy {
        retrofit.create(PhotoApi::class.java)
    }
    val albumMemberApi: AlbumMemberApi by lazy {
        retrofit.create(AlbumMemberApi::class.java)
    }
}
