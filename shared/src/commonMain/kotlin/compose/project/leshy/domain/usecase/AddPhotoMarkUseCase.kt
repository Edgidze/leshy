package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository

class AddPhotoMarkUseCase(
    private val fieldMarkRepository: FieldMarkRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(walkId: Long, location: GeoPoint?, timestamp: Long, photoPath: String): FieldMark {
        val miscCategory = categoryRepository.getByNameKey(MISC_CATEGORY_NAME_KEY)
        requireNotNull(miscCategory) { "Misc category must exist before recording photo marks" }
        val mark = FieldMark(
            id = 0,
            walkId = walkId,
            categoryId = miscCategory.id,
            lat = location?.lat ?: 0.0,
            lon = location?.lon ?: 0.0,
            timestamp = timestamp,
            type = MarkType.PHOTO,
            photoPath = photoPath,
        )
        val id = fieldMarkRepository.addMark(mark)
        return mark.copy(id = id)
    }
}
