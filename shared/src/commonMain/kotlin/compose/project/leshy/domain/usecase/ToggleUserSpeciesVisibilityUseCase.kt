package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.Category

/**
 * Show/hide toggle for a row in the "My mushrooms" list (`.claude/plans/user-mushrooms.md`,
 * Phase 4) — there is no delete path by design, see that plan's "only hiding" section.
 *
 * Delegates straight to [SetCategoryPickedUseCase], which writes both `isPicked` and `isActive`
 * itself — a species with existing finds would otherwise stay eligible forever
 * (`isFilterEligible = isPicked || has finds`) and never get its `isActive` toggled by the
 * recalculation cascade alone.
 */
class ToggleUserSpeciesVisibilityUseCase(
    private val setCategoryPicked: SetCategoryPickedUseCase,
) {
    suspend operator fun invoke(category: Category, visible: Boolean) {
        setCategoryPicked(category, visible)
    }
}
