package leshy.mushrooms.map.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Box of roughly [kmWide] x [kmTall] around (55°N, 37°E) — a mid-latitude walking region. */
private fun box(kmWide: Double, kmTall: Double): OfflineRegionEstimate {
    val latSpan = kmTall / 111.0
    val lonSpan = kmWide / (111.32 * 0.574) // cos(55°)
    return estimateOfflineRegion(
        west = 37.0 - lonSpan / 2,
        south = 55.0 - latSpan / 2,
        east = 37.0 + lonSpan / 2,
        north = 55.0 + latSpan / 2,
    )
}

class OfflineRegionEstimatorTest {

    @Test
    fun keepsTheDeepestExistingZoomForRegionsOfAWalkableSize() {
        // z14 is the level that carries buildings, POIs and most labels, and is the deepest tile
        // OpenFreeMap's planet source has. Dropping below it is what makes a downloaded region
        // render as blurry blobs with no names.
        // ~90x90 km is where the 6000-tile budget (MapLibre's own default offline limit) runs out
        // at this latitude; past that the step-down is deliberate, not accidental.
        listOf(5.0, 20.0, 50.0, 90.0).forEach { km ->
            assertEquals(14, box(km, km).maxZoom, "region ${km}x$km km")
        }
    }

    @Test
    fun stillBacksOffForRegionsTooLargeToFitTheTileBudget() {
        val huge = box(1_000.0, 1_000.0)

        assertTrue(huge.maxZoom < 14, "maxZoom=${huge.maxZoom}")
        assertTrue(huge.maxZoom >= 5, "maxZoom=${huge.maxZoom}")
    }

    @Test
    fun neverGetsMoreDetailedAsTheAreaGrows() {
        val zooms = listOf(10.0, 100.0, 400.0, 1_000.0, 4_000.0).map { box(it, it).maxZoom }

        assertEquals(zooms.sortedDescending(), zooms, zooms.toString())
    }
}
