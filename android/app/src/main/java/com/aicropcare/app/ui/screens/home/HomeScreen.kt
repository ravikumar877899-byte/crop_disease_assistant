package com.aicropcare.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aicropcare.app.ui.components.AppCard
import com.aicropcare.app.ui.components.CropOverviewCard
import com.aicropcare.app.ui.components.HeroScanCard
import com.aicropcare.app.ui.components.QuickActionCard
import com.aicropcare.app.ui.components.SectionTitle
import com.aicropcare.app.ui.theme.AgriGreenPrimary
import com.aicropcare.app.ui.theme.HarvestTertiary
import com.aicropcare.app.ui.theme.InfoBlue
import com.aicropcare.app.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    farmerName: String = "Farmer",
    onNavigateToScan: () -> Unit,
    onNavigateToCrops: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header Greeting
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, $farmerName 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Let's take care of your crops today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { viewModel.showPlaceholderMessage("Notifications") },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main AI Scan Hero Card
        HeroScanCard(
            onScanClick = onNavigateToScan
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions Section
        SectionTitle(title = "Quick Actions")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "Scan Crop",
                icon = Icons.Filled.CenterFocusStrong,
                iconColor = AgriGreenPrimary,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = onNavigateToScan,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "My Crops",
                icon = Icons.Filled.Eco,
                iconColor = Color(0xFF388E3C),
                backgroundColor = Color(0xFFE8F5E9),
                onClick = onNavigateToCrops,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "Disease History",
                icon = Icons.Filled.History,
                iconColor = HarvestTertiary,
                backgroundColor = Color(0xFFFFF8E1),
                onClick = onNavigateToHistory,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "Weather",
                icon = Icons.Filled.Cloud,
                iconColor = InfoBlue,
                backgroundColor = Color(0xFFE3F2FD),
                onClick = { viewModel.showPlaceholderMessage("Weather forecast service") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Crop Overview Section
        SectionTitle(title = "Crop Management")

        CropOverviewCard(
            cropCount = uiState.crops.size,
            onAddCropClick = { viewModel.showPlaceholderMessage("Add crop feature") },
            onViewCropsClick = onNavigateToCrops
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Farming Advisory Banner Tip
        AppCard(
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Farming Tip",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Inspect crop leaves early morning for the best disease detection lighting.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
