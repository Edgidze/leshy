package leshy.mushrooms.map.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import leshy.mushrooms.map.data.catalog.CountriesSource
import leshy.mushrooms.map.data.catalog.countryCodeForCollectionNameKey
import leshy.mushrooms.map.data.catalog.countryCollectionNameKey
import leshy.mushrooms.map.data.platform.currentDeviceRegionCode
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository
import leshy.mushrooms.map.domain.repository.OnboardingRepository
import leshy.mushrooms.map.domain.repository.SettingsRepository
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
    private val settingsRepository: SettingsRepository,
    private val countriesSource: CountriesSource,
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
                settingsRepository.observeLanguage(),
            ) { collections, categories, memberships, language ->
                language to buildCollectionPickerItems(collections, categories, memberships)
            }.collect { (language, items) ->
                _uiState.update {
                    it.copy(language = language, collectionPickerItems = sortByLanguage(items, language))
                }
            }
        }
    }

    /**
     * Countries that speak the language picked on the previous step float to the top; everything
     * else keeps the ordinary [leshy.mushrooms.map.domain.model.Collection.order] below them. This
     * is a default ORDER, not a filter — every one of the 40 countries is still in the list, and
     * the search field still finds any of them.
     *
     * Inside the floated group, the countries the language is *titular* in come first — those whose
     * [leshy.mushrooms.map.data.catalog.CountryEntry.langs] starts with it. Without that split the
     * group is ordered by country code, and picking Russian would put `RU` ninth, below `AM`, `AZ`,
     * `BY`, `EE`, `KG`, `KZ`, `LV`, `MD` — every country where Russian is merely widely spoken.
     * That was already slightly wrong at 6 such countries and became clearly wrong at 13, when
     * `.claude/plans/post-soviet-countries.md` added Russian to all seven Central-Asian and
     * Caucasian presets. The same split does the right thing for every other language too: `uk`
     * puts `UA` first, `kk` puts `KZ` first.
     *
     * Only the onboarding step reorders like this. The same picker on the "Грибы" screen keeps the
     * plain alphabetical/catalog order: there, the user is looking for a specific country they
     * already have in mind, and a list that silently reshuffles itself around the interface
     * language would just make it harder to find.
     */
    private fun sortByLanguage(
        items: List<CollectionPickerItem>,
        language: AppLanguage,
    ): List<CollectionPickerItem> {
        val speaking = countriesSource.entries.filter { language.code in it.langs }
        if (speaking.isEmpty()) return items
        val titularCodes = speaking.filter { it.langs.firstOrNull() == language.code }
            .map { it.code }
            .toSet()
        val otherCodes = speaking.map { it.code }.toSet() - titularCodes

        // partition preserves the relative order inside each half, so each group stays in the
        // collections' own order and the untouched tail keeps it too.
        val (titular, rest) = items.partition { item ->
            countryCodeForCollectionNameKey(item.collection.nameKey) in titularCodes
        }
        val (alsoSpoken, others) = rest.partition { item ->
            countryCodeForCollectionNameKey(item.collection.nameKey) in otherCodes
        }
        return titular + alsoSpoken + others
    }

    /** Step 1's confirm: applies the language app-wide (Settings' own picker writes the same key)
     * and moves on to the countries. */
    fun onLanguageConfirmed(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
        _uiState.update { it.copy(step = OnboardingStep.COLLECTIONS) }
    }

    /** Step 2's back arrow — the language choice is already applied, so this is a real "let me
     * change it", not a cancel. */
    fun onBackToLanguage() {
        _uiState.update { it.copy(step = OnboardingStep.LANGUAGE) }
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
