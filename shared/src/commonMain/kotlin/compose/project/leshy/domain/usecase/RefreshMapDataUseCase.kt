package compose.project.leshy.domain.usecase

import compose.project.leshy.data.repository.MapStyleCacheRepository
import compose.project.leshy.domain.repository.OfflineRegionRepository
import kotlinx.coroutines.flow.first

data class RefreshMapDataResult(
    val success: Boolean,
    /** How many already-downloaded regions got automatically re-queued for download because the
     * refreshed style no longer matches what they were originally pinned under. 0 means either the
     * fetch failed, the style didn't actually change, or there were no offline regions to begin
     * with — in all three cases nothing else needs to happen. */
    val regionsRedownloading: Int,
)

/**
 * "Update map data" (Settings). A refresh is only meaningful to the user if OpenFreeMap's
 * underlying OSM data actually moved since the last pin (new trails, roads, POIs — not just the
 * snapshot's internal timestamp) — but from the app's side, whether the *fetched style content*
 * changed at all is exactly the signal for "existing offline regions are about to go stale", since
 * every downloaded region was pinned to tile URLs resolved from the OLD content. So on a real
 * change, every currently-downloaded region is deleted and immediately re-queued under the new
 * pinned style — automatically, not gated behind a second confirmation, per explicit product
 * decision (the whole point of pinning is that the user should never have to think about this
 * unless they themselves asked for fresher data). See MapStyleCacheRepository/ui/map/CLAUDE.md.
 */
class RefreshMapDataUseCase(
    private val mapStyleCacheRepository: MapStyleCacheRepository,
    private val offlineRegionRepository: OfflineRegionRepository,
) {
    suspend operator fun invoke(): RefreshMapDataResult {
        val regionsBefore = offlineRegionRepository.observeRegions().first()
        val refreshResult = mapStyleCacheRepository.refreshFromNetwork()
        val changed = refreshResult.getOrDefault(false)

        if (!changed || regionsBefore.isEmpty()) {
            return RefreshMapDataResult(success = refreshResult.isSuccess, regionsRedownloading = 0)
        }

        regionsBefore.forEach { region ->
            offlineRegionRepository.delete(region.name)
            offlineRegionRepository.downloadRegion(
                name = region.name,
                west = region.west,
                south = region.south,
                east = region.east,
                north = region.north,
                minZoom = region.minZoom,
                maxZoom = region.maxZoom,
            )
        }
        return RefreshMapDataResult(success = true, regionsRedownloading = regionsBefore.size)
    }
}
