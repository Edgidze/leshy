package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.repository.WalkRepository

class UpdateWalkThumbnailUseCase(
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(walkId: Long, thumbnailPath: String) {
        val walk = walkRepository.getById(walkId) ?: return
        walkRepository.update(walk.copy(thumbnailPath = thumbnailPath))
    }
}
