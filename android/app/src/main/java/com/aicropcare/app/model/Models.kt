package com.aicropcare.app.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Supported Language definition
 */
data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val isSelected: Boolean = false
)

/**
 * Health Status of a crop item
 */
enum class CropHealthStatus {
    HEALTHY,
    ATTENTION_NEEDED,
    DISEASED,
    UNKNOWN
}

/**
 * Crop entity model prepared for future Room DB / API integration
 */
data class CropItem(
    val id: String,
    val name: String,
    val variety: String,
    val plantingDate: String,
    val healthStatus: CropHealthStatus = CropHealthStatus.HEALTHY,
    val notes: String = "",
    val imageUrl: String? = null
)

/**
 * Scan history item model prepared for future scan persistence
 */
data class ScanRecord(
    val id: String,
    val cropName: String,
    val diseaseName: String,
    val confidence: Float,
    val dateFormatted: String,
    val isHealthy: Boolean = false,
    val treatmentSummary: String = "",
    val imageUri: String? = null
)

/**
 * Onboarding step data
 */
data class OnboardingStep(
    val pageIndex: Int,
    val title: String,
    val description: String,
    val icon: ImageVector
)

/**
 * Quick action item for the dashboard
 */
data class QuickActionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String
)
