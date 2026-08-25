package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.repository.FieldMarkRepository

class DeletePlaceMarkUseCase(
    private val fieldMarkRepository: FieldMarkRepository,
) {
    suspend operator fun invoke(mark: FieldMark) {
        fieldMarkRepository.deleteMark(mark)
    }
}
