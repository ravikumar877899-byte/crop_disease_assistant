package com.example.aicropcare.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicropcare.network.WeatherData
import com.example.aicropcare.repository.WeatherRepository
import com.example.aicropcare.utils.CropRecommendationHelper
import com.example.aicropcare.utils.LocationHelper
import com.example.aicropcare.utils.RecommendedCrop
import com.example.aicropcare.utils.UserLocation
import com.example.aicropcare.utils.SearchedCropResult
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

sealed interface CropRecommendationUiState {
    data object Idle : CropRecommendationUiState
    data object Loading : CropRecommendationUiState
    data class Success(
        val weather: WeatherData,
        val crops: List<RecommendedCrop>,
        val searchResult: SearchedCropResult? = null,
        val searchError: String? = null
    ) : CropRecommendationUiState
    data class Error(val message: String) : CropRecommendationUiState
    data object PermissionDenied : CropRecommendationUiState
}

class CropRecommendationViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<CropRecommendationUiState>(CropRecommendationUiState.Idle)
    val uiState: StateFlow<CropRecommendationUiState> = _uiState.asStateFlow()

    fun getRecommendations(context: Context) {
        viewModelScope.launch {
            _uiState.value = CropRecommendationUiState.Loading
            
            if (!LocationHelper.hasLocationPermission(context)) {
                _uiState.value = CropRecommendationUiState.PermissionDenied
                return@launch
            }

            if (!LocationHelper.isLocationEnabled(context)) {
                _uiState.value = CropRecommendationUiState.Error("location_disabled")
                return@launch
            }

            val location = LocationHelper.getBestLocation(context)
            if (location == null) {
                _uiState.value = CropRecommendationUiState.Error("location_not_found")
                return@launch
            }

            try {
                // Fetch weather using the existing repository
                val result = repository.getWeatherData(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    locationName = location.name
                )
                
                result.fold(
                    onSuccess = { weather ->
                        // Determine recommended crops based on weather data
                        val crops = CropRecommendationHelper.getRecommendations(weather)
                        _uiState.value = CropRecommendationUiState.Success(weather, crops)
                    },
                    onFailure = { error ->
                        Log.e("CropRecommendation", "Weather API failed: ${error.message}")
                        _uiState.value = CropRecommendationUiState.Error("weather_data_unavailable")
                    }
                )
            } catch (e: Exception) {
                Log.e("CropRecommendation", "Exception fetching weather: ${e.message}")
                _uiState.value = CropRecommendationUiState.Error("weather_data_unavailable")
            }
        }
    }

    fun analyzeSearchedCrop(cropName: String) {
        val currentState = _uiState.value
        if (currentState is CropRecommendationUiState.Success) {
            val weather = currentState.weather
            val result = CropRecommendationHelper.analyzeSearchedCrop(cropName, weather)
            
            _uiState.value = currentState.copy(
                searchResult = result,
                searchError = if (result == null) "crop_not_available_analysis" else null
            )
        }
    }
    
    fun clearSearchResult() {
        val currentState = _uiState.value
        if (currentState is CropRecommendationUiState.Success) {
            _uiState.value = currentState.copy(searchResult = null, searchError = null)
        }
    }

}
