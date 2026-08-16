package compose.project.leshy.presentation.settings

import compose.project.leshy.domain.model.AppLanguage

data class SettingsUiState(
    val language: AppLanguage = AppLanguage.EN,
)
