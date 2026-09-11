package com.example.aicropcare.network

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    // ---------------- Health & Verification ----------------
    @GET("api/health")
    suspend fun checkHealth(): ApiResponse

    @GET("api/mobile/test")
    suspend fun testMobileConnection(): ApiResponse

    // ---------------- Phase 4 Authentication ----------------
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/auth/me")
    suspend fun getMe(@Header("Authorization") token: String): AuthResponse

    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): ApiResponse

    // ---------------- Phase 6 Gemini Vision AI ----------------
    @Multipart
    @POST("api/ai/predict")
    suspend fun predictCropDisease(
        @Part image: MultipartBody.Part,
        @Header("Authorization") token: String? = null
    ): PredictionResponse
}
