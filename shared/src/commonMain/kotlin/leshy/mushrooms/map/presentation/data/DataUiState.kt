package leshy.mushrooms.map.presentation.data

import leshy.mushrooms.map.domain.model.Walk
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
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
)
