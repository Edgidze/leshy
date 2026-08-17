package compose.project.leshy.ui.components

import androidx.compose.ui.window.DialogProperties

actual fun addPlaceDialogProperties(): DialogProperties = DialogProperties(
    usePlatformDefaultWidth = false,
    dismissOnBackPress = true,
)
