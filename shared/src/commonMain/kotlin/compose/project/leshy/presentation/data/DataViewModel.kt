package compose.project.leshy.presentation.data

import androidx.lifecycle.ViewModel
import compose.project.leshy.data.platform.PickedLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds the Data screen's form state only — picking a folder/file and typing the archive
 * name/import tag. The actual export/import processing isn't implemented yet.
 */
class DataViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DataUiState())
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    fun setMode(mode: DataMode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun onExportFolderPicked(location: PickedLocation) {
        _uiState.update { it.copy(exportFolderName = location.displayName) }
    }

    fun setExportArchiveName(name: String) {
        _uiState.update { it.copy(exportArchiveName = name) }
    }

    fun onImportFilePicked(location: PickedLocation) {
        _uiState.update { it.copy(importFileName = location.displayName) }
    }

    fun setImportWalkLabel(label: String) {
        _uiState.update { it.copy(importWalkLabel = label) }
    }
}
