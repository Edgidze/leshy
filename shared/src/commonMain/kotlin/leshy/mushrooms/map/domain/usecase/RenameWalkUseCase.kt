package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.repository.WalkRepository

class RenameWalkUseCase(
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(walkId: Long, name: String) {
        val walk = walkRepository.getById(walkId) ?: return
        walkRepository.update(walk.copy(name = name))
    }
}
