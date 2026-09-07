package com.example.aicropcare

import android.app.Application
import com.example.aicropcare.data.api.ApiClient
import com.example.aicropcare.data.preferences.SessionManager

class AICropCareApp : Application() {

    lateinit var sessionManager: SessionManager
        private set

    lateinit var apiClient: ApiClient
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        sessionManager = SessionManager(this)
        apiClient = ApiClient.getInstance(this)
    }

    companion object {
        lateinit var instance: AICropCareApp
            private set
    }
}
