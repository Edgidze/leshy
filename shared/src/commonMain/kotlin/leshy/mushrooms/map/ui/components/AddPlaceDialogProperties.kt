package leshy.mushrooms.map.ui.components

import androidx.compose.ui.window.DialogProperties

/**
 * [DialogProperties] for [AddPlaceDialog]'s full-screen [androidx.compose.ui.window.Dialog].
 * `decorFitsSystemWindows` is an Android-only constructor parameter (absent from the common
 * `DialogProperties` expect class, so unavailable in commonMain/iosMain) required alongside
 * `usePlatformDefaultWidth = false` — without it the window keeps
 * `SOFT_INPUT_ADJUST_UNSPECIFIED` and `LocalSoftwareKeyboardController.hide()` silently no-ops.
 */
expect fun addPlaceDialogProperties(): DialogProperties
