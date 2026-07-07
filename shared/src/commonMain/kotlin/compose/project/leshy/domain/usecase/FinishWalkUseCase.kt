package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.repository.WalkRepository

class FinishWalkUseCase(
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(walkId: Long, endTime: Long, endLat: Double?, endLon: Double?) {
        val walk = walkRepository.getById(walkId) ?: return
        val durationSeconds = (endTime - walk.startTime) / 1000.0
        val avgSpeed = if (durationSeconds > 0) walk.distanceMeters / durationSeconds else 0.0
        walkRepository.update(
            walk.copy(endTime = endTime, endLat = endLat, endLon = endLon, avgSpeed = avgSpeed),
        )
    }
}
