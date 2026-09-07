package leshy.mushrooms.map.presentation.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import leshy.mushrooms.map.data.platform.ArchiveFileReader
import leshy.mushrooms.map.data.platform.PickedLocation
import leshy.mushrooms.map.data.platform.currentTimeMillis
import leshy.mushrooms.map.domain.repository.WalkRepository
import leshy.mushrooms.map.domain.usecase.ExportDataUseCase
import leshy.mushrooms.map.domain.usecase.ImportDataUseCase
import leshy.mushrooms.map.domain.usecase.ImportArchiveProblem
import leshy.mushrooms.map.domain.usecase.ValidateImportArchiveUseCase
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
 * to/come from is entirely the platform pickers' job ([leshy.mushrooms.map.data.platform
 * .rememberExportLauncher]/[ArchiveFileReader] — Android `ContentResolver`, iOS sandboxed file
 * path) so this class stays free of platform dependencies: it only calls [ExportDataUseCase]/
 * [ImportDataUseCase] and reports the result.
 */
class DataViewModel(
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val validateImportArchive: ValidateImportArchiveUseCase,
    private val archiveFileReader: ArchiveFileReader,
    private val walkRepository: WalkRepository,
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

    /** Back arrow in [leshy.mushrooms.map.ui.components.WalksPickerDialog] — discards any
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

    /** Called by [leshy.mushrooms.map.data.platform.rememberExportLauncher] once it has a sink. */
    suspend fun writeExportArchive(sink: BufferedSink) {
        _uiState.update { it.copy(isProcessing = true, errorMessage = null, exportSucceeded = false) }
        exportDataUseCase(sink, _uiState.value.selectedWalkIds)
    }

    fun onExportResult(result: Result<Unit>) {
        _uiState.update {
            it.copy(isProcessing = false, exportSucceeded = result.isSuccess, errorMessage = result.exceptionOrNull()?.message)
        }
    }

    /**
     * Checks the file the moment it is picked, not when "Import" is pressed — so an unusable file
     * is named as such right away instead of after the user has typed a label and committed. The
     * archive is read twice in the happy path (once here, once on import); that is a local file
     * read, and it buys a verdict before anything is written.
     */
    fun onImportFilePicked(location: PickedLocation) {
        _uiState.update {
            it.copy(
                importFileName = location.displayName,
                importFileHandle = location.handle,
                errorMessage = null,
                importResult = null,
                importProblem = null,
                importProblemDialogVisible = false,
                isProcessing = true,
            )
        }
        viewModelScope.launch {
            val problem = runCatching { validateImportArchive(archiveFileReader.readBytes(location.handle)) }
                // Unreadable file (permission lost, deleted between picking and reading) is, from
                // the user's point of view, the same answer as "not an archive".
                .getOrDefault(ImportArchiveProblem.NOT_AN_ARCHIVE)
            _uiState.update {
                it.copy(isProcessing = false, importProblem = problem, importProblemDialogVisible = problem != null)
            }
        }
    }

    /** Closes the dialog only — [DataUiState.importProblem] survives, so the import button stays
     * disabled and the reason stays readable until a different file is picked. */
    fun dismissImportProblem() {
        _uiState.update { it.copy(importProblemDialogVisible = false) }
    }

    fun setImportWalkLabel(label: String) {
        _uiState.update { it.copy(importWalkLabel = label) }
    }

    fun confirmImport() {
        val state = _uiState.value
        val handle = state.importFileHandle ?: return
        // Belt and braces: the button is disabled while importProblem != null, but the use case
        // re-validates internally anyway and would throw RejectedException rather than write.
        if (state.importProblem != null) return
        _uiState.update { it.copy(isProcessing = true, errorMessage = null, importResult = null) }
        viewModelScope.launch {
            val result = runCatching { importDataUseCase(archiveFileReader.readBytes(handle), state.importWalkLabel) }
            // Импорт закончен — и колёсико гаснет ЗДЕСЬ. Миниатюры приехавших прогулок сюда не
            // входят и входить не должны: они рисуются снимками карты по сети, по одному на
            // прогулку, и на архиве в сотню прогулок это минуты сверху — минуты, в которые
            // приложение показывало «Идёт обработка…», хотя все треки, находки и фотографии
            // давно лежали в базе (репорт 2026-09-07). Без сети это к тому же не кончалось бы
            // ничем: снимков не будет в любом случае, а импорт при этом прошёл успешно.
            //
            // Отрисовку забирает «Архив» — [BackfillWalkThumbnailsUseCase] запускается на КАЖДОМ
            // входе на экран (`ArchiveViewModel.onScreenShown`), а не однажды в `init`, поэтому
            // приехавшие прогулки подхватываются и тогда, когда архив уже открывали до импорта и
            // его ViewModel пережил переключение разделов (см. `ui/navigation/CLAUDE.md`). Пока
            // снимок не готов — в карточке силуэт маршрута (`WalkRouteThumbnail`), нормальный
            // результат, а не полупустой экран.
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
                importProblem = null,
                importProblemDialogVisible = false,
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
