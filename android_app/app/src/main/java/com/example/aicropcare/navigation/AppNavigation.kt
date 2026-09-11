package com.example.aicropcare.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.repository.AuthRepository
import com.example.aicropcare.repository.PredictionRepository
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.AppHeader
import com.example.aicropcare.ui.screens.*
import com.example.aicropcare.viewmodel.AuthViewModel
import com.example.aicropcare.viewmodel.ScanViewModel
import java.io.File

data class BottomNavItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authRepository = remember { AuthRepository(sessionManager) }
    val authViewModel = remember { AuthViewModel(authRepository) }
    val predictionRepository = remember { PredictionRepository(sessionManager) }
    val scanViewModel = remember { ScanViewModel(predictionRepository) }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var currentImageFile by remember { mutableStateOf<File?>(null) }
    var activeAnalysisResult by remember { mutableStateOf<PredictionResponse?>(null) }

    val bottomNavItems = listOf(
        BottomNavItem(
            screen = Screen.Home,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            label = "Home"
        ),
        BottomNavItem(
            screen = Screen.Scan,
            selectedIcon = Icons.Filled.CameraAlt,
            unselectedIcon = Icons.Outlined.CameraAlt,
            label = "Scan"
        ),
        BottomNavItem(
            screen = Screen.History,
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History,
            label = "History"
        ),
        BottomNavItem(
            screen = Screen.Chatbot,
            selectedIcon = Icons.Filled.SmartToy,
            unselectedIcon = Icons.Outlined.SmartToy,
            label = "Krishi AI"
        )
    )

    // Back button handling
    when (currentScreen) {
        Screen.Register -> {
            BackHandler {
                currentScreen = Screen.Login
            }
        }
        Screen.CameraCapture -> {
            BackHandler {
                currentScreen = Screen.Scan
            }
        }
        Screen.AnalysisResult -> {
            BackHandler {
                currentScreen = Screen.Scan
            }
        }
        Screen.Scan, Screen.History, Screen.Chatbot -> {
            BackHandler {
                currentScreen = Screen.Home
            }
        }
        else -> {}
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriPrimaryDark
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out of AI Crop Care?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriTextPrimary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout {
                            currentImageFile = null
                            activeAnalysisResult = null
                            scanViewModel.resetState()
                            currentScreen = Screen.Login
                        }
                    }
                ) {
                    Text(
                        text = "Sign Out",
                        color = AgriDanger,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = AgriTextSecondary)
                }
            }
        )
    }

    // Screen Switching
    when (currentScreen) {
        Screen.Splash -> {
            SplashScreen(
                onNavigateToHome = {
                    currentScreen = Screen.Home
                },
                onNavigateToLogin = {
                    currentScreen = Screen.Login
                }
            )
        }

        Screen.Login -> {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    currentScreen = Screen.Register
                },
                onLoginSuccess = {
                    currentScreen = Screen.Home
                }
            )
        }

        Screen.Register -> {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    currentScreen = Screen.Login
                },
                onRegisterSuccess = {
                    currentScreen = Screen.Login
                }
            )
        }

        Screen.CameraCapture -> {
            CameraCaptureScreen(
                onImageCaptured = { file ->
                    currentImageFile = file
                    scanViewModel.resetState()
                    currentScreen = Screen.Scan
                },
                onClose = {
                    currentScreen = Screen.Scan
                }
            )
        }

        Screen.AnalysisResult -> {
            activeAnalysisResult?.let { result ->
                AnalysisResultScreen(
                    result = result,
                    imageFile = currentImageFile,
                    onScanAnother = {
                        scanViewModel.resetState()
                        currentScreen = Screen.Scan
                    },
                    onNavigateToChatbot = {
                        currentScreen = Screen.Chatbot
                    },
                    onBack = {
                        currentScreen = Screen.Scan
                    }
                )
            } ?: run {
                // If no result is loaded, return to scan
                LaunchedEffect(Unit) {
                    currentScreen = Screen.Scan
                }
            }
        }

        Screen.Home, Screen.Scan, Screen.History, Screen.Chatbot -> {
            Scaffold(
                topBar = {
                    AppHeader(
                        title = "AI CROP CARE",
                        subtitle = "AI Crop Disease Detection & Treatment",
                        showBackButton = currentScreen != Screen.Home,
                        onBackClick = { currentScreen = Screen.Home },
                        showLogoutButton = true,
                        onLogoutClick = { showLogoutDialog = true }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = AgriSurface,
                        contentColor = AgriPrimaryDark,
                        tonalElevation = 8.dp
                    ) {
                        bottomNavItems.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentScreen = item.screen },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label,
                                        tint = if (isSelected) AgriPrimaryDark else AgriTextSecondary
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) AgriPrimaryDark else AgriTextSecondary
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = AgriPrimaryContainer
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(250),
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            Screen.Home -> HomeScreen(
                                onNavigateToScan = { currentScreen = Screen.Scan },
                                onNavigateToHistory = { currentScreen = Screen.History },
                                onNavigateToChatbot = { currentScreen = Screen.Chatbot }
                            )
                            Screen.Scan -> ScanCropScreen(
                                currentImageFile = currentImageFile,
                                scanViewModel = scanViewModel,
                                onOpenCamera = { currentScreen = Screen.CameraCapture },
                                onImageSelected = { file -> currentImageFile = file },
                                onAnalysisSuccess = { response, file ->
                                    activeAnalysisResult = response
                                    currentImageFile = file
                                    currentScreen = Screen.AnalysisResult
                                }
                            )
                            Screen.History -> HistoryScreen(
                                onNavigateToScan = { currentScreen = Screen.Scan }
                            )
                            Screen.Chatbot -> ChatbotScreen()
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
