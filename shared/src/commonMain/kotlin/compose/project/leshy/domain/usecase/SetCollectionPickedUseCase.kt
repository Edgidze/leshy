package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.CollectionRepository

/** Bulk-writes [Category.isPicked][compose.project.leshy.domain.model.Category.isPicked] for every
 * member of a collection — the "select whole collection" shortcut in the collection picker
 * (Settings and the first-run onboarding screen share this use case, see
 * `.claude/plans/mushroom-collections.md`). Recalculates `isFilterEligible`/`isActive` afterward so
 * every picker write site gets the cascade for free, rather than each caller remembering to chain
 * it (see [RecalculateFilterEligibilityUseCase]). */
class SetCollectionPickedUseCase(
    private val collectionRepository: CollectionRepository,
    private val categoryRepository: CategoryRepository,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
) {
    suspend operator fun invoke(collectionId: Long, picked: Boolean) {
        collectionRepository.getMemberCategoryIds(collectionId).forEach { categoryId ->
            val category = categoryRepository.getById(categoryId) ?: return@forEach
            if (category.isPicked != picked) {
                categoryRepository.upsert(category.copy(isPicked = picked))
            }
        }
        recalculateFilterEligibility()
    }
}
