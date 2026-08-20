package compose.project.leshy.presentation.species

import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.presentation.CollectionPickerItem

data class SpeciesUiState(
    val language: AppLanguage = AppLanguage.EN,
    val collectionPickerItems: List<CollectionPickerItem> = emptyList(),
    /** User-created (`USER`) and imported (`IMPORTED`) species — everything
     * `CategoryRepository.observeNonCatalog()` returns, `USER` first. Listed by `source`, not by
     * `isActive`/`isPicked`, so a hidden species stays reachable to un-hide — see
     * `.claude/plans/user-mushrooms.md`. */
    val userSpecies: List<Category> = emptyList(),
    /** Species pending the delete confirmation dialog (`SpeciesScreen`'s "✕" button) — non-null
     * while the dialog is showing. */
    val pendingDelete: Category? = null,
)
