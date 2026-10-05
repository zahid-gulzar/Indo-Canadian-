package com.example.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class GeoLocationResult(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val isRealGps: Boolean,
    val locationDescription: String
)

class LocationHelper(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(fallbackStationName: String, fallbackLat: Double, fallbackLng: Double): GeoLocationResult {
        if (!hasLocationPermission()) {
            return GeoLocationResult(
                latitude = fallbackLat,
                longitude = fallbackLng,
                accuracyMeters = 50f,
                isRealGps = false,
                locationDescription = "$fallbackStationName (Station Coordinates)"
            )
        }

        return try {
            val cancellationTokenSource = CancellationTokenSource()
            val location = suspendCancellableCoroutine { continuation ->
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { loc ->
                    if (continuation.isActive) {
                        continuation.resume(loc)
                    }
                }.addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }

                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
            }

            if (location != null) {
                GeoLocationResult(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracyMeters = location.accuracy,
                    isRealGps = true,
                    locationDescription = "GPS (${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)}) ±${location.accuracy.toInt()}m"
                )
            } else {
                GeoLocationResult(
                    latitude = fallbackLat,
                    longitude = fallbackLng,
                    accuracyMeters = 30f,
                    isRealGps = false,
                    locationDescription = "$fallbackStationName (Station Coordinates)"
                )
            }
        } catch (_: Exception) {
            GeoLocationResult(
                latitude = fallbackLat,
                longitude = fallbackLng,
                accuracyMeters = 30f,
                isRealGps = false,
                locationDescription = "$fallbackStationName (Station Coordinates)"
            )
        }
    }
}
