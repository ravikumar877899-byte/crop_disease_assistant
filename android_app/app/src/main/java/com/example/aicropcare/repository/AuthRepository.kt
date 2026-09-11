package com.example.aicropcare.repository

import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.network.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AuthRepository(
    private val sessionManager: SessionManager? = null
) {
    private val gson = Gson()

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(username = username, email = email, password = password)
            val response = RetrofitClient.getApiService().register(request)
            
            if (response.status == "success" && response.token != null && response.user != null) {
                sessionManager?.saveAuthSession(response.token, response.user)
            }
            Result.success(response)
        } catch (e: HttpException) {
            val errorMsg = parseHttpError(e)
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Network error occurred."))
        }
    }

    suspend fun login(
        username: String,
        password: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(username = username, password = password)
            val response = RetrofitClient.getApiService().login(request)

            if (response.status == "success" && response.token != null && response.user != null) {
                sessionManager?.saveAuthSession(response.token, response.user)
            }
            Result.success(response)
        } catch (e: HttpException) {
            val errorMsg = parseHttpError(e)
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Network error occurred."))
        }
    }

    suspend fun checkSession(token: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.getApiService().getMe("Bearer $token")
            Result.success(response)
        } catch (e: HttpException) {
            val errorMsg = parseHttpError(e)
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Unable to verify session."))
        }
    }

    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        val token = sessionManager?.authToken
        try {
            if (!token.isNullOrBlank()) {
                RetrofitClient.getApiService().logout("Bearer $token")
            }
        } catch (_: Exception) {
            // Logout locally even if network fails
        } finally {
            sessionManager?.clearSession()
        }
        Result.success(Unit)
    }

    private fun parseHttpError(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, ApiResponse::class.java)
                parsed.message
            } else {
                "Server returned error code ${e.code()}."
            }
        } catch (_: Exception) {
            "Server returned error code ${e.code()}."
        }
    }
}
