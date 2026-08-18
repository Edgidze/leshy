package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.MushroomSortOrder
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeLanguage(): Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)

    /** Multiplier applied to the base mushroom marker icon size shown on maps. */
    fun observeMushroomMarkerSizeScale(): Flow<Float>
    suspend fun setMushroomMarkerSizeScale(scale: Float)

    /** Order mushroom tiles are listed in on the Record screen (and anywhere else the catalog is listed). */
    fun observeMushroomSortOrder(): Flow<MushroomSortOrder>
    suspend fun setMushroomSortOrder(sortOrder: MushroomSortOrder)

    /**
     * Whether the Record screen's tile feed should drop its "most recently tapped first" order
     * and fall back to [MushroomSortOrder] once a walk finishes. Off by default — the feed order
     * built up during a walk carries over into the next one.
     */
    fun observeResetMushroomOrderOnWalkFinish(): Flow<Boolean>
    suspend fun setResetMushroomOrderOnWalkFinish(reset: Boolean)
}
