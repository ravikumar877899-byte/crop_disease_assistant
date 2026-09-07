package com.example.aicropcare.ui.history

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.aicropcare.HistoryDetailNav
import com.example.aicropcare.NavigationKey
import com.example.aicropcare.R
import com.example.aicropcare.data.DataRepository
import com.example.aicropcare.data.models.HistoryItem
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.AgriTopBar
import com.example.aicropcare.ui.components.GlassCard
import com.example.aicropcare.ui.components.StatusBadge
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    onNavigate: (NavigationKey) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository(context) }
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val isTamil = sessionManager.isTamil

    var historyList by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var itemToDelete by remember { mutableStateOf<HistoryItem?>(null) }

    fun loadHistory() {
        isLoading = true
        coroutineScope.launch {
            val result = repository.getHistory()
            isLoading = false
            result.onSuccess {
                historyList = it
            }.onFailure {
                Toast.makeText(context, "Could not load history", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadHistory()
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text(stringResource(R.string.delete_scan)) },
            text = { Text(stringResource(R.string.delete_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val item = itemToDelete!!
                        itemToDelete = null
                        coroutineScope.launch {
                            val res = repository.deleteHistory(item.id)
                            res.onSuccess {
                                Toast.makeText(context, "Record deleted", Toast.LENGTH_SHORT).show()
                                loadHistory()
                            }.onFailure {
                                Toast.makeText(context, "Failed to delete record", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.delete), color = AgriDangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBgLight)
    ) {
        AgriTopBar(
            title = stringResource(R.string.history_title),
            onBack = onBack,
            actions = {
                IconButton(onClick = { loadHistory() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                }
            }
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AgriGreenPrimary)
            }
        } else if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HistoryEdu,
                        contentDescription = null,
                        tint = AgriGreenPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.no_history),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    HistoryCard(
                        item = item,
                        isTamil = isTamil,
                        onClick = { onNavigate(HistoryDetailNav(item)) },
                        onDelete = { itemToDelete = item }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryCard(
    item: HistoryItem,
    isTamil: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val cropName = item.getDisplayCrop(isTamil)
    val diseaseName = item.getDisplayDisease(isTamil)
    val isHealthy = item.isHealthy
    val formattedDate = item.timestamp?.take(10) ?: "Recent"

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        backgroundColor = Color.White,
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isHealthy) AgriHealthyGreen.copy(alpha = 0.15f) else AgriDangerRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isHealthy) Icons.Default.Eco else Icons.Default.BugReport,
                    contentDescription = null,
                    tint = if (isHealthy) AgriHealthyGreen else AgriDangerRed,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cropName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = AgriTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = diseaseName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isHealthy) AgriHealthyGreen else AgriDangerRed
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Confidence: ${item.confidence.toInt()}%",
                        fontSize = 11.sp,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = AgriTextMuted
                    )
                    Text(
                        text = "Severity: ${item.getAffectedPercentage()}%",
                        fontSize = 11.sp,
                        color = if (isHealthy) AgriHealthyGreen else AgriWarningAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = AgriTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
