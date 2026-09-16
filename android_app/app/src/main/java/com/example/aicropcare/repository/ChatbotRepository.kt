package com.example.aicropcare.repository

import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.network.ChatHistoryItem
import com.example.aicropcare.network.ChatRequest
import com.example.aicropcare.network.ChatResponse
import com.example.aicropcare.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ChatbotRepository(
    private val sessionManager: SessionManager? = null
) {
    private val gson = Gson()

    suspend fun sendQuery(
        message: String,
        history: List<ChatHistoryItem>? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val token = sessionManager?.authToken
            val authHeader = if (!token.isNullOrBlank()) "Bearer $token" else null
            val request = ChatRequest(message = message.trim(), history = history)
            val response = RetrofitClient.getApiService().askChatbot(request, authHeader)

            if (response.status == "error" || response.response.isNullOrBlank()) {
                Result.failure(Exception(response.message ?: "Krishi AI could not generate an answer. Please try again."))
            } else {
                Result.success(response.response)
            }
        } catch (_: java.net.UnknownHostException) {
            Result.failure(Exception("Internet connection unavailable. Please check your mobile data or Wi-Fi and try again."))
        } catch (_: java.net.SocketTimeoutException) {
            Result.failure(Exception("Server connection timed out. Please check your internet connection and try again."))
        } catch (e: HttpException) {
            val msg = parseHttpError(e)
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Failed to connect to Krishi AI server."
            if (msg.contains("Unable to resolve host", ignoreCase = true) || msg.contains("No address associated", ignoreCase = true)) {
                Result.failure(Exception("Internet connection unavailable. Please check your mobile data or Wi-Fi and try again."))
            } else {
                Result.failure(Exception(msg))
            }
        }
    }

    private fun parseHttpError(e: HttpException): String {
        if (e.code() == 429) {
            return "Krishi AI is temporarily unavailable. Please try again later."
        }
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, ChatResponse::class.java)
                val msg = parsed.message
                if (!msg.isNullOrBlank()) {
                    if (msg.contains("quota", ignoreCase = true) ||
                        msg.contains("usage limit", ignoreCase = true) ||
                        msg.contains("429") ||
                        msg.contains("resource_exhausted", ignoreCase = true) ||
                        msg.contains("resourceexhausted", ignoreCase = true)
                    ) {
                        "Krishi AI is temporarily unavailable. Please try again later."
                    } else {
                        msg
                    }
                } else {
                    "Krishi AI is temporarily unavailable. Please try again later."
                }
            } else {
                "Krishi AI is temporarily unavailable. Please try again later."
            }
        } catch (_: Exception) {
            "Krishi AI is temporarily unavailable. Please try again later."
        }
    }
}
