package com.example.util

import kotlin.math.*

object LocationHelper {

    /**
     * Menghitung jarak lurus (meter) antara dua koordinat GPS menggunakan formula Haversine
     */
    fun calculateDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371000.0 // Radius bumi dalam meter
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Cek apakah posisi koordinat berada di dalam radius geofence kantor
     */
    fun isWithinGeofence(
        userLat: Double, userLon: Double,
        officeLat: Double, officeLon: Double,
        radiusMeters: Double
    ): Boolean {
        val distance = calculateDistanceMeters(userLat, userLon, officeLat, officeLon)
        return distance <= radiusMeters
    }

    fun formatDistance(meters: Double): String {
        return if (meters < 1000.0) {
            "${meters.roundToInt()} meter"
        } else {
            String.format(java.util.Locale.US, "%.1f km", meters / 1000.0)
        }
    }
}
