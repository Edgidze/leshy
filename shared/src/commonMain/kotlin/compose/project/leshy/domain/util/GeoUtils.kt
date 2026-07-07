package compose.project.leshy.domain.util

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_METERS = 6_371_000.0

private fun degToRad(deg: Double): Double = deg * PI / 180.0

fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = degToRad(lat2 - lat1)
    val dLon = degToRad(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(degToRad(lat1)) * cos(degToRad(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS_METERS * c
}
