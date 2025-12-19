package com.example.mappic_v3.data.remote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
suspend fun <T> safeCall(block: suspend () -> T): T? {
    return try {
        withContext(Dispatchers.IO) { block() }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }}
