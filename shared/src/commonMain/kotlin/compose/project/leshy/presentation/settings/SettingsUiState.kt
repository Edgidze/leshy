package compose.project.leshy.presentation.settings

import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_DEFAULT

data class SettingsUiState(
    val language: AppLanguage = AppLanguage.EN,
    val mushroomMarkerSizeScale: Float = MUSHROOM_MARKER_SIZE_SCALE_DEFAULT,
    val resetMushroomOrderOnWalkFinish: Boolean = false,
    val freezeMushroomOrder: Boolean = false,
    /** One randomly picked species, shown at the current [mushroomMarkerSizeScale] so sizing the
     * marker is visual instead of abstract — picked once (stable for the screen's lifetime, not
     * reshuffled by an unrelated categories update) rather than on every recomposition. */
    val previewCategory: Category? = null,
    val isRefreshingMapData: Boolean = false,
    val mapDataRefreshFailed: Boolean = false,
    /** > 0 right after a refresh whose fetched style content actually changed — that many
     * previously-downloaded offline regions were automatically deleted and re-queued for
     * download. Cleared back to 0 at the start of the next refresh. */
    val mapDataRegionsRedownloading: Int = 0,
    val isClearingMapCache: Boolean = false,
    /** True right after a successful [SettingsViewModel.onClearMapCacheConfirm] — one-off confirmation
     * text, same idiom as [mapDataRegionsRedownloading]. */
    val mapCacheCleared: Boolean = false,
    val showUpdateMapDataConfirm: Boolean = false,
    val showClearMapCacheConfirm: Boolean = false,
)
