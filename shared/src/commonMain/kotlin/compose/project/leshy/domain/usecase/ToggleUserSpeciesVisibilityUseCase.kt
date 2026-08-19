package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.Category

/**
 * Show/hide toggle for a row in the "My mushrooms" list (`.claude/plans/user-mushrooms.md`,
 * Phase 4) — there is no delete path by design, see that plan's "only hiding" section.
 *
 * Both directions write [Category.isActive] explicitly, not just [Category.isPicked]:
 * `RecalculateFilterEligibilityUseCase` only ever clears `isActive` when a species stops being
 * `isFilterEligible`, and never turns it back on — a species with existing finds stays eligible
 * forever (`isFilterEligible = isPicked || has finds`), so the cascade alone can neither hide nor
 * un-hide it. Same pattern the temporary `DebugUserCategoryUseCase` used before this replaced it.
 */
class ToggleUserSpeciesVisibilityUseCase(
    private val setCategoryPicked: SetCategoryPickedUseCase,
) {
    suspend operator fun invoke(category: Category, visible: Boolean) {
        setCategoryPicked(category.copy(isActive = visible), visible)
    }
}
