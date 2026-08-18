package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import kotlinx.coroutines.flow.first

/**
 * Recomputes `Category.isFilterEligible` for every category — `isPicked || has an existing
 * FieldMark` — and cascades `isActive = false` whenever eligibility just turned off. Without the
 * cascade, un-picking a species with no finds in the collection picker would leave it dangling
 * `isActive = true` on Map/Record with no way left to switch it off, since it would already be
 * gone from the Filter screen's `isFilterEligible`-gated list — see
 * `.claude/plans/mushroom-collections.md`.
 *
 * Called whenever the collection picker saves (bulk or single-species toggle) and once at app
 * startup (self-heal after the v4→v5 migration) — idempotent, safe to call from multiple
 * ViewModel `init`s, same pattern as [EnsureDefaultCategoriesUseCase].
 */
class RecalculateFilterEligibilityUseCase(
    private val categoryRepository: CategoryRepository,
    private val fieldMarkRepository: FieldMarkRepository,
) {
    suspend operator fun invoke() {
        val markedCategoryIds = fieldMarkRepository.observeAll().first().map { it.categoryId }.toSet()
        categoryRepository.observeAll().first().forEach { category ->
            val eligible = category.isPicked || category.id in markedCategoryIds
            val needsUpdate = eligible != category.isFilterEligible || (!eligible && category.isActive)
            if (needsUpdate) {
                categoryRepository.upsert(
                    category.copy(
                        isFilterEligible = eligible,
                        isActive = category.isActive && eligible,
                    ),
                )
            }
        }
    }
}
