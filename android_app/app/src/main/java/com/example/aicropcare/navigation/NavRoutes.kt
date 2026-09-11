package com.example.aicropcare.navigation

sealed class Screen(val route: String, val title: String) {
    data object Splash : Screen("splash", "Splash")
    data object Login : Screen("login", "Sign In")
    data object Register : Screen("register", "Register")
    data object Home : Screen("home", "Home")
    data object Scan : Screen("scan", "Scan")
    data object CameraCapture : Screen("camera_capture", "Camera Viewfinder")
    data object AnalysisResult : Screen("analysis_result", "Diagnosis Result")
    data object History : Screen("history", "History")
    data object Chatbot : Screen("chatbot", "Krishi AI")
}
