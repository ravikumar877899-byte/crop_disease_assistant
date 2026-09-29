package com.example.aicropcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.aicropcare.R
import com.example.aicropcare.data.models.ScanHistoryRecord
import com.example.aicropcare.repository.HistoryRepository
import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.EmptyState
import com.example.aicropcare.utils.DiseaseTranslator
import androidx.compose.ui.layout.ContentScale

@Composable
fun HistoryScreen(
    historyRepository: HistoryRepository,
    onNavigateToScan: () -> Unit,
    onHistoryItemClick: (PredictionResponse, String?) -> Unit
) {
    var historyItems by remember { mutableStateOf<List<ScanHistoryRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        historyItems = historyRepository.getHistory().sortedByDescending { it.timestamp }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_scan_history),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AgriPrimaryDark
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.view_records),
            style = MaterialTheme.typography.bodyMedium,
            color = AgriTextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AgriGreenPrimary)
            }
        } else if (historyItems.isEmpty()) {
            EmptyState(
                icon = Icons.Default.FolderOpen,
                title = stringResource(R.string.no_crop_scans),
                description = "Your analyzed crop scans will appear here.",
                actionButtonText = "Start New Scan",
                onActionClick = onNavigateToScan
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyItems) { record ->
                    HistoryItemCard(
                        record = record,
                        onClick = { onHistoryItemClick(record.toPredictionResponse(), record.imagePath) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(record: ScanHistoryRecord, onClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!record.imagePath.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(java.io.File(record.imagePath!!))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Crop Image",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AgriGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = AgriGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = DiseaseTranslator.getLocalizedDiseaseName(context, record.diseaseName),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriPrimaryDark,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = DiseaseTranslator.getLocalizedCropName(context, record.cropName),
                    fontSize = 14.sp,
                    color = AgriTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (record.isHealthy) AgriGreenPrimary else AgriWarningAmber)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = record.formattedDate,
                        fontSize = 12.sp,
                        color = AgriTextSecondary
                    )
                }
            }
        }
    }
}
