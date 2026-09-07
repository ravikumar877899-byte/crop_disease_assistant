package com.aicropcare.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aicropcare.app.model.AuthState
import com.aicropcare.app.ui.auth.AuthViewModel
import com.aicropcare.app.ui.screens.auth.LoginScreen
import com.aicropcare.app.ui.screens.auth.RegisterScreen
import com.aicropcare.app.ui.screens.auth.WelcomeAuthScreen
import com.aicropcare.app.ui.screens.language.LanguageScreen
import com.aicropcare.app.ui.screens.onboarding.OnboardingScreen
import com.aicropcare.app.ui.screens.profile.EditProfileScreen
import com.aicropcare.app.ui.screens.splash.SplashScreen
import com.aicropcare.app.viewmodel.MainViewModel

@Composable
fun AppNavGraph(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val authState by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    if (authState is AuthState.LoggedIn) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    mainViewModel.completeOnboarding()
                    navController.navigate(Screen.Language.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Language.route) {
            LanguageScreen(
                viewModel = mainViewModel,
                onLanguageSelected = {
                    navController.navigate(Screen.WelcomeAuth.route) {
                        popUpTo(Screen.Language.route) { inclusive = true }
                    }
                },
                showBackButton = false
            )
        }

        // Welcome Auth Landing
        composable(Screen.WelcomeAuth.route) {
            WelcomeAuthScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.WelcomeAuth.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.WelcomeAuth.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Sub-screen for changing language from Profile with back button
        composable(Screen.LanguageSettings.route) {
            LanguageScreen(
                viewModel = mainViewModel,
                onLanguageSelected = {
                    navController.popBackStack()
                },
                showBackButton = true,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Edit Profile Screen
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                viewModel = authViewModel,
                onSaveSuccess = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Main App Scaffold (Protected Screen)
        composable(Screen.Main.route) {
            MainScaffold(
                mainViewModel = mainViewModel,
                authViewModel = authViewModel,
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToLanguage = {
                    navController.navigate(Screen.LanguageSettings.route)
                },
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
