package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.domain.repository.WalkRepository

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
                thumbnailPath = null,
                description = null,
            ),
        )
}
