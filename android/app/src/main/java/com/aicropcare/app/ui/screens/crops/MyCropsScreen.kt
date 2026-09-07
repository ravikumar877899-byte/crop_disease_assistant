package com.aicropcare.app.ui.screens.crops

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicropcare.app.ui.components.AppTopBar
import com.aicropcare.app.ui.components.CropItemCard
import com.aicropcare.app.ui.components.EmptyState
import com.aicropcare.app.viewmodel.MainViewModel

@Composable
fun MyCropsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "My Crops"
            )
        },
        floatingActionButton = {
            if (uiState.crops.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { viewModel.showPlaceholderMessage("Add crop feature") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Crop"
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (uiState.crops.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Eco,
                    title = "No crops added yet.",
                    description = "Add your crops here to monitor their growth stages, health condition, and receive personalized care alerts.",
                    actionButtonText = "Add Crop",
                    onActionClick = { viewModel.showPlaceholderMessage("Add crop feature") },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.crops, key = { it.id }) { crop ->
                        CropItemCard(
                            crop = crop,
                            onClick = {
                                viewModel.showPlaceholderMessage("Crop detail for ${crop.name}")
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }
}
