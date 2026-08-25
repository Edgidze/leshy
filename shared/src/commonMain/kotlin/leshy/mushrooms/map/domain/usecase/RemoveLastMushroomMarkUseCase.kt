package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.WalkRepository

class RemoveLastMushroomMarkUseCase(
    private val fieldMarkRepository: FieldMarkRepository,
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(walkId: Long, categoryId: Long): FieldMark? {
        val removed = fieldMarkRepository.removeLastMushroomMark(walkId, categoryId)
        if (removed != null) {
            val walk = walkRepository.getById(walkId)
            if (walk != null && walk.mushroomCount > 0) {
                walkRepository.update(walk.copy(mushroomCount = walk.mushroomCount - 1))
            }
        }
        return removed
    }
}
