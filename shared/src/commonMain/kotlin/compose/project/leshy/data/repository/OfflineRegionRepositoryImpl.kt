package compose.project.leshy.data.repository

import androidx.compose.runtime.snapshotFlow
import compose.project.leshy.domain.model.OfflineRegionInfo
import compose.project.leshy.domain.model.OfflineRegionStatus
import compose.project.leshy.domain.repository.OfflineRegionRepository
import compose.project.leshy.ui.map.OPEN_FREE_MAP_STYLE_URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.maplibre.compose.offline.DownloadProgress
import org.maplibre.compose.offline.DownloadStatus
import org.maplibre.compose.offline.OfflineManager
import org.maplibre.compose.offline.OfflinePack
import org.maplibre.compose.offline.OfflinePackDefinition
import org.maplibre.spatialk.geojson.BoundingBox

/**
 * Wraps the maplibre-compose [OfflineManager] singleton, which is itself the persistent
 * (survives app restart) catalog of downloaded regions — no separate Room table. The region name
 * (the only thing the native pack doesn't already track) rides along as raw UTF-8 bytes in
 * [OfflinePack.metadata]; packs are looked up by decoded name for resume/pause/delete since the
 * library exposes no numeric/string id of its own.
 */
class OfflineRegionRepositoryImpl(
    private val offlineManager: OfflineManager,
    private val mapStyleCacheRepository: MapStyleCacheRepository,
) : OfflineRegionRepository {

    override fun observeRegions(): Flow<List<OfflineRegionInfo>> =
        // snapshotFlow bridges OfflineManager.packs (a Compose State, not a Flow) into a cold
        // Flow — works outside @Composable as long as Compose's global snapshot system is
        // running, which it already is on both app hosts. Reading pack.downloadProgress here too
        // (not just the outer `packs` set) is required, not cosmetic: snapshotFlow only re-emits on
        // a State read INSIDE this block, and each OfflinePack's progress lives on its own separate
        // State — without this, a pack's progress is only ever (re-)read at the instant it's added
        // to/removed from the set, freezing every pack's displayed status/size at whatever it was
        // when it (or some unrelated pack) was last created or deleted.
        snapshotFlow { offlineManager.packs.map { pack -> pack to pack.downloadProgress } }
            .map { entries -> entries.map { (pack, progress) -> toRegionInfo(pack, progress) } }

    override suspend fun downloadRegion(
        name: String,
        west: Double,
        south: Double,
        east: Double,
        north: Double,
        minZoom: Int,
        maxZoom: Int,
    ) {
        // Plain remote URL, NOT the pinned local file — MapLibre's native offline downloader
        // resolves styleUrl through its own HTTP resource loader, which can't parse a `file://`
        // reference (confirmed on-device: "Unable to parse resourceUrl file://..." from
        // Mbgl-HttpRequest, and the pack then silently never leaves 0 bytes downloaded). The live
        // map never hits this code path — it reads the pinned file into memory itself and hands
        // MapLibre already-parsed JSON, so it never needed the native loader to resolve a URL at
        // all. See MapStyleCacheRepository's doc for why this reopens a narrow drift window (a
        // region downloaded between a server-side snapshot rotation and the user's next "Обновить
        // данные карты") — isStyleDrifted() below is how callers detect and warn about that.
        val definition = OfflinePackDefinition.TilePyramid(
            styleUrl = OPEN_FREE_MAP_STYLE_URL,
            bounds = BoundingBox(west = west, south = south, east = east, north = north),
            minZoom = minZoom,
            maxZoom = maxZoom,
        )
        val pack = offlineManager.create(definition, metadata = name.encodeToByteArray())
        offlineManager.resume(pack)
    }

    override suspend fun isStyleDrifted(): Boolean = mapStyleCacheRepository.isRemoteStyleDrifted()

    override fun resume(name: String) {
        findPack(name)?.let(offlineManager::resume)
    }

    override fun pause(name: String) {
        findPack(name)?.let(offlineManager::pause)
    }

    override suspend fun delete(name: String) {
        findPack(name)?.let { offlineManager.delete(it) }
    }

    private fun findPack(name: String): OfflinePack? =
        offlineManager.packs.firstOrNull { decodeName(it) == name }

    private fun decodeName(pack: OfflinePack): String = pack.metadata?.decodeToString().orEmpty()

    private fun toRegionInfo(pack: OfflinePack, progress: DownloadProgress): OfflineRegionInfo {
        val definition = pack.definition as? OfflinePackDefinition.TilePyramid
        val bounds = definition?.bounds
        return OfflineRegionInfo(
            name = decodeName(pack),
            west = bounds?.southwest?.longitude ?: 0.0,
            south = bounds?.southwest?.latitude ?: 0.0,
            east = bounds?.northeast?.longitude ?: 0.0,
            north = bounds?.northeast?.latitude ?: 0.0,
            minZoom = definition?.minZoom ?: 0,
            maxZoom = definition?.maxZoom ?: 0,
            status = progress.toStatus(),
            completedTileCount = (progress as? DownloadProgress.Healthy)?.completedTileCount ?: 0L,
            completedBytes = (progress as? DownloadProgress.Healthy)?.completedTileBytes ?: 0L,
            requiredTileCount = (progress as? DownloadProgress.Healthy)
                ?.takeIf { it.isRequiredResourceCountPrecise }
                ?.requiredResourceCount,
        )
    }

    private fun DownloadProgress.toStatus(): OfflineRegionStatus = when (this) {
        is DownloadProgress.Healthy -> when (status) {
            DownloadStatus.Paused -> OfflineRegionStatus.PAUSED
            DownloadStatus.Downloading -> OfflineRegionStatus.DOWNLOADING
            DownloadStatus.Complete -> OfflineRegionStatus.COMPLETE
        }
        is DownloadProgress.Error, is DownloadProgress.TileLimitExceeded -> OfflineRegionStatus.ERROR
        DownloadProgress.Unknown -> OfflineRegionStatus.PAUSED
    }
}
