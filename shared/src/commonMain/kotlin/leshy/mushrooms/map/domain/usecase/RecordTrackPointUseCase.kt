package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.TrackPoint
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import leshy.mushrooms.map.domain.util.haversineMeters

class RecordTrackPointUseCase(
    private val trackPointRepository: TrackPointRepository,
    private val walkRepository: WalkRepository,
) {
    /** Returns the distance in meters added by this point (0.0 if there was no previous point). */
    suspend operator fun invoke(walkId: Long, point: GeoPoint, sequence: Int, previous: GeoPoint?): Double {
        trackPointRepository.addPoint(
            TrackPoint(
                id = 0,
                walkId = walkId,
                lat = point.lat,
                lon = point.lon,
                timestamp = point.timestamp,
                elevation = point.elevation,
                sequence = sequence,
            ),
        )
        if (previous == null) return 0.0
        val deltaMeters = haversineMeters(previous.lat, previous.lon, point.lat, point.lon)
        val walk = walkRepository.getById(walkId) ?: return deltaMeters
        walkRepository.update(walk.copy(distanceMeters = walk.distanceMeters + deltaMeters))
        return deltaMeters
    }
}
