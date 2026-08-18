package compose.project.leshy.presentation.preparation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.repository.OfflineRegionRepository
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// How far below the zoom level the user is looking at when they tap "download" the minimum zoom
// is set — gives a few pre-cached zoomed-out fallback levels without inflating the tile count by
// downloading the whole world at low zoom.
private const val ZOOM_MIN_OFFSET = 4.0

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

    fun onDownloadCurrentViewClicked(west: Double, south: Double, east: Double, north: Double, currentZoom: Double) {
        val maxZoom = currentZoom.roundToInt()
        val minZoom = (currentZoom - ZOOM_MIN_OFFSET).roundToInt().coerceAtLeast(0)
        _uiState.update {
            it.copy(
                showNameDialog = true,
                nameInput = "",
                pendingSelection = PendingRegionSelection(west, south, east, north, minZoom, maxZoom),
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
}
