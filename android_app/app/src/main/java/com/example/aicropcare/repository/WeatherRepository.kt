package com.example.aicropcare.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import com.example.aicropcare.R
import com.example.aicropcare.network.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WeatherRepository(
    private val context: Context? = null,
    private val weatherApiService: WeatherApiService = WeatherRetrofitClient.weatherService,
    private val geocodingApiService: GeocodingApiService = WeatherRetrofitClient.geocodingService
) {
    private val gson = Gson()
    private val prefs: SharedPreferences? by lazy {
        context?.getSharedPreferences("ai_crop_care_weather_cache", Context.MODE_PRIVATE)
    }

    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        locationName: String,
        selectedCrop: FarmCrop = FarmCrop.RICE,
        selectedStage: GrowthStage = GrowthStage.VEGETATIVE
    ): Result<WeatherData> = withContext(Dispatchers.IO) {
        try {
            val response = weatherApiService.getForecast(latitude, longitude)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val current = body.current
                val hourly = body.hourly
                val daily = body.daily

                val temperature = current?.temperature ?: 0.0
                val feelsLike = current?.apparentTemperature ?: temperature
                val humidity = current?.humidity ?: 50
                val windSpeed = current?.windSpeed ?: 0.0
                val windGusts = current?.windGusts ?: (windSpeed * 1.3)
                val cloudCover = current?.cloudCover ?: 0
                val rainAmount = current?.rain ?: current?.precipitation ?: 0.0
                val weatherCode = current?.weatherCode ?: 0

                val apiTimezone = body.timezone ?: TimeZone.getDefault().id
                val sdfIso = SimpleDateFormat("yyyy-MM-dd'T'HH", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone(apiTimezone)
                }
                val currentIsoHour = sdfIso.format(Date())
                val currentHourIndex = hourly?.time?.indexOfFirst { it.startsWith(currentIsoHour) }?.takeIf { it >= 0 } ?: 0
                val rainProbability = hourly?.precipProb?.getOrNull(currentHourIndex) ?: 0

                val sunrise = formatTime(daily?.sunrise?.firstOrNull()) ?: "06:00 AM"
                val sunset = formatTime(daily?.sunset?.firstOrNull()) ?: "06:30 PM"

                // 1. Process 24-Hour Hourly Forecast
                val hourlyList = extractHourlyForecast(hourly, apiTimezone)

                // 2. Process 7-Day Daily Forecast
                val dailyList = extractDailyForecast(daily, temperature)

                // 3. Process Active Data-Backed Weather Alerts
                val alerts = generateWeatherAlerts(
                    currentTemp = temperature,
                    currentHumidity = humidity,
                    currentWind = windSpeed,
                    currentGusts = windGusts,
                    currentRain = rainAmount,
                    hourlyForecast = hourlyList
                )

                // 4. Generate Farm & Crop-Specific Advisory
                val advisories = generateCropAdvisories(
                    crop = selectedCrop,
                    stage = selectedStage,
                    weatherCode = weatherCode,
                    rainProbability = rainProbability,
                    humidity = humidity,
                    temperature = temperature,
                    windSpeed = windSpeed
                )

                // 5. Generate Weather-Based Disease Risk
                val diseaseRisk = generateDiseaseWeatherRisk(
                    humidity = humidity,
                    temperature = temperature,
                    rainProbability = rainProbability,
                    weatherCode = weatherCode
                )

                val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone(apiTimezone)
                }
                val currentTimeFormatted = sdfTime.format(Date())

                val weatherData = WeatherData(
                    locationName = locationName,
                    latitude = latitude,
                    longitude = longitude,
                    temperature = temperature,
                    feelsLike = feelsLike,
                    humidity = humidity,
                    windSpeed = windSpeed,
                    windGusts = windGusts,
                    cloudCover = cloudCover,
                    rainAmount = rainAmount,
                    weatherCode = weatherCode,
                    rainProbability = rainProbability,
                    sunrise = sunrise,
                    sunset = sunset,
                    advisories = advisories,
                    alerts = alerts,
                    diseaseRisk = diseaseRisk,
                    hourlyForecast = hourlyList,
                    dailyForecast = dailyList,
                    dataSource = "Open-Meteo",
                    lastUpdated = currentTimeFormatted,
                    isOfflineCached = false
                )

                // Cache successful weather response
                cacheWeatherData(locationName, weatherData)

                Result.success(weatherData)
            } else {
                // Try to load cached data on HTTP error
                loadCachedWeatherData(locationName)?.let { cached ->
                    return@withContext Result.success(cached)
                }
                Result.failure(Exception("Weather data temporarily unavailable (HTTP ${response.code()}). Please try again."))
            }
        } catch (e: Exception) {
            // Try to load cached data on network exception
            loadCachedWeatherData(locationName)?.let { cached ->
                return@withContext Result.success(cached)
            }
            Result.failure(e)
        }
    }

    suspend fun searchCities(query: String): Result<List<GeocodingLocation>> = withContext(Dispatchers.IO) {
        try {
            val response = geocodingApiService.searchCity(name = query.trim())
            if (response.isSuccessful && response.body() != null) {
                val results = response.body()?.results ?: emptyList()
                Result.success(results)
            } else {
                Result.failure(Exception("Geocoding service unavailable (HTTP ${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractHourlyForecast(hourly: OpenMeteoHourly?, apiTimezone: String): List<HourlyForecastItem> {
        if (hourly == null || hourly.time.isEmpty()) return emptyList()

        val list = mutableListOf<HourlyForecastItem>()
        val sdfIso = SimpleDateFormat("yyyy-MM-dd'T'HH", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone(apiTimezone)
        }
        val currentIsoHour = sdfIso.format(Date())

        // Find index corresponding to the current hour or default to 0
        var startIndex = hourly.time.indexOfFirst { it.startsWith(currentIsoHour) }
        if (startIndex == -1) startIndex = 0

        val count = minOf(24, hourly.time.size - startIndex)
        for (i in 0 until count) {
            val idx = startIndex + i
            val rawTime = hourly.time.getOrElse(idx) { "" }
            val temp = hourly.temperature.getOrElse(idx) { 0.0 }
            val hum = hourly.humidity.getOrElse(idx) { 50 }
            val prob = hourly.precipProb.getOrElse(idx) { 0 }
            val rain = hourly.rain.getOrElse(idx) { hourly.precipitation.getOrElse(idx) { 0.0 } }
            val wind = hourly.windSpeed.getOrElse(idx) { 0.0 }
            val code = hourly.weatherCode.getOrElse(idx) { 0 }

            val label = if (i == 0) "Now" else formatHourLabel(rawTime)

            list.add(
                HourlyForecastItem(
                    rawTime = rawTime,
                    timeLabel = label,
                    temperature = temp,
                    rainProbability = prob,
                    rainAmount = rain,
                    humidity = hum,
                    windSpeed = wind,
                    weatherCode = code
                )
            )
        }
        return list
    }

    private fun extractDailyForecast(daily: OpenMeteoDaily?, currentTemp: Double): List<DailyForecastItem> {
        if (daily == null) return emptyList()

        val forecastList = mutableListOf<DailyForecastItem>()
        val times = daily.time
        val codes = daily.weatherCode
        val maxTemps = daily.tempMax
        val minTemps = daily.tempMin
        val rainProbs = daily.precipProbMax ?: emptyList()
        val rainSums = daily.precipSum ?: emptyList()
        val windMaxs = daily.windSpeedMax ?: emptyList()

        for (i in times.indices) {
            val dateStr = times[i]
            val code = codes.getOrElse(i) { 0 }
            val maxT = maxTemps.getOrElse(i) { currentTemp }
            val minT = minTemps.getOrElse(i) { currentTemp }
            val rProb = rainProbs.getOrElse(i) { if (WeatherCodeMapper.isRainy(code)) 60 else 10 }
            val rSum = rainSums.getOrElse(i) { 0.0 }
            val wMax = windMaxs.getOrElse(i) { 10.0 }
            val dayName = getDayName(dateStr, i)

            forecastList.add(
                DailyForecastItem(
                    date = dateStr,
                    dayName = dayName,
                    minTemp = minT,
                    maxTemp = maxT,
                    weatherCode = code,
                    rainProbability = rProb,
                    precipitationSum = rSum,
                    maxWindSpeed = wMax
                )
            )
        }
        return forecastList
    }

    private fun generateWeatherAlerts(
        currentTemp: Double,
        currentHumidity: Int,
        currentWind: Double,
        currentGusts: Double,
        currentRain: Double,
        hourlyForecast: List<HourlyForecastItem>
    ): List<WeatherAlert> {
        val alerts = mutableListOf<WeatherAlert>()

        // 1. Heavy Rain Alert
        // Check actual forecast precipitation amount and timing
        val hasHeavyRainForecast = hourlyForecast.take(12).any { it.rainAmount >= 5.0 }
        if (currentRain >= 5.0 || hasHeavyRainForecast) {
            alerts.add(
                WeatherAlert(
                    type = AlertType.HEAVY_RAIN,
                    titleResId = R.string.alert_heavy_rain_title,
                    messageResId = R.string.alert_heavy_rain_desc,
                    severity = AdvisorySeverity.ALERT,
                    icon = Icons.Default.Thunderstorm
                )
            )
        }

        // 2. High Wind Warning
        if (currentWind >= 30.0 || currentGusts >= 45.0) {
            alerts.add(
                WeatherAlert(
                    type = AlertType.HIGH_WIND,
                    titleResId = R.string.alert_high_wind_title,
                    messageResId = R.string.alert_high_wind_desc,
                    severity = AdvisorySeverity.WARNING,
                    icon = Icons.Default.Air
                )
            )
        }

        // 3. Extreme Heat Warning
        if (currentTemp >= 37.0) {
            alerts.add(
                WeatherAlert(
                    type = AlertType.EXTREME_HEAT,
                    titleResId = R.string.alert_heatwave_title,
                    messageResId = R.string.alert_heatwave_desc,
                    severity = AdvisorySeverity.WARNING,
                    icon = Icons.Default.WbSunny
                )
            )
        }

        // 4. Cold / Frost Alert
        if (currentTemp <= 10.0) {
            alerts.add(
                WeatherAlert(
                    type = AlertType.COLD_FROST,
                    titleResId = R.string.alert_cold_title,
                    messageResId = R.string.alert_cold_desc,
                    severity = AdvisorySeverity.WARNING,
                    icon = Icons.Default.AcUnit
                )
            )
        }

        // 5. Very High Humidity Alert
        if (currentHumidity >= 85) {
            alerts.add(
                WeatherAlert(
                    type = AlertType.HIGH_HUMIDITY,
                    titleResId = R.string.alert_high_humidity_title,
                    messageResId = R.string.alert_high_humidity_desc,
                    severity = AdvisorySeverity.WARNING,
                    icon = Icons.Default.Opacity
                )
            )
        }

        return alerts
    }

    private fun generateCropAdvisories(
        crop: FarmCrop,
        stage: GrowthStage,
        weatherCode: Int,
        rainProbability: Int,
        humidity: Int,
        temperature: Double,
        windSpeed: Double
    ): List<CropAdvisoryItem> {
        val list = mutableListOf<CropAdvisoryItem>()

        val isRainExpected = WeatherCodeMapper.isRainy(weatherCode) || rainProbability >= 40
        val isWindHigh = windSpeed >= 18.0
        val isHeatHigh = temperature >= 34.0

        // 1. Irrigation Advice
        val (irrTitle, irrMsg, irrIcon, irrSev) = when {
            isRainExpected -> Quadruple(
                R.string.advisory_irrigation_title,
                R.string.advisory_irrigation_rain,
                Icons.Default.WaterDrop,
                AdvisorySeverity.WARNING
            )
            isHeatHigh -> Quadruple(
                R.string.advisory_irrigation_title,
                R.string.advisory_irrigation_heat,
                Icons.Default.WbSunny,
                AdvisorySeverity.WARNING
            )
            else -> Quadruple(
                R.string.advisory_irrigation_title,
                R.string.advisory_irrigation_normal,
                Icons.Default.Water,
                AdvisorySeverity.INFO
            )
        }
        list.add(CropAdvisoryItem(irrTitle, irrMsg, irrIcon, irrSev))

        // 2. Spraying & Chemical Protection
        val (sprayTitle, sprayMsg, sprayIcon, spraySev) = when {
            isRainExpected -> Quadruple(
                R.string.advisory_spraying_title,
                R.string.advisory_spraying_rain,
                Icons.Default.DoNotDisturbOn,
                AdvisorySeverity.ALERT
            )
            isWindHigh -> Quadruple(
                R.string.advisory_spraying_title,
                R.string.advisory_spraying_wind,
                Icons.Default.Air,
                AdvisorySeverity.WARNING
            )
            else -> Quadruple(
                R.string.advisory_spraying_title,
                R.string.advisory_spraying_optimal,
                Icons.Default.CheckCircle,
                AdvisorySeverity.SUCCESS
            )
        }
        list.add(CropAdvisoryItem(sprayTitle, sprayMsg, sprayIcon, spraySev))

        // 3. Growth Stage Specific Precaution
        val stageMsg = when (stage) {
            GrowthStage.SEEDLING -> R.string.advisory_seedling_care
            GrowthStage.VEGETATIVE -> R.string.advisory_vegetative_care
            GrowthStage.FLOWERING -> R.string.advisory_flowering_care
            GrowthStage.FRUITING -> R.string.advisory_fruiting_care
            GrowthStage.MATURITY -> R.string.advisory_harvest_care
            GrowthStage.HARVEST -> R.string.advisory_harvest_care
        }
        list.add(
            CropAdvisoryItem(
                titleResId = R.string.advisory_stage_care_title,
                messageResId = stageMsg,
                icon = Icons.Default.Grass,
                severity = if (stage == GrowthStage.FLOWERING && (isRainExpected || isHeatHigh)) AdvisorySeverity.WARNING else AdvisorySeverity.INFO
            )
        )

        return list
    }

    private fun generateDiseaseWeatherRisk(
        humidity: Int,
        temperature: Double,
        rainProbability: Int,
        weatherCode: Int
    ): DiseaseWeatherRisk {
        val isRainy = WeatherCodeMapper.isRainy(weatherCode) || rainProbability >= 50
        val isWarmTemp = temperature in 20.0..32.0

        val (riskLevel, reasonResId) = when {
            humidity >= 80 && (isRainy || isWarmTemp) -> Pair(DiseaseRiskLevel.HIGH, R.string.disease_risk_high_reason)
            humidity >= 65 || isRainy || (humidity >= 55 && isWarmTemp) -> Pair(DiseaseRiskLevel.MEDIUM, R.string.disease_risk_medium_reason)
            else -> Pair(DiseaseRiskLevel.LOW, R.string.disease_risk_low_reason)
        }

        val titleResId = when (riskLevel) {
            DiseaseRiskLevel.HIGH -> R.string.risk_high
            DiseaseRiskLevel.MEDIUM -> R.string.risk_medium
            DiseaseRiskLevel.LOW -> R.string.risk_low
            else -> R.string.risk_unknown
        }

        return DiseaseWeatherRisk(
            riskLevel = riskLevel,
            titleResId = titleResId,
            reasonResId = reasonResId,
            humidity = humidity,
            temp = temperature.toInt()
        )
    }

    private fun cacheWeatherData(locationName: String, weatherData: WeatherData) {
        try {
            val json = gson.toJson(weatherData)
            prefs?.edit()?.apply {
                putString("cached_weather_json", json)
                putString("cached_location_name", locationName)
                putLong("cached_timestamp", System.currentTimeMillis())
                apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCachedWeatherData(requestedLocation: String): WeatherData? {
        return try {
            val json = prefs?.getString("cached_weather_json", null) ?: return null
            val timestamp = prefs?.getLong("cached_timestamp", 0L) ?: 0L
            val cachedData = gson.fromJson(json, WeatherData::class.java) ?: return null

            val timeFormatted = if (timestamp > 0) {
                SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault()).format(Date(timestamp))
            } else "Earlier"

            cachedData.copy(
                lastUpdated = timeFormatted,
                isOfflineCached = true
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun formatTime(isoString: String?): String? {
        if (isoString.isNullOrBlank()) return null
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val date = inputFormat.parse(isoString) ?: return null
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatHourLabel(isoString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val date = inputFormat.parse(isoString) ?: return isoString
            val outputFormat = SimpleDateFormat("h a", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            isoString
        }
    }

    private fun getDayName(dateStr: String, index: Int): String {
        if (index == 0) return "Today"
        if (index == 1) return "Tomorrow"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateStr) ?: return dateStr
            val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}

