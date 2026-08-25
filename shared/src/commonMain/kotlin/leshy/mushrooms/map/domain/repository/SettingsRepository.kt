package leshy.mushrooms.map.domain.repository

import leshy.mushrooms.map.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeLanguage(): Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)

    /** Multiplier applied to the base mushroom marker icon size shown on maps. */
    fun observeMushroomMarkerSizeScale(): Flow<Float>
    suspend fun setMushroomMarkerSizeScale(scale: Float)

    /**
     * Whether the Record screen's tile feed should drop its "most recently tapped first" order
     * and fall back to the catalog's alphabetical order once a walk finishes. Off by default — the
     * feed order built up during a walk carries over into the next one.
     */
    fun observeResetMushroomOrderOnWalkFinish(): Flow<Boolean>
    suspend fun setResetMushroomOrderOnWalkFinish(reset: Boolean)

    /**
     * Whether the Record screen's tile feed should stop bumping a tile to the front when it's
     * tapped (+/-) — the feed stays in its alphabetical order regardless of new finds. Off by
     * default. Doesn't affect the deliberate jump-to-tile from the search dialog or right after
     * creating a new species — those aren't "reordering because of a new find", they're a jump to
     * a tile the user just explicitly picked.
     */
    fun observeFreezeMushroomOrder(): Flow<Boolean>
    suspend fun setFreezeMushroomOrder(freeze: Boolean)
}
