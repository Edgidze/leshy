package compose.project.leshy.domain.util

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_METERS = 6_371_000.0
private const val AHEAD_THRESHOLD_DEGREES = 3.0

private fun degToRad(deg: Double): Double = deg * PI / 180.0
private fun radToDeg(rad: Double): Double = rad * 180.0 / PI

fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = degToRad(lat2 - lat1)
    val dLon = degToRad(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(degToRad(lat1)) * cos(degToRad(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS_METERS * c
}

/** Initial bearing (forward azimuth) from (lat1,lon1) to (lat2,lon2), in degrees, normalized to [0, 360). */
fun bearingDegrees(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val phi1 = degToRad(lat1)
    val phi2 = degToRad(lat2)
    val dLon = degToRad(lon2 - lon1)
    val y = sin(dLon) * cos(phi2)
    val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(dLon)
    return (radToDeg(atan2(y, x)) + 360.0) % 360.0
}

/**
 * Under forest canopy (this app's actual use case) GPS accuracy commonly degrades to 15-30m, well
 * past the 5-10m open-sky norm — a tight threshold would leave the target flickering between
 * "arrived" and a turn direction as fixes jitter around it.
 */
private const val ARRIVAL_THRESHOLD_METERS = 15.0

fun hasArrived(distanceMeters: Double): Boolean = distanceMeters <= ARRIVAL_THRESHOLD_METERS

enum class TurnDirection { LEFT, RIGHT, AHEAD }

data class TurnRecommendation(val direction: TurnDirection, val degrees: Double)

/** Smallest-angle turn from [courseBearingDegrees] (current direction of travel) to face [targetBearingDegrees]. */
fun turnRecommendation(courseBearingDegrees: Double, targetBearingDegrees: Double): TurnRecommendation {
    val delta = ((targetBearingDegrees - courseBearingDegrees + 540.0) % 360.0) - 180.0 // (-180, 180]
    return when {
        -AHEAD_THRESHOLD_DEGREES < delta && delta < AHEAD_THRESHOLD_DEGREES -> TurnRecommendation(TurnDirection.AHEAD, 0.0)
        delta < 0 -> TurnRecommendation(TurnDirection.LEFT, -delta)
        else -> TurnRecommendation(TurnDirection.RIGHT, delta)
    }
}
