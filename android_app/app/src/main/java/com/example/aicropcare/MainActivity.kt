package com.example.aicropcare

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.navigation.AppNavigation
import com.example.aicropcare.theme.AICropCareTheme
import com.example.aicropcare.utils.LocaleHelper

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val sessionManager = SessionManager(newBase)
        val context = LocaleHelper.setLocale(newBase, sessionManager.language)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val sessionManager = SessionManager(this)

        setContent {
            AICropCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        currentLanguage = sessionManager.language,
                        onLanguageChanged = { newLang ->
                            sessionManager.language = newLang
                            recreate()
                        }
                    )
                }
            }
        }
    }
}
