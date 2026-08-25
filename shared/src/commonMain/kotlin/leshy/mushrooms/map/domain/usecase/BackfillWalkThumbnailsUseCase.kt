package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.platform.WalkThumbnailRenderer
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import kotlinx.coroutines.flow.first

/**
 * One-shot repair pass for walks whose `thumbnailPath` is still null — either recorded before the
 * thumbnail feature existed (pre-v3 Room schema), or hit the now-fixed [WalkThumbnailRenderer] gap
 * where too few live track points at Finish time (short walks) permanently skipped rendering
 * instead of falling back to *some* location. Re-renders from each walk's already-persisted track
 * points/finds/start-or-end coordinates, so no walk is stuck without a map background forever.
 *
 * Called once per [leshy.mushrooms.map.presentation.archive.ArchiveViewModel] lifecycle (Archive
 * screen open) — cheap no-op once every walk has a thumbnail, since the null-thumbnail set shrinks
 * to empty and stays there via the normal [WalkRepository.update] write.
 */
class BackfillWalkThumbnailsUseCase(
    private val walkRepository: WalkRepository,
    private val trackPointRepository: TrackPointRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val walkThumbnailRenderer: WalkThumbnailRenderer,
    private val updateWalkThumbnail: UpdateWalkThumbnailUseCase,
) {
    suspend operator fun invoke() {
        val walksMissingThumbnail = walkRepository.observeAll().first().filter { it.thumbnailPath == null }
        walksMissingThumbnail.forEach { walk -> backfill(walk) }
    }

    private suspend fun backfill(walk: Walk) {
        val track = trackPointRepository.observeByWalkId(walk.id).first()
            .sortedBy { it.sequence }
            .map { GeoPoint(it.lat, it.lon, it.elevation, it.timestamp) }
        val findLocations = fieldMarkRepository.observeByWalkId(walk.id).first()
            .filter { it.type == MarkType.MUSHROOM }
            .map { GeoPoint(it.lat, it.lon, null, it.timestamp) }
        val anchor = anchorOf(walk)

        val thumbnailPath = walkThumbnailRenderer.render(walk.id, track, findLocations, anchor)
        if (thumbnailPath != null) updateWalkThumbnail(walk.id, thumbnailPath)
    }

    // walk.startLat/startLon default to (0.0, 0.0) when Start was pressed before GPS produced a
    // fix (see RecordViewModel.start()) — not a real location, so it's only usable as a last
    // resort, and never when it's the (0,0) sentinel with nothing else to go on either.
    private fun anchorOf(walk: Walk): GeoPoint? = when {
        walk.endLat != null && walk.endLon != null ->
            GeoPoint(walk.endLat, walk.endLon, null, walk.endTime ?: walk.startTime)
        walk.startLat != 0.0 || walk.startLon != 0.0 ->
            GeoPoint(walk.startLat, walk.startLon, null, walk.startTime)
        else -> null
    }
}
