package compose.project.leshy.presentation.settings

import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category

data class SettingsUiState(
    val language: AppLanguage = AppLanguage.EN,
    val categories: List<Category> = emptyList(),
)
