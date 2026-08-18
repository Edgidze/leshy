package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.repository.CategoryRepository

/** Single-species toggle in the collection picker (Settings and the first-run onboarding screen —
 * see `.claude/plans/mushroom-collections.md`). Writes
 * [Category.isPicked][compose.project.leshy.domain.model.Category.isPicked] then recalculates
 * `isFilterEligible`/`isActive`, same cascade as [SetCollectionPickedUseCase]'s bulk path. */
class SetCategoryPickedUseCase(
    private val categoryRepository: CategoryRepository,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
) {
    suspend operator fun invoke(category: Category, picked: Boolean) {
        categoryRepository.upsert(category.copy(isPicked = picked))
        recalculateFilterEligibility()
    }
}
