package leshy.mushrooms.map.presentation.data

import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.domain.usecase.ImportArchiveProblem
import leshy.mushrooms.map.domain.usecase.ImportDataUseCase

enum class DataMode {
    EXPORT,
    IMPORT,
}

data class DataUiState(
    val mode: DataMode = DataMode.EXPORT,
    val exportArchiveName: String = "",
    val exportSucceeded: Boolean = false,
    val availableWalks: List<Walk> = emptyList(),
    val selectedWalkIds: Set<Long> = emptySet(),
    val showWalksPicker: Boolean = false,
    val importFileName: String? = null,
    val importFileHandle: String? = null,
    val importWalkLabel: String = "",
    val importResult: ImportDataUseCase.Result? = null,
    /** Non-null once the picked file has been checked and found unusable. The verdict itself, kept
     * until another file is picked: dismissing the dialog closes the dialog, it does not make an
     * unusable file importable. */
    val importProblem: ImportArchiveProblem? = null,
    /** Whether the explanation is currently up as a dialog; the same reason also stays on screen as
     * a line of text underneath, so dismissing it doesn't leave a disabled button with no reason. */
    val importProblemDialogVisible: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
)
