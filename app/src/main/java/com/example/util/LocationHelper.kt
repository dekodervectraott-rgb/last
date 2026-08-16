package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val street: String = "",
    val district: String = "",
    val city: String = "",
    val blockNumber: String = ""
)

object LocationHelper {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val cancellationTokenSource = CancellationTokenSource()

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        continuation.resume(location)
                    } else {
                        // Fallback to last known location
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                            continuation.resume(lastLoc)
                        }.addOnFailureListener {
                            continuation.resume(null)
                        }
                    }
                }.addOnFailureListener {
                    continuation.resume(null)
                }

                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
            } catch (e: Exception) {
                continuation.resume(null)
            }
        }
    }

    suspend fun reverseGeocode(context: Context, lat: Double, lon: Double): LocationResult =
        withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val addresses = suspendCancellableCoroutine<List<Address>> { cont ->
                        geocoder.getFromLocation(lat, lon, 1) { addresses ->
                            cont.resume(addresses)
                        }
                    }
                    parseAddress(lat, lon, addresses.firstOrNull())
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    parseAddress(lat, lon, addresses?.firstOrNull())
                }
            } catch (e: Exception) {
                LocationResult(latitude = lat, longitude = lon)
            }
        }

    private fun parseAddress(lat: Double, lon: Double, address: Address?): LocationResult {
        if (address == null) return LocationResult(latitude = lat, longitude = lon)

        val street = address.thoroughfare ?: address.featureName ?: ""
        val block = address.subThoroughfare ?: ""
        val district = address.subLocality ?: address.locality ?: ""
        val city = address.locality ?: ""

        return LocationResult(
            latitude = lat,
            longitude = lon,
            street = street,
            blockNumber = block,
            district = district,
            city = city
        )
    }

    /**
     * Calculate distance between two GPS coordinates using Haversine formula in meters.
     */
    fun calculateDistanceMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun formatDistance(distanceMeters: Double): String {
        return if (distanceMeters < 1000) {
            "${distanceMeters.toInt()} m"
        } else {
            String.format(Locale.US, "%.1f km", distanceMeters / 1000.0)
        }
    }
}
