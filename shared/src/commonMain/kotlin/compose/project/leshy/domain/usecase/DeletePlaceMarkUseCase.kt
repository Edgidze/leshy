package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.repository.FieldMarkRepository

class DeletePlaceMarkUseCase(
    private val fieldMarkRepository: FieldMarkRepository,
) {
    suspend operator fun invoke(mark: FieldMark) {
        fieldMarkRepository.deleteMark(mark)
    }
}
