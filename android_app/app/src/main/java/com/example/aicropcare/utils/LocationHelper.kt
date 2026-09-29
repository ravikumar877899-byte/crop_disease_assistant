package com.example.aicropcare.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val name: String
)

object LocationHelper {

    // Default agricultural hub (Coimbatore, Tamil Nadu)
    val DEFAULT_LOCATION = UserLocation(
        latitude = 11.0168,
        longitude = 76.9558,
        name = "Coimbatore, Tamil Nadu"
    )

    // Popular Indian agricultural regions for instant manual selection
    val POPULAR_REGIONS = listOf(
        UserLocation(11.0168, 76.9558, "Coimbatore, Tamil Nadu"),
        UserLocation(13.0827, 80.2707, "Chennai, Tamil Nadu"),
        UserLocation(10.7870, 79.1378, "Thanjavur, Tamil Nadu"),
        UserLocation(9.9252, 78.1198, "Madurai, Tamil Nadu"),
        UserLocation(12.9716, 77.5946, "Bengaluru, Karnataka"),
        UserLocation(17.3850, 78.4867, "Hyderabad, Telangana"),
        UserLocation(18.5204, 73.8567, "Pune, Maharashtra"),
        UserLocation(28.6139, 77.2090, "New Delhi, Delhi"),
        UserLocation(23.0225, 72.5714, "Ahmedabad, Gujarat"),
        UserLocation(30.9010, 75.8573, "Ludhiana, Punjab"),
        UserLocation(22.5726, 88.3639, "Kolkata, West Bengal"),
        UserLocation(26.9124, 75.7873, "Jaipur, Rajasthan"),
        UserLocation(10.8505, 76.2711, "Palakkad, Kerala")
    )

    fun hasLocationPermission(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    @Suppress("DEPRECATION")
    suspend fun getLastKnownLocation(context: Context): UserLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) return@withContext null

        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: return@withContext null

            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null

            for (provider in providers) {
                try {
                    val location = locationManager.getLastKnownLocation(provider) ?: continue
                    if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                        bestLocation = location
                    }
                } catch (e: SecurityException) {
                    // Permission revoked or not granted
                }
            }

            if (bestLocation != null) {
                val cityName = getCityNameFromCoordinates(context, bestLocation.latitude, bestLocation.longitude)
                UserLocation(
                    latitude = bestLocation.latitude,
                    longitude = bestLocation.longitude,
                    name = cityName
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("DEPRECATION")
    suspend fun getCityNameFromCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: address.featureName
                    val state = address.adminArea
                    return@withContext if (!city.isNullOrBlank() && !state.isNullOrBlank() && city != state) {
                        "$city, $state"
                    } else {
                        city ?: state ?: "Farm Location"
                    }
                }
            } else {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: address.featureName
                    val state = address.adminArea
                    return@withContext if (!city.isNullOrBlank() && !state.isNullOrBlank() && city != state) {
                        "$city, $state"
                    } else {
                        city ?: state ?: "Farm Location"
                    }
                }
            }
        } catch (e: Exception) {
            // Geocoder unavailable on some devices/emulators
        }
        "Lat: ${"%.2f".format(latitude)}, Lon: ${"%.2f".format(longitude)}"
    }
}
