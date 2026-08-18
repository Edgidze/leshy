package compose.project.leshy.presentation.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.data.platform.ArchiveFileReader
import compose.project.leshy.data.platform.PickedLocation
import compose.project.leshy.data.platform.currentTimeMillis
import compose.project.leshy.domain.usecase.ExportDataUseCase
import compose.project.leshy.domain.usecase.ImportDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.BufferedSink

/**
 * Holds the Data screen's state and drives export/import. Where the archive bytes actually go
 * to/come from is entirely the platform pickers' job ([compose.project.leshy.data.platform
 * .rememberExportLauncher]/[ArchiveFileReader] — Android `ContentResolver`, iOS sandboxed file
 * path) so this class stays free of platform dependencies: it only calls [ExportDataUseCase]/
 * [ImportDataUseCase] and reports the result.
 */
class DataViewModel(
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val archiveFileReader: ArchiveFileReader,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DataUiState(exportArchiveName = "leshy-export-${currentTimeMillis()}.zip"),
    )
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    fun setMode(mode: DataMode) {
        _uiState.update { it.copy(mode = mode, errorMessage = null, exportSucceeded = false, importResult = null) }
    }

    fun setExportArchiveName(name: String) {
        _uiState.update { it.copy(exportArchiveName = name) }
    }

    /** Called by [compose.project.leshy.data.platform.rememberExportLauncher] once it has a sink. */
    suspend fun writeExportArchive(sink: BufferedSink) {
        _uiState.update { it.copy(isProcessing = true, errorMessage = null, exportSucceeded = false) }
        exportDataUseCase(sink)
    }

    fun onExportResult(result: Result<Unit>) {
        _uiState.update {
            it.copy(isProcessing = false, exportSucceeded = result.isSuccess, errorMessage = result.exceptionOrNull()?.message)
        }
    }

    fun onImportFilePicked(location: PickedLocation) {
        _uiState.update {
            it.copy(
                importFileName = location.displayName,
                importFileHandle = location.handle,
                errorMessage = null,
                importResult = null,
            )
        }
    }

    fun setImportWalkLabel(label: String) {
        _uiState.update { it.copy(importWalkLabel = label) }
    }

    fun confirmImport() {
        val state = _uiState.value
        val handle = state.importFileHandle ?: return
        _uiState.update { it.copy(isProcessing = true, errorMessage = null, importResult = null) }
        viewModelScope.launch {
            val result = runCatching { importDataUseCase(archiveFileReader.readBytes(handle), state.importWalkLabel) }
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    importResult = result.getOrNull(),
                    errorMessage = result.exceptionOrNull()?.message,
                )
            }
        }
    }

    fun cancel() {
        _uiState.update {
            it.copy(
                importFileName = null,
                importFileHandle = null,
                importWalkLabel = "",
                importResult = null,
                exportSucceeded = false,
                errorMessage = null,
            )
        }
    }
}
