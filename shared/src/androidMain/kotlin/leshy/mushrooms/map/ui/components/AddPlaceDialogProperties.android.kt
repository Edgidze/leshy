package leshy.mushrooms.map.ui.components

import androidx.compose.ui.window.DialogProperties

actual fun addPlaceDialogProperties(): DialogProperties = DialogProperties(
    usePlatformDefaultWidth = false,
    dismissOnBackPress = true,
    decorFitsSystemWindows = false,
)
