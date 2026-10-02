package com.example.aicropcare.ui.screens

import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.aicropcare.utils.TreatmentVoiceHelper
import com.example.aicropcare.utils.LocaleHelper
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aicropcare.R
import com.example.aicropcare.network.DiseaseSeverity
import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentAdviceScreen(
    result: PredictionResponse,
    onBack: () -> Unit,
    onRescanCrop: () -> Unit
) {
    val context = LocalContext.current
    val voiceHelper = remember { TreatmentVoiceHelper(context) }
    val isSpeaking by voiceHelper.isSpeaking.collectAsState()
    val ttsError by voiceHelper.error.collectAsState()
    
    DisposableEffect(Unit) {
        onDispose {
            voiceHelper.shutdown()
        }
    }
    
    val diseasePrefix = stringResource(R.string.title_disease_name)
    val immediateActionPrefix = stringResource(R.string.result_immediate_action_title)
    val managementPrefix = stringResource(R.string.treatment_cultural_mgmt_title)
    val medicinePrefix = stringResource(R.string.treatment_medicine_guidance_title)
    val weatherPrefix = stringResource(R.string.weather_precaution_title)
    val avoidPrefix = stringResource(R.string.treatment_avoid_title)
    val safetyPrefix = stringResource(R.string.treatment_safety_title)
    
    val fullSpeechText = remember(result, diseasePrefix, immediateActionPrefix, managementPrefix, medicinePrefix, weatherPrefix, avoidPrefix, safetyPrefix) {
        buildString {
            append("$diseasePrefix: ${result.displayDisease}. ")
            
            val plan = result.resolvedTreatmentPlan
            if (!plan.immediateAction.isNullOrBlank()) {
                append("$immediateActionPrefix: ${plan.immediateAction}. ")
            }
            
            if (!plan.management.isNullOrEmpty()) {
                append("$managementPrefix: ")
                plan.management.forEach { append("$it. ") }
            }
            
            if (!plan.medicineGuidance.isNullOrEmpty()) {
                append("$medicinePrefix: ")
                plan.medicineGuidance.forEach { med ->
                    append("${med.activeIngredient ?: ""} for ${med.purpose ?: ""}. ")
                }
            }
            
            if (!plan.weatherPrecaution.isNullOrBlank()) {
                append("$weatherPrefix: ${plan.weatherPrecaution}. ")
            }
            
            if (!plan.avoid.isNullOrEmpty()) {
                append("$avoidPrefix: ")
                plan.avoid.forEach { append("$it. ") }
            }
            
            if (!plan.safetyPrecautions.isNullOrEmpty()) {
                append("$safetyPrefix: ")
                plan.safetyPrecautions.forEach { append("$it. ") }
            }
        }
    }

    val scrollState = rememberScrollState()
    val isHealthy = result.isHealthy
    val severity = result.parsedSeverity
    val treatmentPlan = result.resolvedTreatmentPlan
    val treatmentCategory = treatmentPlan.treatmentCategory?.uppercase() ?: (if (isHealthy) "PREVENTIVE_CARE" else "CONFIRMED_TREATMENT")

    val (catBadgeTextRes, catBadgeColor, catBadgeBg) = when (treatmentCategory) {
        "CONFIRMED_TREATMENT" -> Triple(R.string.treatment_category_confirmed, AgriSuccess, AgriSuccessContainer)
        "GENERAL_MANAGEMENT" -> Triple(R.string.treatment_category_management, AgriSecondaryDark, AgriSecondaryContainer)
        "EXPERT_CONSULTATION" -> Triple(R.string.treatment_category_expert, AgriDanger, AgriDangerContainer)
        else -> Triple(R.string.treatment_category_preventive, AgriSuccess, AgriSuccessContainer)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.treatment_screen_title),
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TTS Voice Control
            if (ttsError != null) {
                Surface(
                    color = AgriDangerContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = if (ttsError == "voice_language_unavailable") R.string.voice_language_unavailable else R.string.voice_initialization_failed),
                        color = AgriDanger,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                OutlinedButton(
                    onClick = {
                        if (isSpeaking) {
                            voiceHelper.stop()
                        } else {
                            val currentLang = java.util.Locale.getDefault().language
                            voiceHelper.speak(fullSpeechText, currentLang)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isSpeaking) AgriDanger else AgriPrimaryDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSpeaking) AgriDanger else AgriPrimary)
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = if (isSpeaking) R.string.stop_treatment else R.string.listen_treatment),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 1. SMART TREATMENT & MANAGEMENT PLAN (Category Badge, Objective, Immediate Action)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.5.dp, AgriPrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                color = AgriSurface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = AgriPrimaryDark,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = if (isHealthy) stringResource(id = R.string.treatment_preventive_title) else stringResource(id = R.string.treatment_smart_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriPrimaryDark
                            )
                        }
                    }

                    // Category Badge
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = catBadgeBg
                    ) {
                        Text(
                            text = stringResource(id = catBadgeTextRes),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = catBadgeColor
                        )
                    }

                    // Treatment Objective
                    val objective = treatmentPlan.treatmentObjective
                    if (!objective.isNullOrBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = AgriBgLight,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = stringResource(id = R.string.treatment_objective_title),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriPrimaryDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = objective,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    // Immediate Action
                    val immediateAction = treatmentPlan.immediateAction ?: result.displayImmediateAction
                    if (!immediateAction.isNullOrBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = AgriPrimaryContainer.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = stringResource(id = R.string.result_immediate_action_title),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriPrimaryDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = immediateAction,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // 2. RECOMMENDED CULTURAL & FIELD MANAGEMENT
            val mgmtList = treatmentPlan.management
            if (!mgmtList.isNullOrEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, AgriBorder, RoundedCornerShape(18.dp)),
                    color = AgriSurface,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = AgriPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.treatment_cultural_mgmt_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriPrimaryDark
                            )
                        }

                        mgmtList.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriPrimary
                                )
                                Text(
                                    text = item.replaceFirst(Regex("^\\\\d+\\\\.\\\\s*"), ""),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. MEDICINE / ACTIVE INGREDIENT CARDS
            val medicines = treatmentPlan.medicineGuidance
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.5.dp, AgriPrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                color = AgriSurface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = AgriPrimaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.treatment_medicine_guidance_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                    }

                    if (!medicines.isNullOrEmpty()) {
                        medicines.forEach { med ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, AgriBorder, RoundedCornerShape(12.dp)),
                                color = AgriBgLight,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "${stringResource(id = R.string.treatment_active_ingredient_label)}:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = med.activeIngredient ?: "",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = AgriPrimaryDark
                                        )
                                    }

                                    if (!med.purpose.isNullOrBlank()) {
                                        Text(
                                            text = "• ${stringResource(id = R.string.treatment_purpose_label)}: ${med.purpose}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Black
                                        )
                                    }

                                    if (!med.applicationMethod.isNullOrBlank()) {
                                        Text(
                                            text = "• ${stringResource(id = R.string.treatment_application_label)}: ${med.applicationMethod}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Black
                                        )
                                    }

                                    if (!med.timing.isNullOrBlank()) {
                                        Text(
                                            text = "• ${stringResource(id = R.string.treatment_timing_label)}: ${med.timing}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // No medicines needed / Healthy note
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, AgriBorder, RoundedCornerShape(12.dp)),
                            color = if (isHealthy) AgriSuccessContainer.copy(alpha = 0.4f) else AgriBgLight,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isHealthy) Icons.Default.Spa else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isHealthy) AgriSuccess else AgriTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isHealthy) stringResource(id = R.string.treatment_no_chemicals_healthy) else stringResource(id = R.string.treatment_no_chemicals_needed),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Black,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // 4. WEATHER-BASED SPRAYING WINDOW
            val sprayingStatus = treatmentPlan.parsedSprayingWindow
            val (sprayColor, sprayBg, sprayTextRes, sprayIcon) = when (sprayingStatus) {
                com.example.aicropcare.network.SprayingWindowStatus.GOOD -> TreatmentQuadruple(AgriSuccess, AgriSuccessContainer, R.string.treatment_spraying_good, Icons.Default.CheckCircle)
                com.example.aicropcare.network.SprayingWindowStatus.CAUTION -> TreatmentQuadruple(Color(0xFFEA580C), Color(0xFFFFF7ED), R.string.treatment_spraying_caution, Icons.Default.WarningAmber)
                com.example.aicropcare.network.SprayingWindowStatus.AVOID -> TreatmentQuadruple(AgriDanger, AgriDangerContainer, R.string.treatment_spraying_avoid, Icons.Default.Dangerous)
                com.example.aicropcare.network.SprayingWindowStatus.UNAVAILABLE -> TreatmentQuadruple(AgriTextSecondary, AgriPrimaryContainer, R.string.treatment_spraying_unavailable, Icons.Default.Info)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, sprayColor.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                color = sprayBg,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = sprayIcon,
                                contentDescription = null,
                                tint = sprayColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.treatment_spraying_window_title),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(id = sprayTextRes),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = sprayColor
                    )

                    val sprayReason = treatmentPlan.sprayingReason
                    if (!sprayReason.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sprayReason,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // 5. WEATHER PRECAUTION
            val weatherPrecaution = treatmentPlan.weatherPrecaution
            if (!weatherPrecaution.isNullOrBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, AgriSecondaryDark.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                    color = AgriSecondaryContainer.copy(alpha = 0.4f),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = AgriSecondaryDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.weather_precaution_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriSecondaryDark
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = weatherPrecaution,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // 6. THINGS TO AVOID
            val avoidList = treatmentPlan.avoid
            if (!avoidList.isNullOrEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, AgriDanger.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                    color = AgriDangerContainer.copy(alpha = 0.35f),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = AgriDanger,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.treatment_avoid_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriDanger
                            )
                        }

                        avoidList.forEach { avoidItem ->
                            Text(
                                text = "• $avoidItem",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // 7. SAFETY PRECAUTIONS
            val safetyList = treatmentPlan.safetyPrecautions
            if (!safetyList.isNullOrEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, AgriPrimaryDark.copy(alpha = 0.2f), RoundedCornerShape(18.dp)),
                    color = AgriPrimaryContainer.copy(alpha = 0.3f),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = AgriPrimaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.treatment_safety_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriPrimaryDark
                            )
                        }

                        safetyList.forEach { safetyItem ->
                            Text(
                                text = "• $safetyItem",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // 8. EXPERT CONSULTATION CARD
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, AgriPrimaryLight, RoundedCornerShape(18.dp)),
                color = AgriSurface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = AgriPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(id = R.string.expert_consultation_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(id = R.string.expert_consultation_msg),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // 9. SEPARATE DISCLAIMER CARD
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, AgriBorder, RoundedCornerShape(18.dp)),
                color = AgriSurfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Policy,
                        contentDescription = null,
                        tint = AgriTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(id = R.string.disclaimer_card_title),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(id = R.string.treatment_label_disclaimer),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 10. RESCAN RECOMMENDATION
            val rescanAdviceRes = when {
                isHealthy -> R.string.rescan_advice_healthy
                severity == DiseaseSeverity.MILD -> R.string.rescan_advice_mild
                severity == DiseaseSeverity.MODERATE -> R.string.rescan_advice_moderate
                severity == DiseaseSeverity.SEVERE -> R.string.rescan_advice_severe
                else -> R.string.rescan_advice_default
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, AgriSecondary.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                color = AgriSecondaryContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = null,
                        tint = AgriSecondaryDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(id = R.string.result_rescan_title),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(id = rescanAdviceRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private data class TreatmentQuadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
