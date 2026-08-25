package leshy.mushrooms.map.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val EPSILON = 0.01

class GeoUtilsTest {
    @Test
    fun bearingDegreesPointsDueNorthEastSouthWest() {
        val lat = 55.0
        val lon = 37.0
        val delta = 0.01

        assertEquals(0.0, bearingDegrees(lat, lon, lat + delta, lon), EPSILON)
        assertEquals(90.0, bearingDegrees(lat, lon, lat, lon + delta), 1.0)
        assertEquals(180.0, bearingDegrees(lat, lon, lat - delta, lon), EPSILON)
        assertEquals(270.0, bearingDegrees(lat, lon, lat, lon - delta), 1.0)
    }

    @Test
    fun bearingDegreesIsAlwaysInRange() {
        val bearing = bearingDegrees(10.0, 20.0, -5.0, 170.0)
        assertTrue(bearing >= 0.0 && bearing < 360.0)
    }

    @Test
    fun turnRecommendationPicksShorterSideAcrossTheWrapAround() {
        val result = turnRecommendation(courseBearingDegrees = 350.0, targetBearingDegrees = 10.0)
        assertEquals(TurnDirection.RIGHT, result.direction)
        assertEquals(20.0, result.degrees, EPSILON)
    }

    @Test
    fun turnRecommendationSymmetricLeftAndRight() {
        val right = turnRecommendation(courseBearingDegrees = 0.0, targetBearingDegrees = 90.0)
        assertEquals(TurnDirection.RIGHT, right.direction)
        assertEquals(90.0, right.degrees, EPSILON)

        val left = turnRecommendation(courseBearingDegrees = 0.0, targetBearingDegrees = 270.0)
        assertEquals(TurnDirection.LEFT, left.direction)
        assertEquals(90.0, left.degrees, EPSILON)
    }

    @Test
    fun turnRecommendationReportsAheadWithinThreshold() {
        val result = turnRecommendation(courseBearingDegrees = 10.0, targetBearingDegrees = 12.0)
        assertEquals(TurnDirection.AHEAD, result.direction)
    }

    @Test
    fun hasArrivedWithinThreshold() {
        assertTrue(hasArrived(0.0))
        assertTrue(hasArrived(15.0))
    }

    @Test
    fun hasArrivedFalsePastThreshold() {
        assertTrue(!hasArrived(15.01))
        assertTrue(!hasArrived(200.0))
    }
}
