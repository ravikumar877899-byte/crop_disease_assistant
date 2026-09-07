package com.example.aicropcare.data.api

import com.example.aicropcare.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication Endpoints
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getMe(): Response<UserMeResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<AuthResponse>

    // Leaf Disease Prediction Endpoints
    @Multipart
    @POST("api/predict/upload")
    suspend fun predictUpload(
        @Part file: MultipartBody.Part,
        @Part("lang") lang: RequestBody
    ): Response<PredictionResponse>

    @POST("api/predict/camera")
    suspend fun predictCamera(
        @Body request: CameraPredictRequest
    ): Response<PredictionResponse>

    // Krishi AI Chatbot Endpoint
    @POST("api/chatbot")
    suspend fun sendChatQuery(
        @Body request: ChatRequest
    ): Response<ChatResponse>

    // Scan History Endpoints
    @GET("api/history")
    suspend fun getHistory(): Response<HistoryListResponse>

    @DELETE("api/history/{id}")
    suspend fun deleteHistory(
        @Path("id") id: Int
    ): Response<DeleteHistoryResponse>
}
