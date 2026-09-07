package com.example.aicropcare.data

import android.content.Context
import com.example.aicropcare.data.api.ApiClient
import com.example.aicropcare.data.models.*
import com.example.aicropcare.data.preferences.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class DataRepository(context: Context) {

    private val apiClient = ApiClient.getInstance(context)
    private val sessionManager = SessionManager(context)

    suspend fun login(email: String, pass: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val response = apiClient.getService().login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.user != null) {
                    sessionManager.isLoggedIn = true
                    sessionManager.userProfile = body.user
                    Result.success(body.user)
                } else {
                    Result.failure(Exception(body.error ?: "Login failed"))
                }
            } else {
                val err = response.errorBody()?.string() ?: "Invalid email or password"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, pass: String, loc: String, lang: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val response = apiClient.getService().register(RegisterRequest(name, email, pass, loc, lang))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.user != null) {
                    sessionManager.isLoggedIn = true
                    sessionManager.userProfile = body.user
                    Result.success(body.user)
                } else {
                    Result.failure(Exception(body.error ?: "Registration failed"))
                }
            } else {
                val err = response.errorBody()?.string() ?: "Registration failed"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkSession(): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiClient.getService().getMe()
            if (response.isSuccessful && response.body()?.authenticated == true) {
                response.body()?.user?.let { sessionManager.userProfile = it }
                sessionManager.isLoggedIn = true
                true
            } else {
                // If offline but previously logged in, retain session for offline UX
                sessionManager.isLoggedIn
            }
        } catch (e: Exception) {
            sessionManager.isLoggedIn
        }
    }

    suspend fun logout(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            apiClient.getService().logout()
        } catch (_: Exception) {}
        sessionManager.clearSession()
        Result.success(true)
    }

    suspend fun predictUpload(file: File, lang: String): Result<PredictionResponse> = withContext(Dispatchers.IO) {
        try {
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, reqFile)
            val langBody = lang.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiClient.getService().predictUpload(body, langBody)
            if (response.isSuccessful && response.body() != null) {
                val res = response.body()!!
                if (res.error != null && res.crop == null) {
                    Result.failure(Exception(res.error))
                } else {
                    Result.success(res)
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Prediction failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun predictCamera(base64Image: String, lang: String): Result<PredictionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiClient.getService().predictCamera(CameraPredictRequest(base64Image, lang))
            if (response.isSuccessful && response.body() != null) {
                val res = response.body()!!
                if (res.error != null && res.crop == null) {
                    Result.failure(Exception(res.error))
                } else {
                    Result.success(res)
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Camera prediction failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendChat(message: String, lang: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiClient.getService().sendChatQuery(ChatRequest(message, lang))
            if (response.isSuccessful && response.body() != null) {
                val text = response.body()?.response ?: "No advice received."
                Result.success(text)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Chat service unavailable"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistory(): Result<List<HistoryItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiClient.getService().getHistory()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.history)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteHistory(id: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiClient.getService().deleteHistory(id)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to delete record"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
