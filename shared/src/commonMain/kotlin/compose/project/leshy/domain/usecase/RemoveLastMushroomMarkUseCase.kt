package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.WalkRepository

class RemoveLastMushroomMarkUseCase(
    private val fieldMarkRepository: FieldMarkRepository,
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(walkId: Long, categoryId: Long): Boolean {
        val removed = fieldMarkRepository.removeLastMushroomMark(walkId, categoryId)
        if (removed) {
            val walk = walkRepository.getById(walkId)
            if (walk != null && walk.mushroomCount > 0) {
                walkRepository.update(walk.copy(mushroomCount = walk.mushroomCount - 1))
            }
        }
        return removed
    }
}
