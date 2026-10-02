package com.example.aicropcare.navigation
import androidx.compose.ui.res.stringResource
import com.example.aicropcare.R

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.aicropcare.*
import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.repository.AuthRepository
import com.example.aicropcare.repository.PredictionRepository
import com.example.aicropcare.ui.screens.LoginScreen
import com.example.aicropcare.ui.screens.RegisterScreen
import com.example.aicropcare.ui.screens.ChatbotScreen
import com.example.aicropcare.ui.components.AppHeader
import com.example.aicropcare.ui.screens.HistoryScreen
import com.example.aicropcare.ui.screens.HomeScreen
import com.example.aicropcare.ui.screens.AnalysisResultScreen
import com.example.aicropcare.ui.screens.CameraCaptureScreen
import com.example.aicropcare.ui.screens.CropHealthProgressScreen
import com.example.aicropcare.ui.screens.DiseaseDetectionScreen
import com.example.aicropcare.ui.screens.FarmingRemindersScreen
import com.example.aicropcare.ui.screens.ProfileScreen
import com.example.aicropcare.ui.screens.ScanCropScreen
import com.example.aicropcare.ui.screens.SplashScreen
import com.example.aicropcare.ui.screens.CropRecommendationScreen
import com.example.aicropcare.ui.screens.TreatmentAdviceScreen
import com.example.aicropcare.ui.screens.WeatherScreen
import com.example.aicropcare.ui.theme.AgriPrimaryContainer
import com.example.aicropcare.ui.theme.AgriPrimaryDark
import com.example.aicropcare.ui.theme.AgriSurface
import com.example.aicropcare.ui.theme.AgriTextSecondary
import com.example.aicropcare.viewmodel.AuthViewModel
import com.example.aicropcare.viewmodel.ConnectionViewModel
import com.example.aicropcare.viewmodel.ScanViewModel
import com.example.aicropcare.viewmodel.WeatherViewModel
import java.io.File

data class BottomNavItem(
    val key: NavigationKey,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun AppNavigation(
    currentLanguage: String,
    onLanguageChanged: (String) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    // Properly instantiated ViewModels attached to the Activity scope (thanks to MainActivity base context override)
    val authRepository = remember { AuthRepository(sessionManager) }
    val authViewModel = remember { AuthViewModel(authRepository) }
    val connectionViewModel = remember { ConnectionViewModel() }
    
    val predictionRepository = remember { PredictionRepository(sessionManager) }
    val historyRepository = remember { com.example.aicropcare.repository.HistoryRepository(context) }
    val cropHealthProgressRepository = remember { com.example.aicropcare.repository.CropHealthProgressRepository(historyRepository) }
    val scanViewModel = remember { ScanViewModel(predictionRepository, historyRepository) }

    var activeAnalysisResult by remember { mutableStateOf<PredictionResponse?>(null) }
    var currentImageFile by remember { mutableStateOf<File?>(null) }

    val backStack = rememberNavBackStack(SplashNav)

    val currentKey = backStack.lastOrNull()

    val bottomNavItems = listOf(
        BottomNavItem(HomeNav, stringResource(R.string.nav_home), Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(ScanCropNav, stringResource(R.string.nav_scan), Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt),
        BottomNavItem(HistoryNav, stringResource(R.string.nav_history), Icons.Filled.History, Icons.Outlined.History),
        BottomNavItem(ChatbotNav, stringResource(R.string.nav_krishi_ai), Icons.Filled.SmartToy, Icons.Outlined.SmartToy)
    )

    val showBottomBar = currentKey is ScanCropNav || currentKey is HistoryNav || currentKey is ChatbotNav
    val showHeader = currentKey is HomeNav || currentKey is ScanCropNav || currentKey is HistoryNav || currentKey is ChatbotNav

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val handleLanguageChange: (String) -> Unit = { lang ->
        onLanguageChanged(lang)
        showLanguageDialog = false
        val isViewingResult = currentKey is AnalysisResultNav || currentKey is TreatmentNav
        if (isViewingResult && activeAnalysisResult != null && currentImageFile != null) {
            scanViewModel.analyzeCrop(context, currentImageFile!!) { response ->
                activeAnalysisResult = response
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout {
                            backStack.clear()
                            backStack.add(LoginNav)
                        }
                    }
                ) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLanguageDialog) {
        com.example.aicropcare.ui.components.LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onDismissRequest = { showLanguageDialog = false },
            onLanguageSelected = handleLanguageChange
        )
    }

    Scaffold(
        topBar = {
            if (showHeader) {
                AppHeader(
                    title = "AI CROP CARE",
                    subtitle = stringResource(R.string.ai_crop_disease_detection),
                    showBackButton = currentKey !is HomeNav,
                    onBackClick = { 
                        backStack.clear()
                        backStack.add(HomeNav)
                    },
                    showLogoutButton = true,
                    onLogoutClick = { showLogoutDialog = true },
                    onLanguageClick = { showLanguageDialog = true },
                    onProfileClick = { backStack.add(ProfileNav) }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = AgriSurface,
                    contentColor = AgriPrimaryDark,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentKey == item.key
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { 
                                if (!isSelected) {
                                    backStack.clear()
                                    backStack.add(item.key)
                                }
                            },
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
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<SplashNav> {
                        SplashScreen(
                            onNavigateToHome = {
                                backStack.clear()
                                backStack.add(HomeNav)
                            },
                            onNavigateToLogin = {
                                backStack.clear()
                                backStack.add(LoginNav)
                            }
                        )
                    }

                    entry<LoginNav> {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onNavigateToRegister = { backStack.add(RegisterNav) },
                            onLoginSuccess = {
                                backStack.clear()
                                backStack.add(HomeNav)
                            }
                        )
                    }

                    entry<RegisterNav> {
                        RegisterScreen(
                            authViewModel = authViewModel,
                            onNavigateToLogin = { backStack.removeLastOrNull() },
                            onRegisterSuccess = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }

                    entry<HomeNav> {
                        HomeScreen(
                            onNavigateToScan = { backStack.add(ScanCropNav) },
                            onNavigateToHistory = { backStack.add(HistoryNav) },
                            onNavigateToChatbot = { backStack.add(ChatbotNav) },
                            onNavigateToWeather = { backStack.add(WeatherNav) },
                            onNavigateToProgress = { backStack.add(CropHealthProgressNav) },
                            onNavigateToReminders = { backStack.add(FarmingRemindersNav) },
                            onNavigateToCropRecommendation = { backStack.add(CropRecommendationNav) },
                            onNavigateToTreatment = {
                                if (activeAnalysisResult != null) {
                                    backStack.add(TreatmentNav(activeAnalysisResult!!))
                                } else {
                                    backStack.add(ScanCropNav)
                                }
                            },
                            connectionViewModel = connectionViewModel,
                            
                        )
                    }

                    entry<ScanCropNav> {
                        ScanCropScreen(
                            currentImageFile = currentImageFile,
                            scanViewModel = scanViewModel,
                            onOpenCamera = { backStack.add(UploadImageNav) },
                            onImageSelected = { file -> currentImageFile = file },
                            onAnalysisSuccess = { response, file ->
                                activeAnalysisResult = response
                                currentImageFile = file
                                backStack.add(AnalysisResultNav(response, file?.absolutePath))
                            }
                        )
                    }

                    entry<UploadImageNav> {
                        CameraCaptureScreen(
                            onImageCaptured = { file ->
                                currentImageFile = file
                                scanViewModel.resetState()
                                backStack.removeLastOrNull()
                            },
                            onClose = { backStack.removeLastOrNull() }
                        )
                    }

                    entry<AnalysisResultNav> { key: AnalysisResultNav ->
                        DiseaseDetectionScreen(
                            result = key.result,
                            onNavigateToTreatment = { backStack.add(TreatmentNav(key.result)) },
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    entry<TreatmentNav> { key: TreatmentNav ->
                        TreatmentAdviceScreen(
                            result = key.result,
                            onBack = { backStack.removeLastOrNull() },
                            onRescanCrop = { 
                                backStack.clear()
                                backStack.add(ScanCropNav) 
                            }
                        )
                    }

                    entry<HistoryNav> {
                        HistoryScreen(
                            historyRepository = historyRepository,
                            onNavigateToScan = { 
                                backStack.clear()
                                backStack.add(ScanCropNav) 
                            },
                            onHistoryItemClick = { response, imagePath ->
                                backStack.add(AnalysisResultNav(response, imagePath))
                            }
                        )
                    }

                    

                    entry<ChatbotNav> {
                        ChatbotScreen()
                    }

                    

                    entry<CropHealthProgressNav> {
                        CropHealthProgressScreen(
                            repository = cropHealthProgressRepository,
                            onBack = { backStack.removeLastOrNull() },
                            onNavigateToScan = { 
                                backStack.clear()
                                backStack.add(ScanCropNav)
                            }
                        )
                    }

                    
                    entry<CropRecommendationNav> {
                        CropRecommendationScreen(
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }
entry<WeatherNav> {
                        WeatherScreen(
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    entry<FarmingRemindersNav> {
                        FarmingRemindersScreen(
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    entry<ProfileNav> {
                        ProfileScreen(
                            sessionManager = sessionManager,
                            authViewModel = authViewModel,
                            currentLanguage = currentLanguage,
                            onLanguageSelected = handleLanguageChange,
                            onBack = { backStack.removeLastOrNull() },
                            onLogoutSuccess = {
                                backStack.clear()
                                backStack.add(LoginNav)
                            }
                        )
                    }
                }
            )
        }
    }
}




















