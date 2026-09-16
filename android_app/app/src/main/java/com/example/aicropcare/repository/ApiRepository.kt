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
        } catch (_: java.net.UnknownHostException) {
            Result.failure(Exception("Internet connection unavailable. Please check your mobile data or Wi-Fi and try again."))
        } catch (_: java.net.SocketTimeoutException) {
            Result.failure(Exception("Server connection timed out. Please check your internet connection and try again."))
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Failed to connect to backend server."
            if (msg.contains("Unable to resolve host", ignoreCase = true) || msg.contains("No address associated", ignoreCase = true)) {
                Result.failure(Exception("Internet connection unavailable. Please check your mobile data or Wi-Fi and try again."))
            } else {
                Result.failure(Exception(msg))
            }
        }
    }

    suspend fun testMobileConnection(): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.getApiService().testMobileConnection()
            Result.success(response)
        } catch (_: java.net.UnknownHostException) {
            Result.failure(Exception("Internet connection unavailable. Please check your mobile data or Wi-Fi and try again."))
        } catch (_: java.net.SocketTimeoutException) {
            Result.failure(Exception("Server connection timed out. Please check your internet connection and try again."))
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Failed to connect to backend server."
            if (msg.contains("Unable to resolve host", ignoreCase = true) || msg.contains("No address associated", ignoreCase = true)) {
                Result.failure(Exception("Internet connection unavailable. Please check your mobile data or Wi-Fi and try again."))
            } else {
                Result.failure(Exception(msg))
            }
        }
    }
}
