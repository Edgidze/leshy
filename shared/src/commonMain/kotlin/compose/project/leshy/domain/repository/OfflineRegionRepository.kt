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

    /** Clears MapLibre's ambient (browsing) cache — tiles fetched while just panning/zooming the
     * live map, never pinned to a named offline region. Does NOT affect any downloaded region's own
     * tiles. Exists as a diagnostic tool: since ambient-cached tiles render offline exactly like a
     * downloaded region's tiles do, a user checking "is this area really covered by my download"
     * can't otherwise tell the two apart. */
    suspend fun clearAmbientCache()
}
