package compose.project.leshy.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.MushroomSortOrder
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.CollectionRepository
import compose.project.leshy.domain.repository.SettingsRepository
import compose.project.leshy.domain.usecase.EnsureDefaultCategoriesUseCase
import compose.project.leshy.domain.usecase.EnsureDefaultCollectionsUseCase
import compose.project.leshy.domain.usecase.RecalculateFilterEligibilityUseCase
import compose.project.leshy.domain.usecase.RefreshMapDataUseCase
import compose.project.leshy.domain.usecase.SetCategoryPickedUseCase
import compose.project.leshy.domain.usecase.SetCollectionPickedUseCase
import compose.project.leshy.presentation.CollectionPickState
import compose.project.leshy.presentation.CollectionPickerItem
import compose.project.leshy.presentation.buildCollectionPickerItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository,
    private val ensureDefaultCategories: EnsureDefaultCategoriesUseCase,
    private val ensureDefaultCollections: EnsureDefaultCollectionsUseCase,
    private val setCollectionPickedUseCase: SetCollectionPickedUseCase,
    private val setCategoryPickedUseCase: SetCategoryPickedUseCase,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
    private val refreshMapDataUseCase: RefreshMapDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Settings can be the very first screen a user ever opens — Record's init isn't
            // guaranteed to have run yet, so the catalog/collections need seeding here too.
            // Idempotent upsert-diff, safe to call from both places.
            ensureDefaultCategories()
            ensureDefaultCollections()
            recalculateFilterEligibility()
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
        viewModelScope.launch {
            settingsRepository.observeLanguage().collect { language ->
                _uiState.update { it.copy(language = language) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observeMushroomMarkerSizeScale().collect { scale ->
                _uiState.update { it.copy(mushroomMarkerSizeScale = scale) }
            }
        }
        viewModelScope.launch {
            val preview = categoryRepository.observeAll().first().filter { it.iconRef != null }.randomOrNull()
            _uiState.update { it.copy(previewCategory = preview) }
        }
        viewModelScope.launch {
            settingsRepository.observeMushroomSortOrder().collect { sortOrder ->
                _uiState.update { it.copy(mushroomSortOrder = sortOrder) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observeResetMushroomOrderOnWalkFinish().collect { reset ->
                _uiState.update { it.copy(resetMushroomOrderOnWalkFinish = reset) }
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }

    fun setMushroomMarkerSizeScale(scale: Float) {
        viewModelScope.launch { settingsRepository.setMushroomMarkerSizeScale(scale) }
    }

    fun setMushroomSortOrder(sortOrder: MushroomSortOrder) {
        viewModelScope.launch { settingsRepository.setMushroomSortOrder(sortOrder) }
    }

    fun setResetMushroomOrderOnWalkFinish(reset: Boolean) {
        viewModelScope.launch { settingsRepository.setResetMushroomOrderOnWalkFinish(reset) }
    }

    /** Tri-state click convention: anything short of fully picked selects every member, only a
     * fully-picked collection deselects them all. */
    fun toggleCollection(item: CollectionPickerItem) {
        val picked = item.pickState != CollectionPickState.ALL
        viewModelScope.launch { setCollectionPickedUseCase(item.collection.id, picked) }
    }

    fun setCategoryPicked(category: Category, picked: Boolean) {
        viewModelScope.launch { setCategoryPickedUseCase(category, picked) }
    }

    /** Explicit-only re-fetch of the pinned map style — see `MapStyleCacheRepository`'s doc for
     * why this never happens automatically beyond the very first launch. When the fetched style
     * actually changed, [RefreshMapDataUseCase] has already deleted and re-queued every
     * previously-downloaded offline region by the time this returns — [mapDataRegionsRedownloading]
     * just reports how many, for the one-off notice in Settings. */
    fun refreshMapData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isRefreshingMapData = true, mapDataRefreshFailed = false, mapDataRegionsRedownloading = 0)
            }
            val result = refreshMapDataUseCase()
            _uiState.update {
                it.copy(
                    isRefreshingMapData = false,
                    mapDataRefreshFailed = !result.success,
                    mapDataRegionsRedownloading = result.regionsRedownloading,
                )
            }
        }
    }
}
