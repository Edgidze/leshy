package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.Walk
import compose.project.leshy.domain.repository.WalkRepository

class StartWalkUseCase(
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(name: String, startTime: Long, startLat: Double, startLon: Double): Long =
        walkRepository.insert(
            Walk(
                id = 0,
                name = name,
                startTime = startTime,
                endTime = null,
                distanceMeters = 0.0,
                avgSpeed = 0.0,
                startLat = startLat,
                startLon = startLon,
                endLat = null,
                endLon = null,
                mushroomCount = 0,
            ),
        )
}
