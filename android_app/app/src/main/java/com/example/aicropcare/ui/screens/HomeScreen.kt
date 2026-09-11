package com.example.aicropcare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aicropcare.network.RetrofitClient
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.FeatureCard
import com.example.aicropcare.ui.components.PrimaryButton
import com.example.aicropcare.ui.components.SectionTitle
import com.example.aicropcare.utils.Constants
import com.example.aicropcare.viewmodel.ConnectionUiState
import com.example.aicropcare.viewmodel.ConnectionViewModel

@Composable
fun HomeScreen(
    onNavigateToScan: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    connectionViewModel: ConnectionViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val connectionState by connectionViewModel.uiState.collectAsState()

    fun showPlaceholderToast(featureName: String) {
        Toast.makeText(
            context,
            "$featureName: ${Constants.MSG_FEATURE_FUTURE}",
            Toast.LENGTH_SHORT
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Welcome Hero Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = AgriPrimaryDark,
            shadowElevation = 3.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(AgriPrimaryDark, AgriPrimary)
                        )
                    )
                    .padding(22.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "🌱 AI CROP CARE",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            color = AgriSecondary,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "Phase 3 API",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Welcome to AI Crop Care",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Detect crop diseases early and get treatment guidance using AI.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE8F5E9),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Prominent "Scan Your Crop" Hero Button
                    Button(
                        onClick = onNavigateToScan,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AgriSecondary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scan Your Crop Now",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phase 3: Flask Backend Live Connection Status Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, AgriBorder, RoundedCornerShape(16.dp)),
            color = AgriSurface,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(AgriPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Server",
                                tint = AgriPrimaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Crop Care Server",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriPrimaryDark
                            )
                            Text(
                                text = "Base URL: ${RetrofitClient.getBaseUrl()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AgriTextSecondary
                            )
                        }
                    }

                    // Status Badge
                    when (val state = connectionState) {
                        is ConnectionUiState.Loading -> {
                            Surface(
                                color = AgriSecondaryContainer,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 2.dp,
                                        color = AgriSecondaryDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Connecting...",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AgriSecondaryDark
                                    )
                                }
                            }
                        }
                        is ConnectionUiState.Success -> {
                            Surface(
                                color = AgriSuccessContainer,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "Backend Connected ✓",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriSuccess
                                )
                            }
                        }
                        is ConnectionUiState.Error -> {
                            Surface(
                                color = AgriDangerContainer,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "Backend Offline",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriDanger
                                )
                            }
                        }
                        ConnectionUiState.Idle -> {
                            Surface(
                                color = AgriSurfaceVariant,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "Idle",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AgriTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Connection Message Details
                when (val state = connectionState) {
                    is ConnectionUiState.Success -> {
                        Text(
                            text = "Server Response: \"${state.message}\" (at ${state.timestamp})",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriSuccess,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    is ConnectionUiState.Error -> {
                        Text(
                            text = state.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriDanger
                        )
                    }
                    is ConnectionUiState.Loading -> {
                        Text(
                            text = "Checking connection with Flask backend API...",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                    ConnectionUiState.Idle -> {
                        Text(
                            text = "Tap below to test communication with the Flask backend.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Test Server Connection Button
                OutlinedButton(
                    onClick = {
                        connectionViewModel.testMobileApi()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AgriPrimaryDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Test Server Connection",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Farmer Advisory Tip
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp)),
            color = AgriSecondaryContainer,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AgriSecondaryDark.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = AgriSecondaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Farmer Advisory Tip",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Scan leaf symptoms early in the day with clear sunlight for optimal disease detection accuracy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF78350F)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Feature Services Section (5 Cards)
        SectionTitle(title = "Crop Care Services", icon = Icons.Default.GridView)

        Spacer(modifier = Modifier.height(12.dp))

        // Card 1: Scan Crop
        FeatureCard(
            title = "Scan Crop",
            description = "Upload or capture a leaf image for AI analysis.",
            icon = Icons.Default.CameraAlt,
            badgeText = "Ready",
            onClick = onNavigateToScan
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card 2: Disease Detection
        FeatureCard(
            title = "Disease Detection",
            description = "Identify possible crop diseases using AI.",
            icon = Icons.Default.Biotech,
            badgeText = "AI Model",
            onClick = { showPlaceholderToast("Disease Detection") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card 3: Treatment Advice
        FeatureCard(
            title = "Treatment Advice",
            description = "Get simple treatment recommendations.",
            icon = Icons.Default.Medication,
            badgeText = "Guidance",
            onClick = { showPlaceholderToast("Treatment Advice") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card 4: Scan History
        FeatureCard(
            title = "Scan History",
            description = "View your previous crop scans.",
            icon = Icons.Default.History,
            badgeText = "Records",
            onClick = onNavigateToHistory
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card 5: Krishi AI
        FeatureCard(
            title = "Krishi AI",
            description = "Ask questions about crops and diseases.",
            icon = Icons.Default.SmartToy,
            badgeText = "Assistant",
            onClick = onNavigateToChatbot
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
