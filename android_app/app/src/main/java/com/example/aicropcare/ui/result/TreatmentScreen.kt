package com.example.aicropcare.ui.result

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aicropcare.R
import com.example.aicropcare.data.models.PredictionResponse
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.AgriTopBar
import com.example.aicropcare.ui.components.GlassCard

@Composable
fun TreatmentScreen(
    result: PredictionResponse,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isTamil = sessionManager.isTamil

    val cropName = result.getDisplayCrop(isTamil)
    val diseaseName = result.getDisplayDisease(isTamil)
    val treatmentText = result.getDisplayTreatment(isTamil)
    val isHealthy = result.isHealthy

    // Split treatment into actionable points
    val treatmentSteps = treatmentText
        .split(Regex("(?<=[.!?])\\s+"))
        .filter { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBgLight)
    ) {
        AgriTopBar(
            title = stringResource(R.string.treatment_title),
            subtitle = "$cropName • $diseaseName",
            onBack = onBack,
            actions = {
                IconButton(
                    onClick = {
                        val shareText = "AI Crop Care Diagnosis Report\n" +
                                "Crop: $cropName\n" +
                                "Condition: $diseaseName\n" +
                                "Treatment:\n$treatmentText"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Crop Disease Treatment Prescription")
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Treatment Plan"))
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
            // Summary Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isHealthy) AgriHealthyGreen.copy(alpha = 0.15f) else AgriWarningAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isHealthy) Icons.Default.CheckCircle else Icons.Default.Healing,
                            contentDescription = null,
                            tint = if (isHealthy) AgriHealthyGreen else AgriWarningAmber,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = diseaseName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = if (isHealthy)
                                (if (isTamil) "பயிர் ஆரோக்கியமாக உள்ளது. தடுப்பு பராமரிப்பைத் தொடரவும்." else "Crop is healthy. Continue maintenance care.")
                            else
                                (if (isTamil) "கீழே உள்ள சிகிச்சை முறைகளை உடனடியாகப் பின்பற்றவும்." else "Follow these treatment steps immediately."),
                            fontSize = 12.sp,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            // Prescribed Steps Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White
            ) {
                Text(
                    text = stringResource(R.string.treatment_management),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (treatmentSteps.isNotEmpty()) {
                    treatmentSteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(AgriGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriGreenPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step,
                                fontSize = 13.sp,
                                color = AgriTextPrimary,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    Text(
                        text = treatmentText,
                        fontSize = 13.sp,
                        color = AgriTextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }

            // Organic & Natural Remedies
            TreatmentAdviceSection(
                title = stringResource(R.string.organic_remedies),
                icon = Icons.Default.Eco,
                color = AgriHealthyGreen,
                tips = listOf(
                    if (isTamil) "வேப்பெண்ணெய் கரைசல் (Neem Oil Spray): 5 மிலி வேப்பெண்ணெய் + 1 லிட்டர் தண்ணீரில் சில துளிகள் சோப்பு சேர்த்து தெளிக்கவும்."
                    else "Neem Oil Extract: Dilute 5ml neem oil with a few drops of liquid soap per 1 liter of water and spray on leaves.",
                    if (isTamil) "புளித்த மோர்க் கரைசல் (Sour Buttermilk): பூஞ்சாண நோய்களுக்கு 100 மிலி புளித்த மோர் 1 லிட்டர் தண்ணீரில் கலந்து தெளிக்கலாம்."
                    else "Fermented Buttermilk Spray: Mix 100ml sour buttermilk with 1L water to combat mild fungal blights.",
                    if (isTamil) "3G கரைசல் (இஞ்சி, பூண்டு, பச்சை மிளகாய் சாறு): பூச்சி விரட்டியாகப் பயன்படுத்தவும்."
                    else "3G Herbal Spray: Blend ginger, garlic, and green chili extract as an organic insect and pathogen repellent."
                )
            )

            // Prevention Guidelines
            TreatmentAdviceSection(
                title = stringResource(R.string.prevention_tips),
                icon = Icons.Default.Shield,
                color = Color(0xFF0284C7),
                tips = listOf(
                    if (isTamil) "பாதிக்கப்பட்ட இலைகள் மற்றும் பயிர் எச்சங்களை அகற்றி அழிக்கவும்."
                    else "Prune and safely destroy infected leaf debris to prevent pathogen spore spread.",
                    if (isTamil) "சொட்டு நீர் பாசனத்தைப் பயன்படுத்தி இலைகளில் ஈரப்பதம் தேங்குவதைத் தவிர்க்கவும்."
                    else "Avoid overhead watering; use drip irrigation to prevent standing moisture on foliage.",
                    if (isTamil) "பயிர் சுழற்சி முறையைப் பின்பற்றவும்."
                    else "Practice crop rotation with non-host crops to break pathogen lifecycle in soil."
                )
            )
        }
    }
}

@Composable
fun TreatmentAdviceSection(
    title: String,
    icon: ImageVector,
    color: Color,
    tips: List<String>
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        tips.forEach { tip ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tip,
                    fontSize = 12.sp,
                    color = AgriTextSecondary,
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
