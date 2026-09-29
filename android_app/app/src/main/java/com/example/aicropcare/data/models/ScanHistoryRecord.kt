package com.example.aicropcare.data.models

import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.network.TreatmentPlan
import com.google.gson.Gson
import java.io.File
import java.io.Serializable

data class ScanHistoryRecord(
    val id: Long = 0,
    val userId: Int = -1,
    val username: String = "",
    val cropName: String,
    val diseaseName: String,
    val confidence: Float,
    val affectedPercentage: Int = 0,
    val severity: String = "UNKNOWN",
    val riskLevel: String = "UNKNOWN",
    val riskReason: String = "",
    val immediateAction: String = "",
    val rescanRecommended: Boolean = false,
    val symptoms: String,
    val treatment: String,
    val treatmentPlanJson: String = "",
    val isHealthy: Boolean,
    val isClear: Boolean = true,
    val engine: String = "Gemini Vision AI",
    val imagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String = ""
) : Serializable {

    fun toPredictionResponse(): PredictionResponse {
        val parsedPlan = if (treatmentPlanJson.isNotBlank()) {
            try {
                Gson().fromJson(treatmentPlanJson, TreatmentPlan::class.java)
            } catch (e: Exception) {
                null
            }
        } else null

        return PredictionResponse(
            status = "success",
            crop = cropName,
            disease = diseaseName,
            confidence = confidence,
            affectedPercentage = affectedPercentage,
            severity = severity,
            riskLevel = riskLevel,
            riskReason = riskReason,
            immediateAction = immediateAction,
            rescanRecommended = rescanRecommended,
            symptoms = symptoms,
            treatment = treatment,
            treatmentPlan = parsedPlan,
            isClear = isClear,
            engine = engine,
            message = null
        )
    }

    val imageFile: File?
        get() = if (!imagePath.isNullOrBlank()) {
            val file = File(imagePath)
            if (file.exists() && file.length() > 0) file else null
        } else null
}

