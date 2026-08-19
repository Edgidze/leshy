package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.OfflineRegionInfo
import kotlinx.coroutines.flow.Flow

interface OfflineRegionRepository {
    fun observeRegions(): Flow<List<OfflineRegionInfo>>

    suspend fun downloadRegion(
        name: String,
        west: Double,
        south: Double,
        east: Double,
        north: Double,
        minZoom: Int,
        maxZoom: Int,
    )

    fun resume(name: String)
    fun pause(name: String)
    suspend fun delete(name: String)

    /** Whether the map style currently live on the server differs from the pinned local copy —
     * a mismatch means a region downloaded right now won't share tile URLs with the live map (or
     * with previously-downloaded regions) until the user refreshes map data in Settings.
     * Read-only, never mutates the pin. */
    suspend fun isStyleDrifted(): Boolean
}
