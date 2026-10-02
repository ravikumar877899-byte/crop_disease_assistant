package com.example.aicropcare.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
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

    @SuppressLint("MissingPermission")
    suspend fun getFreshLocation(context: Context): UserLocation? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission(context)) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val provider = if (isGpsEnabled) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER
            try {
                locationManager.getCurrentLocation(
                    provider,
                    null,
                    ContextCompat.getMainExecutor(context)
                ) { location ->
                    if (location != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val cityName = getCityNameFromCoordinates(context, location.latitude, location.longitude)
                            if (continuation.isActive) continuation.resume(UserLocation(location.latitude, location.longitude, cityName))
                        }
                    } else {
                        if (continuation.isActive) continuation.resume(null)
                    }
                }
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(null)
            }
        } else {
            try {
                val provider = if (isNetworkEnabled) LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationManager.removeUpdates(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            val cityName = getCityNameFromCoordinates(context, location.latitude, location.longitude)
                            if (continuation.isActive) continuation.resume(UserLocation(location.latitude, location.longitude, cityName))
                        }
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }
                locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                
                continuation.invokeOnCancellation {
                    locationManager.removeUpdates(listener)
                }
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(null)
            }
        }
    }
    
    suspend fun getBestLocation(context: Context): UserLocation? {
        val fresh = getFreshLocation(context)
        if (fresh != null) return fresh
        return getLastKnownLocation(context)
    }
    
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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
