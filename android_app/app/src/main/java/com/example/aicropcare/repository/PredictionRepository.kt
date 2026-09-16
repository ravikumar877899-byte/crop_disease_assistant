package com.example.aicropcare.repository

import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class PredictionRepository(
    private val sessionManager: SessionManager? = null
) {
    private val gson = Gson()

    suspend fun analyzeCropLeaf(imageFile: File): Result<PredictionResponse> = withContext(Dispatchers.IO) {
        try {
            if (!imageFile.exists()) {
                return@withContext Result.failure(Exception("Image file does not exist on device."))
            }

            val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

            val token = sessionManager?.authToken
            val authHeader = if (!token.isNullOrBlank()) "Bearer $token" else null

            val response = RetrofitClient.getApiService().predictCropDisease(filePart, authHeader)

            if (response.isError) {
                Result.failure(Exception(response.message ?: "Analysis failed."))
            } else {
                Result.success(response)
            }
        } catch (_: java.net.UnknownHostException) {
            Result.failure(Exception("Internet connection unavailable. Please check your mobile data or Wi-Fi and try again."))
        } catch (_: java.net.SocketTimeoutException) {
            Result.failure(Exception("Server connection timed out. Please check your internet connection and try again."))
        } catch (e: HttpException) {
            val errorMsg = parseHttpError(e)
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Failed to connect to AI vision server."
            if (msg.contains("Unable to resolve host", ignoreCase = true) || msg.contains("No address associated", ignoreCase = true)) {
                Result.failure(Exception("Internet connection unavailable. Please check your mobile data or Wi-Fi and try again."))
            } else {
                Result.failure(Exception(msg))
            }
        }
    }

    private fun parseHttpError(e: HttpException): String {
        if (e.code() == 429) {
            return "AI service is temporarily unavailable because the Gemini usage limit has been reached. Please try again later."
        }
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, PredictionResponse::class.java)
                val msg = parsed.message
                if (!msg.isNullOrBlank()) {
                    if (msg.contains("quota", ignoreCase = true) ||
                        msg.contains("usage limit", ignoreCase = true) ||
                        msg.contains("429") ||
                        msg.contains("resource_exhausted", ignoreCase = true) ||
                        msg.contains("resourceexhausted", ignoreCase = true)
                    ) {
                        "AI service is temporarily unavailable because the Gemini usage limit has been reached. Please try again later."
                    } else {
                        msg
                    }
                } else {
                    "AI service error (${e.code()}). Please try again later."
                }
            } else {
                "AI service error (${e.code()}). Please try again later."
            }
        } catch (_: Exception) {
            "AI service error (${e.code()}). Please try again later."
        }
    }
}
