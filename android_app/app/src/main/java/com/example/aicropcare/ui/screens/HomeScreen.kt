package com.example.aicropcare.ui.screens

import androidx.compose.ui.res.stringResource
import com.example.aicropcare.R
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
    onNavigateToWeather: () -> Unit = {},
    onNavigateToProgress: () -> Unit = {},
    onNavigateToReminders: () -> Unit = {},
    onNavigateToTreatment: () -> Unit = onNavigateToScan,
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
                                text = stringResource(R.string.home_product_name),
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
                                text = stringResource(R.string.home_phase_3_api),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = stringResource(R.string.home_hero_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.home_hero_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE8F5E9),
                        lineHeight = 20.sp
                    )}
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
                // Row 1: Icon and Title
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
                            contentDescription = stringResource(R.string.home_server_card_title),
                            tint = AgriPrimaryDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.home_server_card_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriPrimaryDark
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Base URL
                Text(
                    text = stringResource(R.string.home_server_base_url, RetrofitClient.getBaseUrl()),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Status Badge
                when (val state = connectionState) {
                    is ConnectionUiState.Loading -> {
                        Surface(
                            color = AgriSecondaryContainer,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = AgriSecondaryDark
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.home_server_connecting),
                                    style = MaterialTheme.typography.labelMedium,
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
                                text = stringResource(R.string.home_server_connected),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
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
                                text = stringResource(R.string.home_server_offline),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
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
                                text = stringResource(R.string.home_server_idle),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = AgriTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Connection Message Details
                when (val state = connectionState) {
                    is ConnectionUiState.Success -> {
                        Text(
                            text = stringResource(R.string.home_server_response_format, state.message, state.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriSuccess,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is ConnectionUiState.Error -> {
                        Text(
                            text = state.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriDanger,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is ConnectionUiState.Loading -> {
                        Text(
                            text = stringResource(R.string.home_server_checking),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    ConnectionUiState.Idle -> {
                        Text(
                            text = stringResource(R.string.home_server_tap_test),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Test Server Connection Button
                OutlinedButton(
                    onClick = {
                        connectionViewModel.testMobileApi()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AgriPrimaryDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.home_test_server),
                        style = MaterialTheme.typography.labelLarge,
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
                        text = stringResource(R.string.home_advisory_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.home_advisory_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF78350F)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Feature Services Section (5 Cards)
        SectionTitle(title = stringResource(R.string.home_crop_care_services), icon = Icons.Default.GridView)

        Spacer(modifier = Modifier.height(12.dp))

        // Card 1: Scan Crop
        FeatureCard(
            title = stringResource(R.string.home_scan_crop),
            description = stringResource(R.string.home_scan_desc),
            icon = Icons.Default.CameraAlt,
            badgeText = stringResource(R.string.badge_ready),
            onClick = onNavigateToScan
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card 2: Disease Detection
        FeatureCard(
            title = stringResource(R.string.home_disease_detection),
            description = stringResource(R.string.home_disease_desc),
            icon = Icons.Default.Biotech,
            badgeText = stringResource(R.string.badge_ai_model),
            onClick = onNavigateToScan
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card 3: Treatment Advice
        FeatureCard(
            title = stringResource(R.string.home_treatment_advice),
            description = stringResource(R.string.home_treatment_desc),
            icon = Icons.Default.Medication,
            badgeText = stringResource(R.string.badge_guidance),
            onClick = onNavigateToTreatment
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card 4: Scan History
        FeatureCard(
            title = stringResource(R.string.home_scan_history),
            description = stringResource(R.string.home_history_desc),
            icon = Icons.Default.History,
            badgeText = stringResource(R.string.badge_records),
            onClick = onNavigateToHistory
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card 5: Krishi AI
        FeatureCard(
            title = stringResource(R.string.home_krishi_ai),
            description = stringResource(R.string.home_krishi_ai_desc),
            icon = Icons.Default.SmartToy,
            badgeText = stringResource(R.string.badge_assistant),
            onClick = onNavigateToChatbot
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card 6: Weather & Advisory
        FeatureCard(
            title = stringResource(R.string.home_service_weather_title),
            description = stringResource(R.string.home_service_weather_desc),
            icon = Icons.Default.Cloud,
            onClick = onNavigateToWeather
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Card 7: Crop Health Progress
        FeatureCard(
            title = stringResource(R.string.home_service_crop_progress_title),
            description = stringResource(R.string.home_service_crop_progress_desc),
            icon = Icons.Default.TrendingUp,
            onClick = onNavigateToProgress
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Card 8: Farming Reminders
        FeatureCard(
            title = stringResource(R.string.home_service_reminders_title),
            description = stringResource(R.string.home_service_reminders_desc),
            icon = Icons.Default.Notifications,
            onClick = onNavigateToReminders
        )
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(24.dp))
    }
}




