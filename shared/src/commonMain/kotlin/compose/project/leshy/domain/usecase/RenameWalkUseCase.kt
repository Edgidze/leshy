package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.repository.WalkRepository

class RenameWalkUseCase(
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(walkId: Long, name: String) {
        val walk = walkRepository.getById(walkId) ?: return
        walkRepository.update(walk.copy(name = name))
    }
}
