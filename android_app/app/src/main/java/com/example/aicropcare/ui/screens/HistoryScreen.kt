package com.example.aicropcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aicropcare.theme.AgriBackground
import com.example.aicropcare.theme.AgriPrimaryDark
import com.example.aicropcare.theme.AgriTextSecondary
import com.example.aicropcare.ui.components.EmptyState

@Composable
fun HistoryScreen(
    onNavigateToScan: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header Section
        Text(
            text = "Scan History",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AgriPrimaryDark
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "View records of previously analyzed crop leaves.",
            style = MaterialTheme.typography.bodyMedium,
            color = AgriTextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Empty State Card
        EmptyState(
            icon = Icons.Default.FolderOpen,
            title = "No crop scans available yet.",
            description = "Your analyzed crop scans will appear here.",
            actionButtonText = "Start New Scan",
            onActionClick = onNavigateToScan
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
