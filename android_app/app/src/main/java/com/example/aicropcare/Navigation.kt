package com.example.aicropcare

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.ui.auth.LoginScreen
import com.example.aicropcare.ui.auth.RegisterScreen
import com.example.aicropcare.ui.auth.ServerSettingsScreen
import com.example.aicropcare.ui.chatbot.ChatbotScreen
import com.example.aicropcare.ui.history.HistoryDetailScreen
import com.example.aicropcare.ui.history.HistoryScreen
import com.example.aicropcare.ui.home.HomeScreen
import com.example.aicropcare.ui.result.AnalysisResultScreen
import com.example.aicropcare.ui.result.TreatmentScreen
import com.example.aicropcare.ui.scan.ScanCropScreen
import com.example.aicropcare.ui.scan.UploadImageScreen
import com.example.aicropcare.ui.splash.SplashScreen

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var currentLanguage by remember { mutableStateOf(sessionManager.language) }

    fun toggleLanguage() {
        val newLang = if (currentLanguage == "en") "ta" else "en"
        sessionManager.language = newLang
        currentLanguage = newLang
    }

    val backStack = rememberNavBackStack(SplashNav)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SplashNav> {
                SplashScreen(
                    onNavigate = { dest: NavigationKey ->
                        backStack.clear()
                        backStack.add(dest)
                    }
                )
            }

            entry<LoginNav> {
                LoginScreen(
                    onNavigate = { dest: NavigationKey ->
                        if (dest == HomeNav) {
                            backStack.clear()
                        }
                        backStack.add(dest)
                    },
                    onToggleLanguage = { toggleLanguage() }
                )
            }

            entry<RegisterNav> {
                RegisterScreen(
                    onNavigate = { dest: NavigationKey ->
                        if (dest == HomeNav) {
                            backStack.clear()
                        }
                        backStack.add(dest)
                    }
                )
            }

            entry<ServerSettingsNav> {
                ServerSettingsScreen(
                    onBack = { backStack.removeLastOrNull() }
                )
            }

            entry<HomeNav> {
                HomeScreen(
                    onNavigate = { dest: NavigationKey -> backStack.add(dest) },
                    onToggleLanguage = { toggleLanguage() }
                )
            }

            entry<ScanCropNav> {
                ScanCropScreen(
                    onNavigate = { dest: NavigationKey -> backStack.add(dest) },
                    onBack = { backStack.removeLastOrNull() }
                )
            }

            entry<UploadImageNav> {
                UploadImageScreen(
                    onNavigate = { dest: NavigationKey -> backStack.add(dest) },
                    onBack = { backStack.removeLastOrNull() }
                )
            }

            entry<AnalysisResultNav> { key: AnalysisResultNav ->
                AnalysisResultScreen(
                    result = key.result,
                    imagePath = key.imagePath,
                    onNavigate = { dest: NavigationKey -> backStack.add(dest) },
                    onBack = { backStack.removeLastOrNull() }
                )
            }

            entry<TreatmentNav> { key: TreatmentNav ->
                TreatmentScreen(
                    result = key.result,
                    onBack = { backStack.removeLastOrNull() }
                )
            }

            entry<HistoryNav> {
                HistoryScreen(
                    onNavigate = { dest: NavigationKey -> backStack.add(dest) },
                    onBack = { backStack.removeLastOrNull() }
                )
            }

            entry<HistoryDetailNav> { key: HistoryDetailNav ->
                HistoryDetailScreen(
                    item = key.item,
                    onBack = { backStack.removeLastOrNull() }
                )
            }

            entry<ChatbotNav> {
                ChatbotScreen(
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
