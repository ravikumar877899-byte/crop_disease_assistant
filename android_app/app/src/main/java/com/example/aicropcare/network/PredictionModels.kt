package com.example.aicropcare.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PredictionResponse(
    @SerializedName("status")
    val status: String? = "success",

    @SerializedName("crop")
    val crop: String? = null,

    @SerializedName("disease")
    val disease: String? = null,

    @SerializedName("confidence")
    val confidence: Float = 0f,

    @SerializedName("symptoms")
    val symptoms: String? = null,

    @SerializedName("treatment")
    val treatment: String? = null,

    @SerializedName("is_clear")
    val isClear: Boolean = true,

    @SerializedName("engine")
    val engine: String? = "Gemini Vision AI",

    @SerializedName("message")
    val message: String? = null
) : Serializable {

    val isHealthy: Boolean
        get() = disease != null && (
            disease.contains("Healthy", ignoreCase = true) ||
            disease.equals("None", ignoreCase = true) ||
            disease.contains("No disease", ignoreCase = true)
        )

    val isError: Boolean
        get() = status == "error" || message != null && crop == null

    val displayCrop: String
        get() = crop ?: "Unknown Crop"

    val displayDisease: String
        get() = disease ?: "Healthy Leaf"

    val displaySymptoms: String
        get() = if (!symptoms.isNullOrBlank()) symptoms else "No specific visual symptoms noted."

    val displayTreatment: String
        get() = if (!treatment.isNullOrBlank()) treatment else "No treatment required for healthy crops. Maintain standard crop care and regular watering."
}
