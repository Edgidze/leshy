package compose.project.leshy.presentation.preparation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.repository.OfflineRegionRepository
import compose.project.leshy.domain.util.estimateOfflineRegion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PreparationViewModel(
    private val repository: OfflineRegionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreparationUiState())
    val uiState: StateFlow<PreparationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeRegions().collect { regions ->
                _uiState.update { it.copy(regions = regions) }
            }
        }
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
                    estimatedBytes = estimate.estimatedBytes,
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
