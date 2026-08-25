package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.FieldMarkRepository

class AddPlaceMarkUseCase(
    private val fieldMarkRepository: FieldMarkRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(
        walkId: Long,
        location: GeoPoint?,
        timestamp: Long,
        name: String,
        description: String,
        photoPath: String?,
    ): FieldMark {
        val miscCategory = categoryRepository.getByNameKey(MISC_CATEGORY_NAME_KEY)
        requireNotNull(miscCategory) { "Misc category must exist before recording place marks" }
        val mark = FieldMark(
            id = 0,
            walkId = walkId,
            categoryId = miscCategory.id,
            lat = location?.lat ?: 0.0,
            lon = location?.lon ?: 0.0,
            timestamp = timestamp,
            type = MarkType.POI,
            photoPath = photoPath,
            name = name,
            description = description,
        )
        val id = fieldMarkRepository.addMark(mark)
        return mark.copy(id = id)
    }
}
