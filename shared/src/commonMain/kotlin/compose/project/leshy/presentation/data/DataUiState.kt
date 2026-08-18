package compose.project.leshy.presentation.data

enum class DataMode {
    EXPORT,
    IMPORT,
}

data class DataUiState(
    val mode: DataMode = DataMode.EXPORT,
    val exportFolderName: String? = null,
    val exportArchiveName: String = "",
    val importFileName: String? = null,
    val importWalkLabel: String = "",
)
