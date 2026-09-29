package com.example.aicropcare.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aicropcare.R
import com.example.aicropcare.data.models.ScanHistoryRecord
import com.example.aicropcare.repository.CropHealthProgressRepository
import com.example.aicropcare.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropHealthProgressScreen(
    repository: CropHealthProgressRepository,
    onBack: () -> Unit,
    onNavigateToScan: () -> Unit = {},
    initialCropName: String? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var distinctCrops by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedCrop by remember { mutableStateOf<String?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var progressResult by remember { mutableStateOf<CropHealthProgressResult?>(null) }
    var cropRecords by remember { mutableStateOf<List<ScanHistoryRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadDataForCrop(crop: String) {
        isLoading = true
        coroutineScope.launch {
            val records = repository.getRecordsForCrop(crop)
            val result = repository.getProgressForCrop(crop)
            cropRecords = records
            progressResult = result
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        val crops = repository.getDistinctCrops()
        distinctCrops = crops
        if (crops.isNotEmpty()) {
            val initialCrop = crops.first()
            selectedCrop = initialCrop
            loadDataForCrop(initialCrop)
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.crop_health_screen_title),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AgriGreenPrimary
                )
            )
        },
        containerColor = AgriBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AgriGreenPrimary)
                }
            } else if (distinctCrops.isEmpty() || progressResult == null || progressResult?.summary == CropHealthSummary.NO_DATA) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AgriGreenPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ShowChart,
                            contentDescription = null,
                            tint = AgriGreenPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.crop_health_empty_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.crop_health_empty_desc),
                        fontSize = 14.sp,
                        color = AgriTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateToScan,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.crop_health_scan_action),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                val result = progressResult!!
                val latest = result.latestRecord

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Crop Selector Dropdown (when multiple crops exist) or Title
                    item {
                        if (distinctCrops.size > 1) {
                            ExposedDropdownMenuBox(
                                expanded = isDropdownExpanded,
                                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedCrop ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.crop_health_select_crop)) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AgriGreenPrimary,
                                        unfocusedBorderColor = AgriBorder,
                                        focusedLabelColor = AgriGreenPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = isDropdownExpanded,
                                    onDismissRequest = { isDropdownExpanded = false }
                                ) {
                                    distinctCrops.forEach { crop ->
                                        DropdownMenuItem(
                                            text = { Text(crop, fontWeight = if (crop == selectedCrop) FontWeight.Bold else FontWeight.Normal) },
                                            onClick = {
                                                selectedCrop = crop
                                                isDropdownExpanded = false
                                                loadDataForCrop(crop)
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Eco,
                                        contentDescription = null,
                                        tint = AgriGreenPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = selectedCrop ?: "",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriPrimaryDark
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = stringResource(R.string.crop_health_total_scans, result.totalScans),
                                        fontSize = 12.sp,
                                        color = AgriTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Progress Status Badge & Current Health Summary
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.crop_health_current_status_title),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriPrimaryDark
                                    )
                                    ProgressStatusBadge(summary = result.summary)
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = AgriBorder.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))

                                latest?.let { rec ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        SummaryMetricItem(
                                            label = stringResource(R.string.crop_health_current_disease),
                                            value = com.example.aicropcare.utils.DiseaseTranslator.getLocalizedDiseaseName(androidx.compose.ui.platform.LocalContext.current, rec.diseaseName),
                                            modifier = Modifier.weight(1f)
                                        )
                                        SummaryMetricItem(
                                            label = stringResource(R.string.crop_health_current_severity),
                                            value = rec.severity,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        SummaryMetricItem(
                                            label = stringResource(R.string.crop_health_current_risk),
                                            value = rec.riskLevel,
                                            modifier = Modifier.weight(1f)
                                        )
                                        SummaryMetricItem(
                                            label = stringResource(R.string.crop_health_current_affected),
                                            value = "${rec.affectedPercentage}%",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Line Chart Card (Affected Area Trend)
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.crop_health_trend_title),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriPrimaryDark
                                    )
                                    Text(
                                        text = stringResource(R.string.crop_health_total_scans, cropRecords.size),
                                        fontSize = 12.sp,
                                        color = AgriTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                CropHealthLineChart(
                                    records = cropRecords,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(190.dp)
                                )
                            }
                        }
                    }

                    // Scan Timeline Section Header
                    item {
                        Text(
                            text = stringResource(R.string.crop_health_timeline_title),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Timeline Items (chronological order reversed for display - newest on top)
                    val displayRecords = cropRecords.asReversed()
                    items(displayRecords) { record ->
                        TimelineRecordCard(record = record)
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressStatusBadge(summary: CropHealthSummary) {
    val (textRes, bgColor, textColor) = when (summary) {
        CropHealthSummary.HEALTHY -> Triple(
            R.string.crop_health_summary_healthy,
            AgriGreenContainer,
            AgriGreenPrimary
        )
        CropHealthSummary.IMPROVING -> Triple(
            R.string.crop_health_summary_improving,
            Color(0xFFE0F2FE),
            Color(0xFF0369A1)
        )
        CropHealthSummary.STABLE -> Triple(
            R.string.crop_health_summary_stable,
            Color(0xFFFEF3C7),
            Color(0xFFB45309)
        )
        CropHealthSummary.NEEDS_ATTENTION -> Triple(
            R.string.crop_health_summary_needs_attention,
            AgriDangerContainer,
            AgriDanger
        )
        CropHealthSummary.NO_DATA -> Triple(
            R.string.crop_health_summary_no_data,
            Color(0xFFF3F4F6),
            Color(0xFF6B7280)
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = stringResource(textRes),
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun SummaryMetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = AgriTextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary
        )
    }
}

@Composable
fun TimelineRecordCard(record: ScanHistoryRecord) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (record.isHealthy) AgriGreenPrimary else AgriWarningAmber)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = com.example.aicropcare.utils.DiseaseTranslator.getLocalizedDiseaseName(androidx.compose.ui.platform.LocalContext.current, record.diseaseName),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriPrimaryDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = record.formattedDate.ifBlank { "Scan #${record.id}" },
                    fontSize = 12.sp,
                    color = AgriTextSecondary
                )
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (record.isHealthy) AgriGreenContainer else Color(0xFFF3F4F6)
            ) {
                Text(
                    text = "${record.affectedPercentage}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (record.isHealthy) AgriGreenPrimary else AgriTextPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Lightweight custom Jetpack Compose Canvas Line Chart.
 * Safely handles 0, 1, 2, or many scans.
 * Plots affected area percentage (0-100%) against scan sequence.
 */
@Composable
fun CropHealthLineChart(
    records: List<ScanHistoryRecord>,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.crop_health_chart_no_data),
                color = AgriTextSecondary,
                fontSize = 13.sp
            )
        }
        return
    }

    val primaryColor = AgriGreenPrimary
    val gridColor = Color(0xFFE5E7EB)
    val pointColor = AgriPrimaryDark
    val textPaintColor = android.graphics.Color.DKGRAY

    Canvas(modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        val width = size.width
        val height = size.height

        val paddingLeft = 40f
        val paddingRight = 30f
        val paddingTop = 25f
        val paddingBottom = 35f

        val plotWidth = (width - paddingLeft - paddingRight).coerceAtLeast(1f)
        val plotHeight = (height - paddingTop - paddingBottom).coerceAtLeast(1f)

        // Draw horizontal grid lines for 0%, 25%, 50%, 75%, 100%
        val gridLevels = listOf(0, 25, 50, 75, 100)
        for (level in gridLevels) {
            val y = paddingTop + plotHeight * (1f - level / 100f)
            drawLine(
                color = gridColor,
                start = Offset(paddingLeft, y),
                end = Offset(width - paddingRight, y),
                strokeWidth = 1f
            )

            // Draw Y-axis percentage labels
            drawContext.canvas.nativeCanvas.drawText(
                "$level%",
                paddingLeft - 8f,
                y + 4f,
                android.graphics.Paint().apply {
                    color = textPaintColor
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.RIGHT
                    isAntiAlias = true
                }
            )
        }

        // Draw Axis lines
        drawLine(
            color = Color.Gray,
            start = Offset(paddingLeft, paddingTop),
            end = Offset(paddingLeft, height - paddingBottom),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(paddingLeft, height - paddingBottom),
            end = Offset(width - paddingRight, height - paddingBottom),
            strokeWidth = 2f
        )

        val totalPoints = records.size
        val points = mutableListOf<Offset>()

        for (i in 0 until totalPoints) {
            val rec = records[i]
            val pct = rec.affectedPercentage.coerceIn(0, 100)
            val x = if (totalPoints == 1) {
                paddingLeft + plotWidth / 2f
            } else {
                paddingLeft + (i.toFloat() / (totalPoints - 1).toFloat()) * plotWidth
            }
            val y = paddingTop + plotHeight * (1f - pct / 100f)
            points.add(Offset(x, y))
        }

        // Draw connecting line path
        if (points.size > 1) {
            val path = Path()
            path.moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
        }

        // Draw points and percentage labels
        for (i in 0 until points.size) {
            val pt = points[i]
            val pct = records[i].affectedPercentage

            // Outer point circle
            drawCircle(
                color = Color.White,
                radius = 7f,
                center = pt
            )
            // Inner point circle
            drawCircle(
                color = if (records[i].isHealthy) AgriGreenPrimary else pointColor,
                radius = 5f,
                center = pt
            )

            // Draw value label above point
            val labelY = (pt.y - 12f).coerceAtLeast(paddingTop)
            drawContext.canvas.nativeCanvas.drawText(
                "$pct%",
                pt.x,
                labelY,
                android.graphics.Paint().apply {
                    color = textPaintColor
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    isFakeBoldText = true
                }
            )

            // Draw sequence label on X-axis (e.g., "#1", "#2")
            drawContext.canvas.nativeCanvas.drawText(
                "#${i + 1}",
                pt.x,
                height - paddingBottom + 24f,
                android.graphics.Paint().apply {
                    color = textPaintColor
                    textSize = 22f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
        }
    }
}

