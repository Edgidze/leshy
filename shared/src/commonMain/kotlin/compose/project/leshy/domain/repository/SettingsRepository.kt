package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeLanguage(): Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)
}
