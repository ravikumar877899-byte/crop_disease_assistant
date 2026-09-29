package com.example.aicropcare.network

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.aicropcare.R
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Open-Meteo Weather API Response DTO
 */
data class OpenMeteoResponse(
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName("timezone") val timezone: String? = null,
    @SerializedName("current") val current: OpenMeteoCurrent? = null,
    @SerializedName("hourly") val hourly: OpenMeteoHourly? = null,
    @SerializedName("daily") val daily: OpenMeteoDaily? = null
) : Serializable

data class OpenMeteoCurrent(
    @SerializedName("time") val time: String? = null,
    @SerializedName("temperature_2m") val temperature: Double = 0.0,
    @SerializedName("relative_humidity_2m") val humidity: Int = 0,
    @SerializedName("apparent_temperature") val apparentTemperature: Double = 0.0,
    @SerializedName("precipitation") val precipitation: Double = 0.0,
    @SerializedName("rain") val rain: Double = 0.0,
    @SerializedName("showers") val showers: Double = 0.0,
    @SerializedName("weather_code") val weatherCode: Int = 0,
    @SerializedName("cloud_cover") val cloudCover: Int = 0,
    @SerializedName("wind_speed_10m") val windSpeed: Double = 0.0,
    @SerializedName("wind_direction_10m") val windDirection: Int = 0,
    @SerializedName("wind_gusts_10m") val windGusts: Double = 0.0
) : Serializable

data class OpenMeteoHourly(
    @SerializedName("time") val time: List<String> = emptyList(),
    @SerializedName("temperature_2m") val temperature: List<Double> = emptyList(),
    @SerializedName("relative_humidity_2m") val humidity: List<Int> = emptyList(),
    @SerializedName("precipitation_probability") val precipProb: List<Int> = emptyList(),
    @SerializedName("precipitation") val precipitation: List<Double> = emptyList(),
    @SerializedName("rain") val rain: List<Double> = emptyList(),
    @SerializedName("showers") val showers: List<Double> = emptyList(),
    @SerializedName("weather_code") val weatherCode: List<Int> = emptyList(),
    @SerializedName("wind_speed_10m") val windSpeed: List<Double> = emptyList(),
    @SerializedName("cloud_cover") val cloudCover: List<Int> = emptyList()
) : Serializable

data class OpenMeteoDaily(
    @SerializedName("time") val time: List<String> = emptyList(),
    @SerializedName("weather_code") val weatherCode: List<Int> = emptyList(),
    @SerializedName("temperature_2m_max") val tempMax: List<Double> = emptyList(),
    @SerializedName("temperature_2m_min") val tempMin: List<Double> = emptyList(),
    @SerializedName("precipitation_sum") val precipSum: List<Double>? = null,
    @SerializedName("rain_sum") val rainSum: List<Double>? = null,
    @SerializedName("showers_sum") val showersSum: List<Double>? = null,
    @SerializedName("precipitation_probability_max") val precipProbMax: List<Int>? = null,
    @SerializedName("wind_speed_10m_max") val windSpeedMax: List<Double>? = null,
    @SerializedName("wind_gusts_10m_max") val windGustsMax: List<Double>? = null,
    @SerializedName("sunrise") val sunrise: List<String>? = null,
    @SerializedName("sunset") val sunset: List<String>? = null
) : Serializable

/**
 * Open-Meteo Geocoding Search Response
 */
data class GeocodingResponse(
    @SerializedName("results") val results: List<GeocodingLocation>? = null
) : Serializable

data class GeocodingLocation(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName("country") val country: String? = null,
    @SerializedName("admin1") val admin1: String? = null
) : Serializable {
    val displayName: String
        get() = if (!admin1.isNullOrBlank()) "$name, $admin1" else if (!country.isNullOrBlank()) "$name, $country" else name
}

/**
 * 10 Key Agricultural Crops Supported
 */
enum class FarmCrop(val labelResId: Int) {
    RICE(R.string.crop_rice),
    TOMATO(R.string.crop_tomato),
    COTTON(R.string.crop_cotton),
    MAIZE(R.string.crop_maize),
    BANANA(R.string.crop_banana),
    CHILLI(R.string.crop_chilli),
    GROUNDNUT(R.string.crop_groundnut),
    SUGARCANE(R.string.crop_sugarcane),
    POTATO(R.string.crop_potato),
    ONION(R.string.crop_onion)
}

/**
 * 6 Crop Growth Stages
 */
enum class GrowthStage(val labelResId: Int) {
    SEEDLING(R.string.stage_seedling),
    VEGETATIVE(R.string.stage_vegetative),
    FLOWERING(R.string.stage_flowering),
    FRUITING(R.string.stage_fruiting),
    MATURITY(R.string.stage_maturity),
    HARVEST(R.string.stage_harvest)
}

enum class AdvisorySeverity {
    INFO,
    SUCCESS,
    WARNING,
    ALERT
}

enum class AlertType {
    HEAVY_RAIN,
    HIGH_WIND,
    EXTREME_HEAT,
    COLD_FROST,
    HIGH_HUMIDITY
}

data class WeatherAlert(
    val type: AlertType,
    val titleResId: Int,
    val messageResId: Int,
    val severity: AdvisorySeverity,
    val icon: ImageVector,
    val source: String = "Open-Meteo"
) : Serializable

data class DiseaseWeatherRisk(
    val riskLevel: DiseaseRiskLevel,
    val titleResId: Int,
    val reasonResId: Int,
    val humidity: Int,
    val temp: Int
) : Serializable

data class HourlyForecastItem(
    val rawTime: String,
    val timeLabel: String,
    val temperature: Double,
    val rainProbability: Int,
    val rainAmount: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int
) : Serializable

data class DailyForecastItem(
    val date: String,
    val dayName: String,
    val minTemp: Double,
    val maxTemp: Double,
    val weatherCode: Int,
    val rainProbability: Int,
    val precipitationSum: Double,
    val maxWindSpeed: Double
) : Serializable

data class CropAdvisoryItem(
    val titleResId: Int,
    val messageResId: Int,
    val icon: ImageVector,
    val severity: AdvisorySeverity
) : Serializable

/**
 * Domain Models for AI Crop Care Weather UI
 */
data class WeatherData(
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windGusts: Double,
    val cloudCover: Int,
    val rainAmount: Double,
    val weatherCode: Int,
    val rainProbability: Int,
    val sunrise: String,
    val sunset: String,
    val advisories: List<CropAdvisoryItem>,
    val alerts: List<WeatherAlert>,
    val diseaseRisk: DiseaseWeatherRisk,
    val hourlyForecast: List<HourlyForecastItem>,
    val dailyForecast: List<DailyForecastItem>,
    val dataSource: String = "Open-Meteo",
    val lastUpdated: String = "Just now",
    val isOfflineCached: Boolean = false
) : Serializable

/**
 * Helper to map WMO weather code to string resource and icon
 */
object WeatherCodeMapper {
    fun getWeatherConditionResId(code: Int): Int {
        return when (code) {
            0 -> R.string.weather_cond_clear
            1, 2 -> R.string.weather_cond_partly_cloudy
            3 -> R.string.weather_cond_cloudy
            45, 48 -> R.string.weather_cond_fog
            51, 53, 55, 56, 57 -> R.string.weather_cond_drizzle
            61, 63, 65, 66, 67, 80, 81, 82 -> R.string.weather_cond_rain
            71, 73, 75, 77, 85, 86 -> R.string.weather_cond_snow
            95, 96, 99 -> R.string.weather_cond_thunderstorm
            else -> R.string.weather_cond_partly_cloudy
        }
    }

    fun getWeatherIcon(code: Int): ImageVector {
        return when (code) {
            0 -> Icons.Default.WbSunny
            1, 2 -> Icons.Default.WbCloudy
            3 -> Icons.Default.Cloud
            45, 48 -> Icons.Default.BlurOn
            51, 53, 55, 56, 57 -> Icons.Default.Grain
            61, 63, 65, 66, 67, 80, 81, 82 -> Icons.Default.WaterDrop
            71, 73, 75, 77, 85, 86 -> Icons.Default.AcUnit
            95, 96, 99 -> Icons.Default.Thunderstorm
            else -> Icons.Default.WbSunny
        }
    }

    fun isRainy(code: Int): Boolean {
        return code in listOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99)
    }
}
