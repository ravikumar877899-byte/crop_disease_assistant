package com.example.aicropcare

import androidx.navigation3.runtime.NavKey
import com.example.aicropcare.data.models.HistoryItem
import com.example.aicropcare.data.models.PredictionResponse
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationKey : NavKey

@Serializable
data object SplashNav : NavigationKey

@Serializable
data object LoginNav : NavigationKey

@Serializable
data object RegisterNav : NavigationKey

@Serializable
data object HomeNav : NavigationKey

@Serializable
data object ScanCropNav : NavigationKey

@Serializable
data object UploadImageNav : NavigationKey

@Serializable
data class AnalysisResultNav(
    val result: PredictionResponse,
    val imagePath: String? = null
) : NavigationKey

@Serializable
data class TreatmentNav(
    val result: PredictionResponse
) : NavigationKey

@Serializable
data object HistoryNav : NavigationKey

@Serializable
data class HistoryDetailNav(
    val item: HistoryItem
) : NavigationKey

@Serializable
data object ChatbotNav : NavigationKey

@Serializable
data object ServerSettingsNav : NavigationKey
