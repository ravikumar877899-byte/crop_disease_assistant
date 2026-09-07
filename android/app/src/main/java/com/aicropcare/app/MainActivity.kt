package com.aicropcare.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aicropcare.app.data.auth.LocalAuthRepository
import com.aicropcare.app.navigation.AppNavGraph
import com.aicropcare.app.ui.auth.AuthViewModel
import com.aicropcare.app.ui.auth.AuthViewModelFactory
import com.aicropcare.app.ui.theme.AICropCareTheme
import com.aicropcare.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = LocalAuthRepository(applicationContext)
        val authViewModelFactory = AuthViewModelFactory(authRepository)

        setContent {
            AICropCareTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val mainViewModel: MainViewModel = viewModel()
                    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

                    AppNavGraph(
                        mainViewModel = mainViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}
