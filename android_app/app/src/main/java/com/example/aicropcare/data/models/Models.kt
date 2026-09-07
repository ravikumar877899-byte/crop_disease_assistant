package com.example.aicropcare.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

// Authentication Models
@Serializable
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

@Serializable
data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("location") val location: String = "Punjab",
    @SerializedName("language") val language: String = "en"
)

@Serializable
data class UserProfile(
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("joined") val joined: String? = null
) : JavaSerializable

@Serializable
data class AuthResponse(
    @SerializedName("success") val success: String? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("user") val user: UserProfile? = null
)

@Serializable
data class UserMeResponse(
    @SerializedName("authenticated") val authenticated: Boolean = false,
    @SerializedName("user") val user: UserProfile? = null
)

// AI Prediction & Scan Models
@Serializable
data class PredictionResponse(
    @SerializedName("crop") val crop: String? = "Unknown Crop",
    @SerializedName("crop_ta") val cropTa: String? = null,
    @SerializedName("scientific_crop") val scientificCrop: String? = null,
    @SerializedName("disease") val disease: String? = "Healthy",
    @SerializedName("disease_ta") val diseaseTa: String? = null,
    @SerializedName("scientific_disease") val scientificDisease: String? = null,
    @SerializedName("confidence") val confidence: Float = 0f,
    @SerializedName("severity") val severity: Float? = null,
    @SerializedName("reasoning") val reasoning: String? = null,
    @SerializedName("reasoning_ta") val reasoningTa: String? = null,
    @SerializedName("treatment") val treatment: String? = null,
    @SerializedName("treatment_ta") val treatmentTa: String? = null,
    @SerializedName("is_clear") val isClear: Boolean = true,
    @SerializedName("mock_mode") val mockMode: Boolean = false,
    @SerializedName("error") val error: String? = null
) : JavaSerializable {
    val isHealthy: Boolean
        get() = disease?.equals("Healthy", ignoreCase = true) == true ||
                disease?.contains("Healthy", ignoreCase = true) == true

    fun getDisplayCrop(isTamil: Boolean): String {
        return if (isTamil && !cropTa.isNullOrBlank()) cropTa else (crop ?: "Unknown Crop")
    }

    fun getDisplayDisease(isTamil: Boolean): String {
        return if (isTamil && !diseaseTa.isNullOrBlank()) diseaseTa else (disease ?: "Healthy")
    }

    fun getDisplayReasoning(isTamil: Boolean): String {
        return if (isTamil && !reasoningTa.isNullOrBlank()) reasoningTa else (reasoning ?: "")
    }

    fun getDisplayTreatment(isTamil: Boolean): String {
        return if (isTamil && !treatmentTa.isNullOrBlank()) treatmentTa else (treatment ?: "")
    }

    // Calculates disease affected percentage based on confidence and severity
    fun getAffectedPercentage(): Int {
        if (isHealthy) return 0
        val base = severity?.toInt() ?: ((confidence * 0.45f).toInt() + 25)
        return base.coerceIn(12, 95)
    }
}

@Serializable
data class CameraPredictRequest(
    @SerializedName("image") val image: String,
    @SerializedName("lang") val lang: String = "en"
)

// Scan History Models
@Serializable
data class HistoryListResponse(
    @SerializedName("history") val history: List<HistoryItem> = emptyList(),
    @SerializedName("error") val error: String? = null
)

@Serializable
data class HistoryItem(
    @SerializedName("id") val id: Int,
    @SerializedName("crop_name") val cropName: String,
    @SerializedName("crop_ta") val cropTa: String? = null,
    @SerializedName("scientific_crop") val scientificCrop: String? = null,
    @SerializedName("disease_name") val diseaseName: String,
    @SerializedName("disease_ta") val diseaseTa: String? = null,
    @SerializedName("scientific_disease") val scientificDisease: String? = null,
    @SerializedName("confidence") val confidence: Float = 0f,
    @SerializedName("reasoning") val reasoning: String? = null,
    @SerializedName("reasoning_ta") val reasoningTa: String? = null,
    @SerializedName("treatment") val treatment: String? = null,
    @SerializedName("treatment_ta") val treatmentTa: String? = null,
    @SerializedName("language_code") val languageCode: String? = "en",
    @SerializedName("image_path") val imagePath: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null
) : JavaSerializable {
    val isHealthy: Boolean
        get() = diseaseName.equals("Healthy", ignoreCase = true) || diseaseName.contains("Healthy", ignoreCase = true)

    fun getDisplayCrop(isTamil: Boolean): String {
        return if (isTamil && !cropTa.isNullOrBlank()) cropTa else cropName
    }

    fun getDisplayDisease(isTamil: Boolean): String {
        return if (isTamil && !diseaseTa.isNullOrBlank()) diseaseTa else diseaseName
    }

    fun getDisplayTreatment(isTamil: Boolean): String {
        return if (isTamil && !treatmentTa.isNullOrBlank()) treatmentTa else (treatment ?: "")
    }

    fun getAffectedPercentage(): Int {
        if (isHealthy) return 0
        return ((confidence * 0.45f).toInt() + 25).coerceIn(12, 95)
    }
}

@Serializable
data class DeleteHistoryResponse(
    @SerializedName("success") val success: String? = null,
    @SerializedName("error") val error: String? = null
)

// Chatbot Models
@Serializable
data class ChatRequest(
    @SerializedName("message") val message: String,
    @SerializedName("language") val language: String = "en"
)

@Serializable
data class ChatResponse(
    @SerializedName("response") val response: String? = null,
    @SerializedName("error") val error: String? = null
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

// Weather Advisory Model
data class WeatherAdvisory(
    val temperature: String = "28°C",
    val humidity: String = "65%",
    val condition: String = "Partly Cloudy",
    val advisory: String = "Favorable weather for leaf inspection. Ensure early morning irrigation."
)
