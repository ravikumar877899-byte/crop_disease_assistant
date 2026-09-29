package com.example.aicropcare.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

@kotlinx.serialization.Serializable
data class PredictionResponse(
    @SerializedName("status")
    val status: String? = "success",

    @SerializedName("crop")
    val crop: String? = null,

    @SerializedName("disease")
    val disease: String? = null,

    @SerializedName("confidence")
    val confidence: Float? = 0f,

    @SerializedName("affected_percentage")
    val affectedPercentage: Int? = 0,

    @SerializedName("severity")
    val severity: String? = null,

    @SerializedName("risk_level")
    val riskLevel: String? = null,

    @SerializedName("risk_reason")
    val riskReason: String? = null,

    @SerializedName("immediate_action")
    val immediateAction: String? = null,

    @SerializedName("rescan_recommended")
    val rescanRecommended: Boolean? = false,

    @SerializedName("symptoms")
    val symptoms: String? = null,

    @SerializedName("treatment")
    val treatment: String? = null,

    @SerializedName("treatment_plan")
    val treatmentPlan: TreatmentPlan? = null,

    @SerializedName("is_clear")
    val isClear: Boolean? = true,

    @SerializedName("engine")
    val engine: String? = null,

    @SerializedName("message")
    val message: String? = null
) : Serializable {

    val isError: Boolean
        get() = status == "error"

    val isHealthy: Boolean
        get() = disease?.contains("health", ignoreCase = true) == true || severity == "HEALTHY"

    val parsedSeverity: DiseaseSeverity
        get() = try {
            severity?.let { DiseaseSeverity.valueOf(it.uppercase()) } ?: DiseaseSeverity.MODERATE
        } catch (e: Exception) {
            DiseaseSeverity.MODERATE
        }

    val parsedRiskLevel: DiseaseRiskLevel
        get() = try {
            riskLevel?.let { DiseaseRiskLevel.valueOf(it.replace(" ", "_").uppercase()) } ?: DiseaseRiskLevel.MEDIUM
        } catch (e: Exception) {
            DiseaseRiskLevel.MEDIUM
        }

    val confidenceValue: Float
        get() = confidence ?: 85f

    val affectedPercentageValue: Int
        get() = affectedPercentage ?: 0

    val isClearValue: Boolean
        get() = isClear ?: true

    val isRescanAdvised: Boolean
        get() = rescanRecommended ?: false

    val displayCrop: String
        get() = if (!crop.isNullOrBlank()) crop else "Generic Plant"

    val displayDisease: String
        get() = if (!disease.isNullOrBlank()) disease else "Unknown Issue"

    val displaySymptoms: String
        get() = if (!symptoms.isNullOrBlank()) symptoms else ""

    val displayTreatment: String
        get() = if (!treatment.isNullOrBlank()) treatment else ""

    val displayRiskReason: String?
        get() = if (!riskReason.isNullOrBlank()) riskReason else null

    val displayImmediateAction: String?
        get() = if (!immediateAction.isNullOrBlank()) immediateAction else null

    val resolvedTreatmentPlan: TreatmentPlan
        get() {
            if (treatmentPlan != null) return treatmentPlan
            // Strict API adherence. No English fallbacks for dynamic fields.
            return TreatmentPlan(
                crop = displayCrop,
                disease = displayDisease,
                severity = severity ?: "MODERATE",
                riskLevel = riskLevel ?: "MEDIUM",
                treatmentCategory = if (parsedSeverity == DiseaseSeverity.MILD) "GENERAL_MANAGEMENT" else "CONFIRMED_TREATMENT",
                immediateAction = displayImmediateAction ?: displayRiskReason ?: "",
                treatmentObjective = "",
                management = if (displayTreatment.isNotBlank()) displayTreatment.split("\n") else emptyList(),
                medicineGuidance = emptyList(),
                weatherPrecaution = "",
                sprayingWindow = "UNAVAILABLE",
                sprayingReason = "",
                safetyPrecautions = emptyList(),
                avoid = emptyList(),
                rescanRecommended = isRescanAdvised
            )
        }
}

@kotlinx.serialization.Serializable
enum class SprayingWindowStatus {
    GOOD,
    CAUTION,
    AVOID,
    UNAVAILABLE
}

@kotlinx.serialization.Serializable
data class MedicineGuidanceItem(
    @SerializedName("active_ingredient")
    val activeIngredient: String? = null,
    @SerializedName("purpose")
    val purpose: String? = null,
    @SerializedName("application_method")
    val applicationMethod: String? = null,
    @SerializedName("timing")
    val timing: String? = null
) : Serializable

@kotlinx.serialization.Serializable
data class TreatmentPlan(
    @SerializedName("disease")
    val disease: String? = null,

    @SerializedName("crop")
    val crop: String? = null,

    @SerializedName("severity")
    val severity: String? = null,

    @SerializedName("risk_level")
    val riskLevel: String? = null,

    @SerializedName("treatment_category")
    val treatmentCategory: String? = null,

    @SerializedName("immediate_action")
    val immediateAction: String? = null,

    @SerializedName("treatment_objective")
    val treatmentObjective: String? = null,

    @SerializedName("management")
    val management: List<String>? = emptyList(),

    @SerializedName("medicine_guidance")
    val medicineGuidance: List<MedicineGuidanceItem>? = emptyList(),

    @SerializedName("weather_precaution")
    val weatherPrecaution: String? = null,

    @SerializedName("spraying_window")
    val sprayingWindow: String? = null,

    @SerializedName("spraying_reason")
    val sprayingReason: String? = null,

    @SerializedName("safety_precautions")
    val safetyPrecautions: List<String>? = emptyList(),

    @SerializedName("avoid")
    val avoid: List<String>? = emptyList(),

    @SerializedName("rescan_recommended")
    val rescanRecommended: Boolean? = false
) : Serializable {

    val parsedSprayingWindow: SprayingWindowStatus
        get() = try {
            sprayingWindow?.let { SprayingWindowStatus.valueOf(it.uppercase()) } ?: SprayingWindowStatus.UNAVAILABLE
        } catch (e: Exception) {
            SprayingWindowStatus.UNAVAILABLE
        }
}

@kotlinx.serialization.Serializable
enum class DiseaseSeverity(val label: String) {
    HEALTHY("Healthy"),
    MILD("Mild"),
    MODERATE("Moderate"),
    SEVERE("Severe"),
    UNKNOWN("Unknown")
}

@kotlinx.serialization.Serializable
enum class DiseaseRiskLevel(val label: String) {
    NO_RISK("No Risk"),
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    UNKNOWN("Unknown")
}


