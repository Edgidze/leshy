package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.repository.FieldMarkRepository

class UpdatePlaceMarkUseCase(
    private val fieldMarkRepository: FieldMarkRepository,
) {
    suspend operator fun invoke(mark: FieldMark, name: String, description: String, photoPath: String?): FieldMark {
        val updated = mark.copy(name = name, description = description, photoPath = photoPath)
        fieldMarkRepository.updateMark(updated)
        return updated
    }
}
