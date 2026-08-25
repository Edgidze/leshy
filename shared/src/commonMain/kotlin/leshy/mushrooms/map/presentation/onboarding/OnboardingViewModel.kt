package leshy.mushrooms.map.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import leshy.mushrooms.map.data.catalog.countryCollectionNameKey
import leshy.mushrooms.map.data.platform.currentDeviceRegionCode
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository
import leshy.mushrooms.map.domain.repository.OnboardingRepository
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCategoriesUseCase
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCollectionsUseCase
import leshy.mushrooms.map.domain.usecase.RecalculateFilterEligibilityUseCase
import leshy.mushrooms.map.domain.usecase.SetCategoryPickedUseCase
import leshy.mushrooms.map.domain.usecase.SetCollectionPickedUseCase
import leshy.mushrooms.map.presentation.CollectionPickState
import leshy.mushrooms.map.presentation.CollectionPickerItem
import leshy.mushrooms.map.presentation.buildCollectionPickerItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * First-run screen shown once before Home (see `.claude/plans/mushroom-collections.md`, Phase 3).
 * Reuses the same [leshy.mushrooms.map.ui.components.CollectionPicker] composable and picker
 * use cases as Settings — this screen owns no picker logic of its own beyond the one-time
 * "completed" flag.
 */
class OnboardingViewModel(
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository,
    private val onboardingRepository: OnboardingRepository,
    private val ensureDefaultCategories: EnsureDefaultCategoriesUseCase,
    private val ensureDefaultCollections: EnsureDefaultCollectionsUseCase,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
    private val setCollectionPickedUseCase: SetCollectionPickedUseCase,
    private val setCategoryPickedUseCase: SetCategoryPickedUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Onboarding is, by construction, the very first screen a user can ever open — same
            // idempotent seeding as Record/Settings init, see data/CLAUDE.md.
            ensureDefaultCategories()
            ensureDefaultCollections()
            recalculateFilterEligibility()
            preselectByDeviceRegion()
        }
        viewModelScope.launch {
            combine(
                collectionRepository.observeAll(),
                categoryRepository.observeAll(),
                collectionRepository.observeAllMemberships(),
            ) { collections, categories, memberships ->
                buildCollectionPickerItems(collections, categories, memberships)
            }.collect { items ->
                _uiState.update { it.copy(collectionPickerItems = items) }
            }
        }
    }

    /** Tri-state click convention: anything short of fully picked selects every member, only a
     * fully-picked collection deselects them all — mirrors [leshy.mushrooms.map.presentation.settings.SettingsViewModel]. */
    fun toggleCollection(item: CollectionPickerItem) {
        val picked = item.pickState != CollectionPickState.ALL
        viewModelScope.launch { setCollectionPickedUseCase(item.collection.id, picked) }
    }

    fun setCategoryPicked(category: Category, picked: Boolean) {
        viewModelScope.launch { setCategoryPickedUseCase(category, picked) }
    }

    /**
     * Plan §"Фаза 3" ("предвыбор по региону устройства при первом запуске") — pre-picks the
     * collection matching the device's region so a fresh install lands with a plausible starting
     * point instead of every species unpicked.
     *
     * Gated on "nothing is picked yet at all" rather than a dedicated one-shot flag: every catalog
     * species is seeded with `isPicked = false` (`EnsureDefaultCategoriesUseCase`), and nothing else
     * writes `isPicked = true` before the user reaches this screen, so a non-empty picked set can
     * only mean this already ran (or, in principle, a restored backup) — either way, re-forcing a
     * region pick on top of a state the user or a previous run already touched would be surprising.
     */
    private suspend fun preselectByDeviceRegion() {
        if (categoryRepository.getAll().any { it.isPicked }) return
        val regionCode = currentDeviceRegionCode() ?: return
        val collection = collectionRepository.getByNameKey(countryCollectionNameKey(regionCode)) ?: return
        setCollectionPickedUseCase(collection.id, true)
    }

    /**
     * Fire-and-forget is safe here: nothing tears this ViewModel down in direct response to this
     * call. [leshy.mushrooms.map.App] observes the same [OnboardingRepository] flag independently
     * and only swaps away from the onboarding screen once the write has actually landed and the
     * flow re-emits — see `.claude/plans/mushroom-collections.md`, Phase 3.
     */
    fun finish() {
        viewModelScope.launch { onboardingRepository.setCollectionPickerCompleted() }
    }
}
