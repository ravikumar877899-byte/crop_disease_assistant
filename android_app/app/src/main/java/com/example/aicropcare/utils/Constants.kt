package com.example.aicropcare.utils

object Constants {
    const val APP_NAME = "AI Crop Care"
    const val APP_SUBTITLE = "AI Crop Disease Detection and Treatment Assistant"
    
    // Centralized Base URL configuration
    // 10.0.2.2:5000 is used by the Android Emulator to connect to Windows host localhost:5000
    // For physical Android devices connected via Wi-Fi/LAN, set to http://<YOUR_PC_IP>:5000/
    const val DEFAULT_BASE_URL = "http://10.0.2.2:5000/"
    
    // Placeholder messages for Phase 2/3 UI
    const val MSG_CAMERA_FUTURE = "Camera scanning will be enabled in a future phase."
    const val MSG_AI_FUTURE = "AI analysis will be available in a future phase."
    const val MSG_FEATURE_FUTURE = "This feature will be available in a future phase."
    const val MSG_CHATBOT_INITIAL = "Hello! I am Krishi AI. I will help you with crop-related questions."
    const val MSG_CHATBOT_FUTURE = "Krishi AI will provide crop-related assistance in a future phase."
}
