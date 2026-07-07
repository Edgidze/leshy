package compose.project.leshy.presentation.archive

import compose.project.leshy.domain.model.Walk

data class ArchiveUiState(
    val walks: List<Walk> = emptyList(),
)
