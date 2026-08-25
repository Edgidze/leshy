package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.repository.CategoryRepository

/** Single-species toggle in the collection picker (Settings and the first-run onboarding screen —
 * see `.claude/plans/mushroom-collections.md`). Writes
 * [Category.isPicked][leshy.mushrooms.map.domain.model.Category.isPicked] AND
 * [Category.isActive][leshy.mushrooms.map.domain.model.Category.isActive] together, then
 * recalculates `isFilterEligible` (same cascade as [SetCollectionPickedUseCase]'s bulk path).
 *
 * `isActive` can't be left to the recalculation cascade alone: that cascade only ever turns
 * `isActive` off when a species stops being `isFilterEligible`, and a species with an existing
 * find stays `isFilterEligible` forever (`isPicked || has finds`) — so unchecking an
 * already-recorded species in the picker was a silent no-op for Map/Record, which still read a
 * stale `isActive = true`. Writing `isActive` directly here makes the picker checkbox the source
 * of truth regardless of find history, same fix already applied to user species via
 * [ToggleUserSpeciesVisibilityUseCase]. */
class SetCategoryPickedUseCase(
    private val categoryRepository: CategoryRepository,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
) {
    suspend operator fun invoke(category: Category, picked: Boolean) {
        categoryRepository.upsert(category.copy(isPicked = picked, isActive = picked))
        recalculateFilterEligibility()
    }
}
