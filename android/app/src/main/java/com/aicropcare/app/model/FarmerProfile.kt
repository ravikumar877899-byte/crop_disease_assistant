package com.aicropcare.app.model

/**
 * Farmer Profile entity representing the farmer's account and agricultural details.
 * Architected to map cleanly to future Flask API / PostgreSQL models.
 */
data class FarmerProfile(
    val id: String = "",
    val fullName: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val farmLocation: String = "",
    val farmSize: Double = 0.0,
    val farmSizeUnit: String = "Acres", // "Acres" or "Hectares"
    val preferredLanguage: String = "English"
)
