package leshy.mushrooms.map.presentation.preparation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import leshy.mushrooms.map.data.platform.LocationTracker
import leshy.mushrooms.map.domain.repository.OfflineRegionRepository
import leshy.mushrooms.map.domain.util.estimateOfflineRegion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PreparationViewModel(
    private val repository: OfflineRegionRepository,
    private val locationTracker: LocationTracker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreparationUiState())
    val uiState: StateFlow<PreparationUiState> = _uiState.asStateFlow()

    /**
     * Гейт подписки на GPS — ровно тот же приём и по той же причине, что на «Записи»: приёмник
     * работает, пока экран перед пользователем, и не работает, когда его показания некому смотреть.
     * Здесь это даже строже: у «Подготовки» нет случая «запись идёт в фоне», ради которого там
     * сделано исключение, — уйти с этого экрана значит перестать нуждаться в координате совсем.
     */
    private val isScreenResumed = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.observeRegions().collect { regions ->
                _uiState.update { it.copy(regions = regions) }
            }
        }
        viewModelScope.launch {
            isScreenResumed
                .flatMapLatest { resumed -> if (resumed) locationTracker.track() else emptyFlow() }
                // Экрану нужна ТОЛЬКО координата: ни курс, ни скорость здесь не рисуются, а
                // приходят они в том же объекте фикса и меняются на каждом. Без этого камера и
                // точка пересобирались бы на каждый фикс неподвижного телефона.
                .distinctUntilChanged { old, new -> old.point == new.point }
                .collect { fix -> _uiState.update { it.copy(currentLocation = fix.point) } }
        }
    }

    fun onScreenResumed() {
        isScreenResumed.value = true
    }

    fun onScreenPaused() {
        isScreenResumed.value = false
    }

    // Detail (zoom range) is never something the user chooses or sees — see PreparationScreen.kt.
    // They only draw an area; estimateOfflineRegion picks a detail level automatically, coarser for
    // larger areas, so no selection can blow up into an unbounded download.
    fun onAreaSelected(west: Double, south: Double, east: Double, north: Double) {
        val estimate = estimateOfflineRegion(west, south, east, north)
        _uiState.update {
            it.copy(
                showNameDialog = true,
                nameInput = "",
                pendingSelection = PendingRegionSelection(
                    west = west,
                    south = south,
                    east = east,
                    north = north,
                    minZoom = estimate.minZoom,
                    maxZoom = estimate.maxZoom,
                ),
            )
        }
    }

    fun onNameInputChanged(name: String) {
        _uiState.update { it.copy(nameInput = name) }
    }

    fun onNameConfirmed() {
        val selection = _uiState.value.pendingSelection ?: return
        val name = _uiState.value.nameInput.trim()
        // Regions are identified by name (see OfflineRegionRepositoryImpl — packs have no id of
        // their own), and the region list is keyed by name in PreparationScreen's LazyColumn, so a
        // duplicate name would both crash that list and make pause/resume/delete ambiguous.
        if (name.isEmpty() || _uiState.value.regions.any { it.name == name }) return
        viewModelScope.launch {
            runCatching {
                repository.downloadRegion(
                    name = name,
                    west = selection.west,
                    south = selection.south,
                    east = selection.east,
                    north = selection.north,
                    minZoom = selection.minZoom,
                    maxZoom = selection.maxZoom,
                )
            }
        }
        _uiState.update { it.copy(showNameDialog = false, pendingSelection = null, nameInput = "") }
    }

    fun onNameDialogDismissed() {
        _uiState.update { it.copy(showNameDialog = false, pendingSelection = null, nameInput = "") }
    }

    fun onPauseClicked(name: String) {
        repository.pause(name)
    }

    fun onResumeClicked(name: String) {
        repository.resume(name)
    }

    fun onDeleteRequested(name: String) {
        _uiState.update { it.copy(regionPendingDelete = name) }
    }

    fun onDeleteConfirmed() {
        val name = _uiState.value.regionPendingDelete ?: return
        viewModelScope.launch { repository.delete(name) }
        _uiState.update { it.copy(regionPendingDelete = null) }
    }

    fun onDeleteDismissed() {
        _uiState.update { it.copy(regionPendingDelete = null) }
    }

    fun onRetryClicked(name: String) {
        val region = _uiState.value.regions.firstOrNull { it.name == name } ?: return
        viewModelScope.launch {
            // The failed pack has to go first — downloadRegion always creates a new pack, and a
            // second pack under the same metadata name would make findPack's name lookup (and the
            // LazyColumn key in PreparationScreen) ambiguous, same hazard as the duplicate-name
            // guard in onNameConfirmed.
            repository.delete(name)
            runCatching {
                repository.downloadRegion(
                    name = region.name,
                    west = region.west,
                    south = region.south,
                    east = region.east,
                    north = region.north,
                    minZoom = region.minZoom,
                    maxZoom = region.maxZoom,
                )
            }
        }
    }
}
