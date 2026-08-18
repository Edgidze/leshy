package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.repository.FieldMarkRepository

class UpdatePlaceMarkUseCase(
    private val fieldMarkRepository: FieldMarkRepository,
) {
    suspend operator fun invoke(mark: FieldMark, name: String, description: String, photoPath: String?): FieldMark {
        val updated = mark.copy(name = name, description = description, photoPath = photoPath)
        fieldMarkRepository.updateMark(updated)
        return updated
    }
}
