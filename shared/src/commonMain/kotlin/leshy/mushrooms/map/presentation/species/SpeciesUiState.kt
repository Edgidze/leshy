package leshy.mushrooms.map.presentation.species

import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.presentation.CollectionPickerItem
import leshy.mushrooms.map.presentation.UserSpeciesGroup

data class SpeciesUiState(
    val language: AppLanguage = AppLanguage.EN,
    val collectionPickerItems: List<CollectionPickerItem> = emptyList(),
    /** User-created (`USER`) and imported (`IMPORTED`) species — everything
     * `CategoryRepository.observeNonCatalog()` returns, `USER` first. Listed by `source`, not by
     * `isActive`/`isPicked`, so a hidden species stays reachable to un-hide — see
     * `.claude/plans/user-mushrooms.md`. */
    val userSpecies: List<Category> = emptyList(),
    /** Те же виды, что в [userSpecies], разложенные по пользовательским подборкам — в этом виде
     * их и рисует экран (`.claude/plans/user-collections.md`). */
    val userGroups: List<UserSpeciesGroup> = emptyList(),
    /** Общий на весь экран запрос поиска: им фильтруются и страновые подборки сверху, и
     * пользовательские снизу — поле ввода одно, живёт в `CollectionPicker`. */
    val collectionQuery: String = "",
    /** Species pending the delete confirmation dialog (`SpeciesScreen`'s "✕" button) — non-null
     * while the dialog is showing. */
    val pendingDelete: Category? = null,
    /** `true` до первой выдачи из базы — см. `ArchiveUiState.isLoading`, там же и зачем. */
    val isLoading: Boolean = true,
)
