package com.example.aicropcare.repository

import com.example.aicropcare.network.ApiResponse
import com.example.aicropcare.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiRepository {

    suspend fun checkHealth(): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.getApiService().checkHealth()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testMobileConnection(): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.getApiService().testMobileConnection()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
