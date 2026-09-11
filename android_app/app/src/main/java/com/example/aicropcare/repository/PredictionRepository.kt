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
        } catch (e: HttpException) {
            val errorMsg = parseHttpError(e)
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to connect to AI vision server."))
        }
    }

    private fun parseHttpError(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, PredictionResponse::class.java)
                parsed.message ?: "Server returned error ${e.code()}."
            } else {
                "Server returned error code ${e.code()}."
            }
        } catch (_: Exception) {
            "Server error (${e.code()}). Please check your connection."
        }
    }
}
