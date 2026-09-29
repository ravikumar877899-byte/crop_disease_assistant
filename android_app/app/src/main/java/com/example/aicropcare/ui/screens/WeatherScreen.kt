package com.example.aicropcare.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aicropcare.R
import com.example.aicropcare.network.*
import com.example.aicropcare.theme.*
import com.example.aicropcare.utils.LocationHelper
import com.example.aicropcare.utils.UserLocation
import com.example.aicropcare.viewmodel.WeatherUiState
import com.example.aicropcare.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onBack: () -> Unit,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by weatherViewModel.uiState.collectAsState()
    val currentLocation by weatherViewModel.currentLocation.collectAsState()
    val selectedCrop by weatherViewModel.selectedCrop.collectAsState()
    val selectedStage by weatherViewModel.selectedStage.collectAsState()
    val searchResults by weatherViewModel.searchResults.collectAsState()
    val isSearching by weatherViewModel.isSearching.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val isGranted = fineLocationGranted || coarseLocationGranted
        weatherViewModel.onPermissionResult(isGranted, context)
    }

    LaunchedEffect(Unit) {
        if (LocationHelper.hasLocationPermission(context)) {
            weatherViewModel.initializeWeather(context)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            weatherViewModel.initializeWeather(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.weather_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                        Text(
                            text = stringResource(R.string.weather_subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriTextSecondary
                        )
                    }
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
                actions = {
                    IconButton(onClick = { weatherViewModel.refreshWeather() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.weather_refresh),
                            tint = AgriPrimaryDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AgriSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AgriBackground)
                .padding(innerPadding)
        ) {
            // Location Bar with Search Trigger
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, AgriBorder, RoundedCornerShape(12.dp))
                    .clickable { showLocationDialog = true },
                color = AgriSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AgriPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentLocation.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextPrimary,
                            maxLines = 1
                        )
                    }

                    Surface(
                        color = AgriPrimaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditLocationAlt,
                                contentDescription = null,
                                tint = AgriPrimaryDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.weather_manual_location_btn),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriPrimaryDark
                            )
                        }
                    }
                }
            }

            // Location Permission Warning Banner
            if (!LocationHelper.hasLocationPermission(context)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp)),
                    color = Color(0xFFFFFBEB)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsOff,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.weather_location_permission_required),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = stringResource(R.string.weather_location_permission_msg),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB45309)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        TextButton(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.weather_grant_permission),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriPrimaryDark
                            )
                        }
                    }
                }
            }

            // Main Weather Content
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AgriPrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.msg_loading),
                                style = MaterialTheme.typography.bodyMedium,
                                color = AgriTextSecondary
                            )
                        }
                    }
                }

                is WeatherUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = AgriDanger,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.weather_unavailable_msg),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { weatherViewModel.refreshWeather() },
                                colors = ButtonDefaults.buttonColors(containerColor = AgriPrimary)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }

                is WeatherUiState.Success -> {
                    WeatherContent(
                        weather = state.weather,
                        selectedCrop = selectedCrop,
                        selectedStage = selectedStage,
                        onSelectCrop = { weatherViewModel.selectCrop(it) },
                        onSelectStage = { weatherViewModel.selectStage(it) }
                    )
                }

                is WeatherUiState.PermissionDenied -> {
                    LaunchedEffect(Unit) {
                        weatherViewModel.selectLocation(state.lastLocation ?: LocationHelper.DEFAULT_LOCATION)
                    }
                }
            }
        }
    }

    // Manual Location / City Search Dialog
    if (showLocationDialog) {
        Dialog(onDismissRequest = {
            showLocationDialog = false
            searchQuery = ""
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(20.dp)),
                color = AgriSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.weather_search_city_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                        IconButton(onClick = {
                            showLocationDialog = false
                            searchQuery = ""
                        }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cd_close), tint = AgriTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            weatherViewModel.searchCities(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.weather_search_city_placeholder)) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = AgriPrimary)
                        },
                        trailingIcon = {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = AgriPrimary
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AgriPrimary,
                            unfocusedBorderColor = AgriBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (searchResults.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.weather_search_results),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {
                            items(searchResults) { loc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            weatherViewModel.selectGeocodingLocation(loc)
                                            showLocationDialog = false
                                            searchQuery = ""
                                        }
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = AgriPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = loc.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AgriTextPrimary
                                    )
                                }
                                HorizontalDivider(color = AgriBorder.copy(alpha = 0.5f))
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.weather_popular_cities),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp)
                        ) {
                            items(LocationHelper.POPULAR_REGIONS) { region ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            weatherViewModel.selectLocation(region)
                                            showLocationDialog = false
                                            searchQuery = ""
                                        }
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Agriculture, contentDescription = null, tint = AgriPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = region.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AgriTextPrimary
                                    )
                                }
                                HorizontalDivider(color = AgriBorder.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherContent(
    weather: WeatherData,
    selectedCrop: FarmCrop,
    selectedStage: GrowthStage,
    onSelectCrop: (FarmCrop) -> Unit,
    onSelectStage: (GrowthStage) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // 0. Offline Banner (if applicable)
        if (weather.isOfflineCached) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp)),
                    color = Color(0xFFFFFBEB)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.weather_cached_banner, weather.lastUpdated),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 1. Current Weather Hero Card
        item {
            CurrentWeatherHero(weather)
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. Weather Metrics Grid (Humidity, Wind Speed, Cloud Cover, Wind Gusts)
        item {
            WeatherMetricsGrid(weather)
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 3. Active Weather Alerts Section (if any alert is present)
        if (weather.alerts.isNotEmpty()) {
            item {
                WeatherAlertsSection(weather.alerts)
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // 4. 24-Hour Hourly Forecast
        if (weather.hourlyForecast.isNotEmpty()) {
            item {
                HourlyForecastSection(weather.hourlyForecast)
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // 5. 7-Day Forecast Section
        if (weather.dailyForecast.isNotEmpty()) {
            item {
                DailyForecastSection(weather.dailyForecast)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // 6. Crop & Growth Stage Filter Selectors
        item {
            CropAndStageSelector(
                selectedCrop = selectedCrop,
                selectedStage = selectedStage,
                onSelectCrop = onSelectCrop,
                onSelectStage = onSelectStage
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 7. Farm-Specific Crop Advisory Cards
        item {
            CropWeatherAdvisorySection(weather.advisories)
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 8. Weather-Based Disease Risk Card
        item {
            DiseaseWeatherRiskSection(weather.diseaseRisk)
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 9. Transparency Attribution & Data Source Footer
        item {
            WeatherFooter(weather)
        }
    }
}

@Composable
fun CurrentWeatherHero(weather: WeatherData) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = AgriPrimaryDark,
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(AgriPrimaryDark, AgriPrimary)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.weather_current_conditions).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC8E6C9)
                        )
                        Text(
                            text = stringResource(WeatherCodeMapper.getWeatherConditionResId(weather.weatherCode)),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Icon(
                        imageVector = WeatherCodeMapper.getWeatherIcon(weather.weatherCode),
                        contentDescription = null,
                        tint = if (WeatherCodeMapper.isRainy(weather.weatherCode)) Color(0xFF90CAF9) else Color(0xFFFFD54F),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "${weather.temperature.toInt()}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "°C",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC8E6C9),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${stringResource(R.string.weather_feels_like)} ${weather.feelsLike.toInt()}°C",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE8F5E9)
                        )
                        if (weather.rainProbability > 0 || weather.rainAmount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = Color(0xFF90CAF9),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${weather.rainProbability}% (${weather.rainAmount} mm)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFE0F2FE),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherMetricsGrid(weather: WeatherData) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.weather_humidity),
                value = "${weather.humidity}%",
                icon = Icons.Default.Opacity,
                iconTint = Color(0xFF0284C7)
            )

            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.weather_wind_speed),
                value = "${weather.windSpeed.toInt()} km/h",
                icon = Icons.Default.Air,
                iconTint = Color(0xFF0D9488)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.weather_rain_prob),
                value = "${weather.rainProbability}% (${weather.rainAmount} mm)",
                icon = Icons.Default.WaterDrop,
                iconTint = Color(0xFF2563EB)
            )

            MetricCard(
                modifier = Modifier.weight(1f),
                title = "${stringResource(R.string.weather_sunrise)} / ${stringResource(R.string.weather_sunset)}",
                value = "${weather.sunrise} • ${weather.sunset}",
                icon = Icons.Default.WbTwilight,
                iconTint = Color(0xFFD97706)
            )
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, AgriBorder, RoundedCornerShape(14.dp)),
        color = AgriSurface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = AgriTextSecondary,
                    maxLines = 1
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun WeatherAlertsSection(alerts: List<WeatherAlert>) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = null,
                tint = AgriDanger,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.weather_alerts_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriDanger
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        alerts.forEach { alert ->
            val (bgColor, borderColor, iconColor, textColor) = when (alert.severity) {
                AdvisorySeverity.ALERT -> WeatherQuadruple(Color(0xFFFEF2F2), Color(0xFFFECACA), AgriDanger, Color(0xFF991B1B))
                AdvisorySeverity.WARNING -> WeatherQuadruple(Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFD97706), Color(0xFF92400E))
                AdvisorySeverity.SUCCESS -> WeatherQuadruple(Color(0xFFF0FDF4), Color(0xFFBBF7D0), AgriSuccess, Color(0xFF166534))
                AdvisorySeverity.INFO -> WeatherQuadruple(Color(0xFFF0F9FF), Color(0xFFBAE6FD), Color(0xFF0284C7), Color(0xFF075985))
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
                color = bgColor
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = alert.icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(alert.titleResId),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(alert.messageResId),
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HourlyForecastSection(hourlyList: List<HourlyForecastItem>) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = AgriPrimaryDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.weather_hourly_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriPrimaryDark
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
        ) {
            items(hourlyList) { item ->
                Surface(
                    modifier = Modifier
                        .width(76.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, AgriBorder, RoundedCornerShape(14.dp)),
                    color = AgriSurface,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.timeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Icon(
                            imageVector = WeatherCodeMapper.getWeatherIcon(item.weatherCode),
                            contentDescription = null,
                            tint = if (WeatherCodeMapper.isRainy(item.weatherCode)) Color(0xFF2563EB) else Color(0xFFD97706),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${item.temperature.toInt()}°",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (item.rainProbability > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "${item.rainProbability}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF2563EB),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = "-",
                                style = MaterialTheme.typography.labelSmall,
                                color = AgriTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyForecastSection(forecastList: List<DailyForecastItem>) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = AgriPrimaryDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.weather_forecast_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriPrimaryDark
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, AgriBorder, RoundedCornerShape(16.dp)),
            color = AgriSurface,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                forecastList.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Day Label
                        Column(modifier = Modifier.weight(1.3f)) {
                            Text(
                                text = item.dayName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (index == 0) AgriPrimaryDark else AgriTextPrimary
                            )
                            if (item.precipitationSum > 0.0) {
                                Text(
                                    text = "${item.precipitationSum} ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF0284C7),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Condition & Rain chance
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1.1f),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = WeatherCodeMapper.getWeatherIcon(item.weatherCode),
                                contentDescription = null,
                                tint = if (WeatherCodeMapper.isRainy(item.weatherCode)) Color(0xFF2563EB) else Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                            if (item.rainProbability > 15) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${item.rainProbability}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2563EB)
                                )
                            }
                        }

                        // Temp Range
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "${item.maxTemp.toInt()}°",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${item.minTemp.toInt()}°",
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary
                            )
                        }
                    }

                    if (index < forecastList.size - 1) {
                        HorizontalDivider(color = AgriBorder.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun CropAndStageSelector(
    selectedCrop: FarmCrop,
    selectedStage: GrowthStage,
    onSelectCrop: (FarmCrop) -> Unit,
    onSelectStage: (GrowthStage) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = null,
                tint = AgriPrimaryDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.weather_select_crop),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriPrimaryDark
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Crop Chips Row
        Text(
            text = stringResource(R.string.weather_select_crop),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = AgriTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FarmCrop.entries.forEach { crop ->
                val isSelected = crop == selectedCrop
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCrop(crop) },
                    label = {
                        Text(
                            text = stringResource(crop.labelResId),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AgriPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = AgriSurface,
                        labelColor = AgriTextPrimary
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stage Chips Row
        Text(
            text = stringResource(R.string.weather_select_stage),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = AgriTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GrowthStage.entries.forEach { stage ->
                val isSelected = stage == selectedStage
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectStage(stage) },
                    label = {
                        Text(
                            text = stringResource(stage.labelResId),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AgriSecondary,
                        selectedLabelColor = Color.White,
                        containerColor = AgriSurface,
                        labelColor = AgriTextPrimary
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
fun CropWeatherAdvisorySection(advisories: List<CropAdvisoryItem>) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = AgriPrimaryDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.weather_advisory_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriPrimaryDark
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        advisories.forEach { advisory ->
            val (bgColor, borderColor, iconColor, titleColor, textColor) = when (advisory.severity) {
                AdvisorySeverity.ALERT -> WeatherQuintuple(
                    Color(0xFFFEF2F2),
                    Color(0xFFFECACA),
                    AgriDanger,
                    Color(0xFF991B1B),
                    Color(0xFF7F1D1D)
                )
                AdvisorySeverity.WARNING -> WeatherQuintuple(
                    Color(0xFFFFFBEB),
                    Color(0xFFFDE68A),
                    Color(0xFFD97706),
                    Color(0xFF92400E),
                    Color(0xFF78350F)
                )
                AdvisorySeverity.SUCCESS -> WeatherQuintuple(
                    Color(0xFFF0FDF4),
                    Color(0xFFBBF7D0),
                    AgriSuccess,
                    Color(0xFF166534),
                    Color(0xFF14532D)
                )
                AdvisorySeverity.INFO -> WeatherQuintuple(
                    Color(0xFFF0F9FF),
                    Color(0xFFBAE6FD),
                    Color(0xFF0284C7),
                    Color(0xFF075985),
                    Color(0xFF0C4A6E)
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
                color = bgColor
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = advisory.icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(advisory.titleResId),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(advisory.messageResId),
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiseaseWeatherRiskSection(diseaseRisk: DiseaseWeatherRisk) {
    val (badgeBg, badgeText, borderColor) = when (diseaseRisk.riskLevel) {
        DiseaseRiskLevel.NO_RISK -> Triple(Color(0xFFDCFCE7), AgriSuccess, Color(0xFFBBF7D0))
        DiseaseRiskLevel.HIGH -> Triple(Color(0xFFFEE2E2), AgriDanger, Color(0xFFFECACA))
        DiseaseRiskLevel.MEDIUM -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), Color(0xFFFDE68A))
        DiseaseRiskLevel.LOW -> Triple(Color(0xFFDCFCE7), AgriSuccess, Color(0xFFBBF7D0))
        DiseaseRiskLevel.UNKNOWN -> Triple(Color(0xFFF3F4F6), Color(0xFF6B7280), Color(0xFFE5E7EB))
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Coronavirus,
                contentDescription = null,
                tint = AgriPrimaryDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.weather_disease_risk_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriPrimaryDark
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
            color = AgriSurface,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(diseaseRisk.titleResId),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )

                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = diseaseRisk.riskLevel.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = badgeText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(diseaseRisk.reasonResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = AgriBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AgriTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.weather_disease_risk_disclaimer),
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherFooter(weather: WeatherData) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = AgriSurface.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${stringResource(R.string.weather_data_source)} • ${stringResource(R.string.weather_last_updated, weather.lastUpdated)}",
                style = MaterialTheme.typography.labelSmall,
                color = AgriTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.weather_forecast_note),
                style = MaterialTheme.typography.labelSmall,
                color = AgriTextSecondary.copy(alpha = 0.8f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private data class WeatherQuadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
private data class WeatherQuintuple<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
