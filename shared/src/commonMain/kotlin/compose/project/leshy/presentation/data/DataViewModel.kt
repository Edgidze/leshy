package compose.project.leshy.presentation.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.data.platform.ArchiveFileReader
import compose.project.leshy.data.platform.PickedLocation
import compose.project.leshy.data.platform.currentTimeMillis
import compose.project.leshy.domain.repository.WalkRepository
import compose.project.leshy.domain.usecase.BackfillWalkThumbnailsUseCase
import compose.project.leshy.domain.usecase.ExportDataUseCase
import compose.project.leshy.domain.usecase.ImportDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import okio.BufferedSink
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
    private val walkRepository: WalkRepository,
    private val backfillWalkThumbnails: BackfillWalkThumbnailsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DataUiState(exportArchiveName = defaultExportArchiveName(currentTimeMillis())),
    )
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    init {
        // One-shot, not observed continuously — the Data screen doesn't stream Room (see
        // `presentation/CLAUDE.md`/plan notes); a walk finished elsewhere while this screen is
        // open just won't show up in the picker until it's reopened, which is an acceptable edge
        // case for a short-lived one-off action screen. Defaults to "export everything" so the
        // picker button's count is meaningful even if the user never opens it.
        viewModelScope.launch {
            val walks = walkRepository.observeAll().first()
            _uiState.update { it.copy(availableWalks = walks, selectedWalkIds = walks.map { w -> w.id }.toSet()) }
        }
    }

    fun setMode(mode: DataMode) {
        _uiState.update { it.copy(mode = mode, errorMessage = null, exportSucceeded = false, importResult = null) }
    }

    fun setExportArchiveName(name: String) {
        _uiState.update { it.copy(exportArchiveName = name) }
    }

    fun openWalksPicker() {
        _uiState.update { it.copy(showWalksPicker = true) }
    }

    /** Back arrow in [compose.project.leshy.ui.components.WalksPickerDialog] — discards any
     * in-dialog toggling, selection stays whatever it was before the dialog opened. */
    fun dismissWalksPicker() {
        _uiState.update { it.copy(showWalksPicker = false) }
    }

    /** Checkmark in the walks picker — commits the new selection. Only an actual change to the
     * selection clears [DataUiState.exportSucceeded] — reopening the picker and confirming the
     * same set the archive was already exported with shouldn't un-stick the "Saved" button. */
    fun confirmWalksSelection(walkIds: Set<Long>) {
        _uiState.update {
            it.copy(
                selectedWalkIds = walkIds,
                showWalksPicker = false,
                exportSucceeded = it.exportSucceeded && walkIds == it.selectedWalkIds,
            )
        }
    }

    /** Called by [compose.project.leshy.data.platform.rememberExportLauncher] once it has a sink. */
    suspend fun writeExportArchive(sink: BufferedSink) {
        _uiState.update { it.copy(isProcessing = true, errorMessage = null, exportSucceeded = false) }
        exportDataUseCase(sink, _uiState.value.selectedWalkIds)
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
            // Imported walks land with thumbnailPath = null (see ImportDataUseCase) and are meant
            // to be repaired by ArchiveViewModel's one-shot backfill on its next init — but Archive
            // is a top-level destination reached via navigateToTopLevel(), so its ViewModel (and
            // that one-shot init) survives across tab switches (see ui/navigation/CLAUDE.md); if
            // the user had already opened Archive before importing, revisiting it afterwards
            // reuses the same ViewModel and never re-runs the backfill. Running it here too closes
            // that gap without depending on Archive's ViewModel lifecycle at all.
            if (result.getOrNull()?.importedWalkCount?.let { it > 0 } == true) backfillWalkThumbnails()
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

/** "leshy-export-20260818-1652.zip" — was raw epoch millis before, which read as a meaningless
 * number in the archive-name field; a local date+time is just as collision-safe for one export at
 * a time and actually says something to the person picking a save location. */
@OptIn(ExperimentalTime::class)
private fun defaultExportArchiveName(epochMillis: Long): String {
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    fun Int.pad() = toString().padStart(2, '0')
    return "leshy-export-${dt.year}${dt.month.number.pad()}${dt.day.pad()}-${dt.hour.pad()}${dt.minute.pad()}.zip"
}
