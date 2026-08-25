package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository

/** Bulk-writes [Category.isPicked][leshy.mushrooms.map.domain.model.Category.isPicked] AND
 * [Category.isActive][leshy.mushrooms.map.domain.model.Category.isActive] for every member of a
 * collection — the "select whole collection" shortcut in the collection picker (Settings and the
 * first-run onboarding screen share this use case, see `.claude/plans/mushroom-collections.md`).
 * Writes `isActive` directly rather than leaving it to the recalculation cascade alone — see
 * [SetCategoryPickedUseCase] for why (a member with an existing find stays `isFilterEligible`
 * forever, so the cascade alone can never turn its `isActive` off). Recalculates
 * `isFilterEligible` afterward so every picker write site gets that part for free, rather than
 * each caller remembering to chain it (see [RecalculateFilterEligibilityUseCase]). */
class SetCollectionPickedUseCase(
    private val collectionRepository: CollectionRepository,
    private val categoryRepository: CategoryRepository,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
) {
    suspend operator fun invoke(collectionId: Long, picked: Boolean) {
        collectionRepository.getMemberCategoryIds(collectionId).forEach { categoryId ->
            val category = categoryRepository.getById(categoryId) ?: return@forEach
            if (category.isPicked != picked || category.isActive != picked) {
                categoryRepository.upsert(category.copy(isPicked = picked, isActive = picked))
            }
        }
        recalculateFilterEligibility()
    }
}
