package com.aicropcare.app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aicropcare.app.model.AuthState
import com.aicropcare.app.ui.auth.AuthViewModel
import com.aicropcare.app.ui.screens.crops.MyCropsScreen
import com.aicropcare.app.ui.screens.history.HistoryScreen
import com.aicropcare.app.ui.screens.home.HomeScreen
import com.aicropcare.app.ui.screens.profile.ProfileScreen
import com.aicropcare.app.ui.screens.scan.ScanScreen
import com.aicropcare.app.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainScaffold(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onLogoutSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavDestination.Home.route

    val authState by authViewModel.authState.collectAsState()
    val farmerName = (authState as? AuthState.LoggedIn)?.profile?.fullName?.ifEmpty { "Farmer" } ?: "Farmer"

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mainViewModel, authViewModel) {
        mainViewModel.userMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(authViewModel) {
        authViewModel.uiEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AppBottomNavigationBar(
                currentRoute = currentRoute,
                onNavigateToDestination = { destination ->
                    if (currentRoute != destination.route) {
                        bottomNavController.navigate(destination.route) {
                            popUpTo(bottomNavController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavDestination.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(BottomNavDestination.Home.route) {
                HomeScreen(
                    viewModel = mainViewModel,
                    farmerName = farmerName,
                    onNavigateToScan = {
                        bottomNavController.navigate(BottomNavDestination.Scan.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToCrops = {
                        bottomNavController.navigate(BottomNavDestination.MyCrops.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToHistory = {
                        bottomNavController.navigate(BottomNavDestination.History.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(BottomNavDestination.Scan.route) {
                ScanScreen(
                    viewModel = mainViewModel
                )
            }

            composable(BottomNavDestination.MyCrops.route) {
                MyCropsScreen(
                    viewModel = mainViewModel
                )
            }

            composable(BottomNavDestination.History.route) {
                HistoryScreen(
                    viewModel = mainViewModel,
                    onNavigateToScan = {
                        bottomNavController.navigate(BottomNavDestination.Scan.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(BottomNavDestination.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    mainViewModel = mainViewModel,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToLanguage = onNavigateToLanguage,
                    onLogoutSuccess = onLogoutSuccess
                )
            }
        }
    }
}
