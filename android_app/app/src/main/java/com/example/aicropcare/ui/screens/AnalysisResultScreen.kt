package com.example.aicropcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.PrimaryButton
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisResultScreen(
    result: PredictionResponse,
    imageFile: File?,
    onScanAnother: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isHealthy = result.isHealthy
    val isClear = result.isClear

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Diagnosis Result",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriPrimaryDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AgriPrimaryDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AgriBackground)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Leaf Photo Thumbnail Preview
            if (imageFile != null && imageFile.exists()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, AgriPrimaryLight, RoundedCornerShape(20.dp)),
                    shadowElevation = 4.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = imageFile,
                            contentDescription = "Analyzed Leaf",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Engine Badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AgriSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = result.engine ?: "Gemini Vision AI",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Image Clarity Warning Banner (if image is unclear or not a plant leaf)
            if (!isClear) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, AgriSecondary, RoundedCornerShape(16.dp)),
                    color = AgriSecondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = AgriSecondaryDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Image Clarity Alert",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriSecondaryDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "The uploaded photo is blurry, dark, or not clearly recognizable as a plant leaf. For higher diagnostic accuracy, retake with clear natural lighting.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Primary Diagnosis Card (Crop & Disease Status)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        2.dp,
                        if (isHealthy) AgriSuccess else AgriPrimary,
                        RoundedCornerShape(20.dp)
                    ),
                color = AgriSurface,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIAGNOSTIC SUMMARY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary,
                            letterSpacing = 1.sp
                        )

                        // Health Status Pill Badge
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isHealthy) AgriSuccessContainer else AgriDangerContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isHealthy) AgriSuccess else AgriDanger)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHealthy) "Healthy Plant" else "Disease Detected",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHealthy) AgriPrimaryDark else AgriDanger
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Crop Name
                    Text(
                        text = result.displayCrop,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Disease Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isHealthy) Icons.Default.CheckCircle else Icons.Default.Coronavirus,
                            contentDescription = null,
                            tint = if (isHealthy) AgriSuccess else AgriDanger,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = result.displayDisease,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isHealthy) AgriPrimaryDark else AgriDanger
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = AgriBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Gemini AI Confidence Metric
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Gemini Vision AI Confidence",
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary
                            )
                            Text(
                                text = "AI Visual Assessment Score",
                                style = MaterialTheme.typography.labelSmall,
                                color = AgriTextSecondary.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AgriPrimaryContainer
                        ) {
                            Text(
                                text = "${result.confidence}%",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriPrimaryDark
                            )
                        }
                    }
                }
            }

            // Visual Symptoms Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, AgriBorder, RoundedCornerShape(18.dp)),
                color = AgriSurface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(AgriSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = AgriSecondaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Observed Symptoms",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = result.displaySymptoms,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }

            // Treatment & Management Plan Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, AgriBorder, RoundedCornerShape(18.dp)),
                color = AgriSurface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(AgriPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = AgriPrimaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isHealthy) "Recommended Plant Care" else "Actionable Treatment Plan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = result.displayTreatment,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }

            // Action Buttons
            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "🌱 Scan Another Crop",
                onClick = onScanAnother,
                icon = Icons.Default.PhotoCamera
            )

            OutlinedButton(
                onClick = onNavigateToChatbot,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AgriPrimaryDark),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AgriPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ask Krishi AI About This Diagnosis",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
