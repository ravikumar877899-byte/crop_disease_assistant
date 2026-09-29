package com.example.aicropcare.navigation

sealed class Screen(val route: String, val title: String) {
    data object Splash : Screen("splash", "Splash")
    data object Login : Screen("login", "Sign In")
    data object Register : Screen("register", "Register")
    data object Home : Screen("home", "Home")
    data object Scan : Screen("scan", "Scan")
    data object CameraCapture : Screen("camera_capture", "Camera Viewfinder")
    data object DiseaseDetection : Screen("disease_detection", "Disease Detection")
    data object TreatmentAdvice : Screen("treatment_advice", "Treatment & Advice")
    data object History : Screen("history", "History")
    data object Chatbot : Screen("chatbot", "Krishi AI")
    data object Profile : Screen("profile", "Profile")
    data object Weather : Screen("weather", "Weather & Advisory")
    data object Reminders : Screen("reminders", "Farming Reminders")
    data object CropHealthProgress : Screen("crop_health_progress", "Crop Health Progress")

    // Backwards-compatible alias
    val AnalysisResult: Screen get() = DiseaseDetection
}
