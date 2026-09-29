package com.example.aicropcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aicropcare.R
import com.example.aicropcare.network.DiseaseRiskLevel
import com.example.aicropcare.network.DiseaseSeverity
import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseDetectionScreen(
    result: PredictionResponse,
    onNavigateToTreatment: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isHealthy = result.isHealthy
    val severity = result.parsedSeverity
    val riskLevel = result.parsedRiskLevel

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.disease_detection_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriPrimaryDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Crop Name Card
            MetricCard(
                icon = Icons.Default.Eco,
                iconColor = AgriPrimary,
                label = stringResource(id = R.string.title_crop_name),
                value = com.example.aicropcare.utils.DiseaseTranslator.getLocalizedCropName(androidx.compose.ui.platform.LocalContext.current, result.displayCrop),
                valueColor = AgriPrimaryDark
            )

            // 2. Disease Name Card
            MetricCard(
                icon = if (isHealthy) Icons.Default.CheckCircle else Icons.Default.Coronavirus,
                iconColor = if (isHealthy) AgriSuccess else AgriDanger,
                label = stringResource(id = R.string.title_disease_name),
                value = if (isHealthy) stringResource(R.string.severity_healthy) else com.example.aicropcare.utils.DiseaseTranslator.getLocalizedDiseaseName(androidx.compose.ui.platform.LocalContext.current, result.displayDisease),
                valueColor = if (isHealthy) AgriSuccess else AgriDanger
            )

            // 3. Confidence Card
            MetricCard(
                icon = Icons.Default.TrackChanges,
                iconColor = AgriPrimary,
                label = stringResource(id = R.string.title_confidence),
                value = "${result.confidenceValue}%",
                valueColor = AgriPrimaryDark
            )

            // 4. Affected Area Card
            val displayPct = if (isHealthy) 0 else result.affectedPercentageValue
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, AgriBorder, RoundedCornerShape(16.dp)),
                color = AgriSurface,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = AgriPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.result_affected_area_title),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (displayPct > 0) "$displayPct%" else "0%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isHealthy) AgriSuccess else if (displayPct > 50) AgriDanger else AgriPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (displayPct / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = if (isHealthy) AgriSuccess else if (displayPct > 50) AgriDanger else AgriSecondary,
                        trackColor = AgriBorder.copy(alpha = 0.5f)
                    )
                }
            }

            // 5. Disease Severity Card
            val (sevColor, sevBgColor, sevTextRes) = when (severity) {
                DiseaseSeverity.HEALTHY -> Triple(AgriSuccess, AgriSuccessContainer, R.string.severity_healthy)
                DiseaseSeverity.MILD -> Triple(AgriSecondaryDark, AgriSecondaryContainer, R.string.severity_mild)
                DiseaseSeverity.MODERATE -> Triple(Color(0xFFEA580C), Color(0xFFFFF7ED), R.string.severity_moderate)
                DiseaseSeverity.SEVERE -> Triple(AgriDanger, AgriDangerContainer, R.string.severity_severe)
                DiseaseSeverity.UNKNOWN -> Triple(AgriTextSecondary, AgriPrimaryContainer, R.string.severity_unknown)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, sevColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                color = AgriSurface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = sevColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.severity_title),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = sevBgColor
                    ) {
                        Text(
                            text = stringResource(id = sevTextRes),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = sevColor
                        )
                    }
                }
            }

            // 6. Disease Risk Level Card
            val (riskColor, riskBgColor, riskTextRes, riskIcon) = when (riskLevel) {
                DiseaseRiskLevel.NO_RISK -> DetectionQuadruple(AgriSuccess, AgriSuccessContainer, R.string.risk_no_risk, Icons.Default.CheckCircle)
                DiseaseRiskLevel.LOW -> DetectionQuadruple(AgriSuccess, AgriSuccessContainer, R.string.risk_low, Icons.Default.CheckCircle)
                DiseaseRiskLevel.MEDIUM -> DetectionQuadruple(Color(0xFFEA580C), Color(0xFFFFF7ED), R.string.risk_medium, Icons.Default.WarningAmber)
                DiseaseRiskLevel.HIGH -> DetectionQuadruple(AgriDanger, AgriDangerContainer, R.string.risk_high, Icons.Default.Dangerous)
                DiseaseRiskLevel.UNKNOWN -> DetectionQuadruple(AgriTextSecondary, AgriPrimaryContainer, R.string.risk_unknown, Icons.Default.Info)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, riskColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                color = AgriSurface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = riskIcon,
                            contentDescription = null,
                            tint = riskColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.risk_title),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = riskBgColor
                    ) {
                        Text(
                            text = stringResource(id = riskTextRes),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = riskColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Only Navigation Button on Page 1: View Treatment & Advice
            Button(
                onClick = onNavigateToTreatment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AgriPrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.btn_view_treatment_advice),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    valueColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, AgriBorder, RoundedCornerShape(16.dp)),
        color = AgriSurface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

private data class DetectionQuadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
