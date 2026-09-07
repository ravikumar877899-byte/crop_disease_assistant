package com.example.aicropcare.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aicropcare.NavigationKey
import com.example.aicropcare.R
import com.example.aicropcare.ScanCropNav
import com.example.aicropcare.TreatmentNav
import com.example.aicropcare.data.models.PredictionResponse
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.AgriTopBar
import com.example.aicropcare.ui.components.GlassCard
import com.example.aicropcare.ui.components.MetricCircleBadge
import com.example.aicropcare.ui.components.StatusBadge
import java.io.File

@Composable
fun AnalysisResultScreen(
    result: PredictionResponse,
    imagePath: String?,
    onNavigate: (NavigationKey) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isTamil = sessionManager.isTamil

    val cropName = result.getDisplayCrop(isTamil)
    val diseaseName = result.getDisplayDisease(isTamil)
    val reasoning = result.getDisplayReasoning(isTamil)
    val isHealthy = result.isHealthy
    val affectedPercent = result.getAffectedPercentage()
    val confidenceScore = "${result.confidence.toInt()}%"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBgLight)
    ) {
        AgriTopBar(
            title = stringResource(R.string.result_title),
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Leaf Photo Thumbnail Preview
            if (imagePath != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    AsyncImage(
                        model = File(imagePath),
                        contentDescription = "Diagnosed Leaf",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Primary Diagnosis Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.crop_label).uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextMuted
                    )

                    StatusBadge(isHealthy = isHealthy, isTamil = isTamil)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = cropName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriGreenPrimary
                )

                if (!result.scientificCrop.isNullOrBlank()) {
                    Text(
                        text = result.scientificCrop,
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = AgriTextSecondary
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = AgriCardGlassBorder
                )

                Text(
                    text = stringResource(R.string.disease_label).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = diseaseName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHealthy) AgriHealthyGreen else AgriDangerRed
                )

                if (!result.scientificDisease.isNullOrBlank() && result.scientificDisease != "N/A") {
                    Text(
                        text = result.scientificDisease,
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = AgriTextSecondary
                    )
                }
            }

            // Metrics: Confidence & Severity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCircleBadge(
                    label = stringResource(R.string.confidence_score),
                    value = confidenceScore,
                    color = AgriGreenPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricCircleBadge(
                    label = stringResource(R.string.affected_severity),
                    value = if (isHealthy) "0%" else "$affectedPercent%",
                    color = if (isHealthy) AgriHealthyGreen else if (affectedPercent > 50) AgriDangerRed else AgriWarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            // Pathology Symptoms & Reasoning
            if (reasoning.isNotBlank()) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = AgriGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.visual_observations),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = reasoning,
                        fontSize = 13.sp,
                        color = AgriTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // Treatment Action Button (if disease detected)
            Button(
                onClick = { onNavigate(TreatmentNav(result)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isHealthy) AgriHealthyGreen else AgriGreenPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.view_treatment),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Scan another
            OutlinedButton(
                onClick = { onNavigate(ScanCropNav) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.scan_another),
                    fontSize = 14.sp,
                    color = AgriGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
