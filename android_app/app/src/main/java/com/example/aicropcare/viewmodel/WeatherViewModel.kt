package com.example.aicropcare.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicropcare.network.FarmCrop
import com.example.aicropcare.network.GeocodingLocation
import com.example.aicropcare.network.GrowthStage
import com.example.aicropcare.network.WeatherData
import com.example.aicropcare.repository.WeatherRepository
import com.example.aicropcare.utils.LocationHelper
import com.example.aicropcare.utils.UserLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val weather: WeatherData) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
    data class PermissionDenied(val lastLocation: UserLocation? = null) : WeatherUiState
}

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _currentLocation = MutableStateFlow<UserLocation>(LocationHelper.DEFAULT_LOCATION)
    val currentLocation: StateFlow<UserLocation> = _currentLocation.asStateFlow()

    private val _selectedCrop = MutableStateFlow<FarmCrop>(FarmCrop.RICE)
    val selectedCrop: StateFlow<FarmCrop> = _selectedCrop.asStateFlow()

    private val _selectedStage = MutableStateFlow<GrowthStage>(GrowthStage.VEGETATIVE)
    val selectedStage: StateFlow<GrowthStage> = _selectedStage.asStateFlow()

    private val _searchResults = MutableStateFlow<List<GeocodingLocation>>(emptyList())
    val searchResults: StateFlow<List<GeocodingLocation>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null

    fun initializeWeather(context: Context) {
        if (LocationHelper.hasLocationPermission(context)) {
            loadWeatherFromGps(context)
        } else {
            // Load default agricultural hub weather
            loadWeather(LocationHelper.DEFAULT_LOCATION)
        }
    }

    fun onPermissionResult(isGranted: Boolean, context: Context) {
        if (isGranted) {
            loadWeatherFromGps(context)
        } else {
            // Keep default/selected location weather loaded
            _uiState.value = WeatherUiState.PermissionDenied(_currentLocation.value)
        }
    }

    fun loadWeatherFromGps(context: Context) {
        _uiState.value = WeatherUiState.Loading
        viewModelScope.launch {
            val userLoc = LocationHelper.getLastKnownLocation(context)
            val targetLocation = userLoc ?: _currentLocation.value
            _currentLocation.value = targetLocation
            loadWeather(targetLocation)
        }
    }

    fun selectCrop(crop: FarmCrop) {
        _selectedCrop.value = crop
        loadWeather(_currentLocation.value)
    }

    fun selectStage(stage: GrowthStage) {
        _selectedStage.value = stage
        loadWeather(_currentLocation.value)
    }

    fun selectLocation(location: UserLocation) {
        _currentLocation.value = location
        _searchResults.value = emptyList()
        loadWeather(location)
    }

    fun selectGeocodingLocation(location: GeocodingLocation) {
        val userLocation = UserLocation(
            latitude = location.latitude,
            longitude = location.longitude,
            name = location.displayName
        )
        selectLocation(userLocation)
    }

    fun searchCities(query: String) {
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(300) // Debounce
            val result = repository.searchCities(query)
            result.onSuccess { list ->
                _searchResults.value = list
            }.onFailure {
                _searchResults.value = emptyList()
            }
            _isSearching.value = false
        }
    }

    fun refreshWeather() {
        loadWeather(_currentLocation.value)
    }

    private fun loadWeather(location: UserLocation) {
        _uiState.value = WeatherUiState.Loading
        viewModelScope.launch {
            val result = repository.getWeatherData(
                latitude = location.latitude,
                longitude = location.longitude,
                locationName = location.name,
                selectedCrop = _selectedCrop.value,
                selectedStage = _selectedStage.value
            )

            result.onSuccess { data ->
                _uiState.value = WeatherUiState.Success(data)
            }.onFailure { error ->
                _uiState.value = WeatherUiState.Error(
                    error.localizedMessage ?: "Weather information is temporarily unavailable."
                )
            }
        }
    }
}

