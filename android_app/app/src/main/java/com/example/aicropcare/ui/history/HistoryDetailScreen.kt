package com.example.aicropcare.ui.history

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aicropcare.R
import com.example.aicropcare.data.models.HistoryItem
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.AgriTopBar
import com.example.aicropcare.ui.components.GlassCard
import com.example.aicropcare.ui.components.MetricCircleBadge
import com.example.aicropcare.ui.components.StatusBadge

@Composable
fun HistoryDetailScreen(
    item: HistoryItem,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isTamil = sessionManager.isTamil

    val cropName = item.getDisplayCrop(isTamil)
    val diseaseName = item.getDisplayDisease(isTamil)
    val treatment = item.getDisplayTreatment(isTamil)
    val isHealthy = item.isHealthy
    val formattedDate = item.timestamp?.replace("T", " ")?.take(16) ?: "Recent"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBgLight)
    ) {
        AgriTopBar(
            title = stringResource(R.string.result_title),
            subtitle = formattedDate,
            onBack = onBack,
            actions = {
                IconButton(
                    onClick = {
                        val shareText = "AI Crop Care Historical Record\nDate: $formattedDate\nCrop: $cropName\nCondition: $diseaseName\nTreatment:\n$treatment"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Crop Disease Report")
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Report"))
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Diagnosis Card
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

                if (!item.scientificCrop.isNullOrBlank()) {
                    Text(
                        text = item.scientificCrop,
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = AgriTextSecondary
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = AgriCardGlassBorder)

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
            }

            // Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCircleBadge(
                    label = stringResource(R.string.confidence_score),
                    value = "${item.confidence.toInt()}%",
                    color = AgriGreenPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricCircleBadge(
                    label = stringResource(R.string.affected_severity),
                    value = if (isHealthy) "0%" else "${item.getAffectedPercentage()}%",
                    color = if (isHealthy) AgriHealthyGreen else AgriWarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            // Treatment Card
            if (treatment.isNotBlank()) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = AgriGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.treatment_management),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = treatment,
                        fontSize = 13.sp,
                        color = AgriTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
