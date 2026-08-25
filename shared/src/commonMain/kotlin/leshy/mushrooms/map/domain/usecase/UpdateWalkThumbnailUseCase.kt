package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.repository.WalkRepository

class UpdateWalkThumbnailUseCase(
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(walkId: Long, thumbnailPath: String) {
        val walk = walkRepository.getById(walkId) ?: return
        walkRepository.update(walk.copy(thumbnailPath = thumbnailPath))
    }
}
