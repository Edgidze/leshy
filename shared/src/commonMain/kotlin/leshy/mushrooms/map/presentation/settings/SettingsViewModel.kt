package leshy.mushrooms.map.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import leshy.mushrooms.map.domain.model.iconSource
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.OfflineRegionRepository
import leshy.mushrooms.map.domain.repository.SettingsRepository
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCategoriesUseCase
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCollectionsUseCase
import leshy.mushrooms.map.domain.usecase.RecalculateFilterEligibilityUseCase
import leshy.mushrooms.map.domain.usecase.RefreshMapDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val categoryRepository: CategoryRepository,
    private val ensureDefaultCategories: EnsureDefaultCategoriesUseCase,
    private val ensureDefaultCollections: EnsureDefaultCollectionsUseCase,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
    private val refreshMapDataUseCase: RefreshMapDataUseCase,
    private val offlineRegionRepository: OfflineRegionRepository,
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
            val preview = categoryRepository.observeAll().first().filter { it.iconSource() != null }.randomOrNull()
            _uiState.update { it.copy(previewCategory = preview) }
        }
        viewModelScope.launch {
            settingsRepository.observeResetMushroomOrderOnWalkFinish().collect { reset ->
                _uiState.update { it.copy(resetMushroomOrderOnWalkFinish = reset) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observeFreezeMushroomOrder().collect { freeze ->
                _uiState.update { it.copy(freezeMushroomOrder = freeze) }
            }
        }
    }

    fun setMushroomMarkerSizeScale(scale: Float) {
        viewModelScope.launch { settingsRepository.setMushroomMarkerSizeScale(scale) }
    }

    fun setResetMushroomOrderOnWalkFinish(reset: Boolean) {
        viewModelScope.launch { settingsRepository.setResetMushroomOrderOnWalkFinish(reset) }
    }

    fun setFreezeMushroomOrder(freeze: Boolean) {
        viewModelScope.launch { settingsRepository.setFreezeMushroomOrder(freeze) }
    }

    fun onUpdateMapDataClick() {
        _uiState.update { it.copy(showUpdateMapDataConfirm = true) }
    }

    fun onUpdateMapDataDismiss() {
        _uiState.update { it.copy(showUpdateMapDataConfirm = false) }
    }

    /** Explicit-only re-fetch of the pinned map style — see `MapStyleCacheRepository`'s doc for
     * why this never happens automatically beyond the very first launch. When the fetched style
     * actually changed, [RefreshMapDataUseCase] has already deleted and re-queued every
     * previously-downloaded offline region by the time this returns — [mapDataRegionsRedownloading]
     * just reports how many, for the one-off notice in Settings. */
    fun onUpdateMapDataConfirm() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showUpdateMapDataConfirm = false,
                    isRefreshingMapData = true,
                    mapDataRefreshFailed = false,
                    mapDataRegionsRedownloading = 0,
                )
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

    fun onClearMapCacheClick() {
        _uiState.update { it.copy(showClearMapCacheConfirm = true) }
    }

    fun onClearMapCacheDismiss() {
        _uiState.update { it.copy(showClearMapCacheConfirm = false) }
    }

    /** Diagnostic tool, not a routine action — clears MapLibre's ambient (browsing) cache so a
     * downloaded offline region's actual coverage can be tested in isolation (ambient-cached tiles
     * render offline exactly like a region's own tiles do, and are otherwise indistinguishable from
     * outside the app). Never touches downloaded regions themselves — see
     * [OfflineRegionRepository.clearAmbientCache]'s doc. */
    fun onClearMapCacheConfirm() {
        viewModelScope.launch {
            _uiState.update { it.copy(showClearMapCacheConfirm = false, isClearingMapCache = true, mapCacheCleared = false) }
            val cleared = runCatching { offlineRegionRepository.clearAmbientCache() }.isSuccess
            _uiState.update { it.copy(isClearingMapCache = false, mapCacheCleared = cleared) }
        }
    }
}
