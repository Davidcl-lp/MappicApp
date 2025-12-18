package com.example.mappic_v3.data.repository

import com.example.mappic_v3.data.model.auth.User
import com.example.mappic_v3.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {

        private suspend fun <T> safeCall(block: suspend () -> T): T? {
        return try {
            withContext(Dispatchers.IO) { block() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun findUserByEmail(email: String):User? =
        safeCall { ApiClient.apiService.searchUserByEmail(email) }
}