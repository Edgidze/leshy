package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeLanguage(): Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)

    /** Multiplier applied to the base mushroom marker icon size shown on maps. */
    fun observeMushroomMarkerSizeScale(): Flow<Float>
    suspend fun setMushroomMarkerSizeScale(scale: Float)
}
