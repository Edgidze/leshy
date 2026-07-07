package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.WalkRepository

class AddMushroomMarkUseCase(
    private val fieldMarkRepository: FieldMarkRepository,
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(walkId: Long, categoryId: Long, location: GeoPoint?, timestamp: Long): FieldMark {
        val mark = FieldMark(
            id = 0,
            walkId = walkId,
            categoryId = categoryId,
            lat = location?.lat ?: 0.0,
            lon = location?.lon ?: 0.0,
            timestamp = timestamp,
            type = MarkType.MUSHROOM,
            photoPath = null,
        )
        val id = fieldMarkRepository.addMark(mark)
        val walk = walkRepository.getById(walkId)
        if (walk != null) {
            walkRepository.update(walk.copy(mushroomCount = walk.mushroomCount + 1))
        }
        return mark.copy(id = id)
    }
}
