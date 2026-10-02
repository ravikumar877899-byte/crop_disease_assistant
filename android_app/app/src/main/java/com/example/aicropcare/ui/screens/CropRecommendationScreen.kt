package com.example.aicropcare.ui.screens

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.runtime.saveable.rememberSaveable
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import com.example.aicropcare.utils.SuitabilityLevel
import com.example.aicropcare.utils.SearchedCropResult
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aicropcare.R
import com.example.aicropcare.theme.*
import com.example.aicropcare.utils.LocationHelper
import com.example.aicropcare.viewmodel.CropRecommendationUiState
import com.example.aicropcare.viewmodel.CropRecommendationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropRecommendationScreen(
    onBack: () -> Unit,
    viewModel: CropRecommendationViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.any { it.value }
        if (isGranted) {
            viewModel.getRecommendations(context)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPromptedSettings by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (LocationHelper.hasLocationPermission(context) && LocationHelper.isLocationEnabled(context)) {
                    val currentState = viewModel.uiState.value
                    if (currentState is CropRecommendationUiState.Error && currentState.message == "location_disabled") {
                        viewModel.getRecommendations(context)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        if (!LocationHelper.hasLocationPermission(context)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.getRecommendations(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.smart_crop_recommendation_title),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AgriBackground)
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is CropRecommendationUiState.Idle,
                is CropRecommendationUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = AgriPrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.loc_based_crop_advisor),
                            color = AgriTextSecondary
                        )
                    }
                }
                is CropRecommendationUiState.PermissionDenied -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = AgriTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.loc_permission_required),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AgriPrimary)
                        ) {
                            Text(text = stringResource(id = R.string.btn_retry_permission))
                        }
                    }
                }
                is CropRecommendationUiState.Error -> {
                    if (state.message == "location_disabled") {
                        LaunchedEffect(Unit) {
                            if (!hasPromptedSettings) {
                                hasPromptedSettings = true
                                try {
                                    val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.LocationOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = AgriTextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = stringResource(id = R.string.loc_disabled_msg), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = AgriTextPrimary)
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { 
                                    try {
                                        val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AgriPrimary)
                            ) {
                                Text(text = "Open Location Settings")
                            }
                        }
                    } else {
                        val isLocationError = state.message == "location_not_found"
                        val icon = if (isLocationError) Icons.Default.LocationOff else Icons.Default.CloudOff
                        val tint = if (isLocationError) AgriTextSecondary else AgriDanger
                        val msgRes = if (isLocationError) R.string.loc_not_found_msg else R.string.weather_data_unavailable
                        
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = tint
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(id = msgRes),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = AgriTextPrimary
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.getRecommendations(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = AgriPrimary)
                            ) {
                                Text(text = stringResource(id = R.string.btn_retry))
                            }
                        }
                    }
                }
                is CropRecommendationUiState.Success -> {
                    val scrollState = rememberScrollState()
                    var searchQuery by remember { mutableStateOf("") }
                    val context = LocalContext.current
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Search Field
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text(stringResource(id = R.string.search_crop_hint)) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AgriPrimary,
                                    unfocusedBorderColor = AgriPrimary.copy(alpha = 0.5f),
                                    focusedLabelColor = AgriPrimary,
                                    unfocusedLabelColor = AgriPrimary.copy(alpha = 0.5f),
                                    focusedTextColor = AgriPrimaryDark,
                                    unfocusedTextColor = AgriPrimaryDark,
                                    cursorColor = AgriPrimary
                                )
                            )
                            Button(
                                onClick = { 
                                    if (searchQuery.isNotBlank()) {
                                        viewModel.analyzeSearchedCrop(searchQuery)
                                    } else {
                                        viewModel.clearSearchResult()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AgriPrimary),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(stringResource(id = R.string.btn_check_crop))
                            }
                        }

                        // Header Card: Weather & Location Info
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = AgriPrimaryContainer.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = AgriPrimaryDark)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = state.weather.locationName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriPrimaryDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("🌡️ ${stringResource(R.string.label_temperature)} ${state.weather.temperature}°C", color = AgriTextPrimary)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("💧 ${stringResource(R.string.label_water)} ${state.weather.humidity}%", color = AgriTextPrimary)
                                    }
                                    Column {
                                        Text("🌧️ ${stringResource(R.string.label_rainfall)} ${state.weather.rainAmount} mm", color = AgriTextPrimary)
                                    }
                                }
                            }
                        }

                        // Recommendations List
                        if (searchQuery.isNotBlank() && (state.searchResult != null || state.searchError != null)) {
                            SearchedCropResultCard(
                                result = state.searchResult,
                                errorMsg = if (state.searchError != null) stringResource(id = R.string.crop_not_found) else null,
                                locationName = state.weather.locationName,
                                temperature = state.weather.temperature,
                                humidity = state.weather.humidity,
                                rainfall = state.weather.rainAmount
                            )
                        } else {
                            state.crops.forEach { crop ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AgriSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = stringResource(crop.nameResId),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriPrimaryDark
                                    )
                                    val suitabilityColor = when(crop.suitabilityDescResId) {
                                        R.string.crop_suitable_current -> AgriSuccess
                                        R.string.crop_moderate_current -> androidx.compose.ui.graphics.Color(0xFFF59E0B)
                                        else -> AgriDanger
                                    }
                                    Text(
                                        text = stringResource(crop.suitabilityDescResId),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = suitabilityColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Details
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text(stringResource(R.string.label_temperature), style = MaterialTheme.typography.labelMedium, color = AgriTextSecondary)
                                            Text(stringResource(crop.tempSuitabilityResId), style = MaterialTheme.typography.bodyMedium)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(stringResource(R.string.label_water), style = MaterialTheme.typography.labelMedium, color = AgriTextSecondary)
                                            Text(stringResource(crop.waterReqResId), style = MaterialTheme.typography.bodyMedium)
                                        }
                                        Column {
                                            Text(stringResource(R.string.label_rainfall), style = MaterialTheme.typography.labelMedium, color = AgriTextSecondary)
                                            Text(stringResource(crop.rainSuitabilityResId), style = MaterialTheme.typography.bodyMedium)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(stringResource(R.string.label_season), style = MaterialTheme.typography.labelMedium, color = AgriTextSecondary)
                                            Text(stringResource(crop.seasonResId), style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(color = AgriBgLight, shape = RoundedCornerShape(8.dp)) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(stringResource(R.string.label_why), fontWeight = FontWeight.Bold, color = AgriPrimaryDark)
                                            Text(stringResource(crop.suitabilityReasonResId), style = MaterialTheme.typography.bodyMedium)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(stringResource(R.string.label_precaution), fontWeight = FontWeight.Bold, color = AgriDanger)
                                            Text(stringResource(crop.precautionResId), style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }

                        }
                        // Safety Note
                        Text(
                            text = stringResource(id = R.string.agricultural_safety_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CropSearchSection(
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.search_crop_hint)) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { 
                if (searchQuery.isNotBlank()) onSearch(searchQuery)
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AgriPrimary,
                focusedLabelColor = AgriPrimary
            )
        )
        
        Button(
            onClick = { if (searchQuery.isNotBlank()) onSearch(searchQuery) },
            colors = ButtonDefaults.buttonColors(containerColor = AgriPrimary)
        ) {
            Text(stringResource(R.string.btn_check_crop))
        }
    }
}

@Composable
fun SearchedCropResultCard(
    result: SearchedCropResult?,
    errorMsg: String?,
    locationName: String,
    temperature: Double,
    humidity: Int,
    rainfall: Double
) {
    if (result == null && errorMsg == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.crop_analysis_result),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriPrimaryDark
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (errorMsg != null) {
                Text(
                    text = stringResource(R.string.crop_not_available_analysis),
                    color = AgriDanger,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else if (result != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${result.emoji} ${result.cropName}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                val suitabilityStr = when (result.suitability) {
                    SuitabilityLevel.SUITABLE -> stringResource(R.string.crop_suitable_current)
                    SuitabilityLevel.MODERATE -> stringResource(R.string.crop_moderate_current)
                    SuitabilityLevel.UNSUITABLE -> stringResource(R.string.crop_unsuitable_current)
                }
                val color = when (result.suitability) {
                    SuitabilityLevel.SUITABLE -> Color(0xFF2E7D32)
                    SuitabilityLevel.MODERATE -> Color(0xFFF57F17)
                    SuitabilityLevel.UNSUITABLE -> AgriDanger
                }
                Text(text = suitabilityStr, color = color, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "${stringResource(R.string.label_temperature)} ${temperature}°C", style = MaterialTheme.typography.bodySmall)
                Text(text = "${stringResource(R.string.label_water)} ${humidity}%", style = MaterialTheme.typography.bodySmall)
                Text(text = "${stringResource(R.string.label_rainfall)} ${rainfall} mm", style = MaterialTheme.typography.bodySmall)
                Text(text = "${stringResource(R.string.crop_location_label)} $locationName", style = MaterialTheme.typography.bodySmall)
                
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(R.string.label_why), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Text(text = stringResource(result.reasonResId), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
