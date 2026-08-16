package compose.project.leshy.data.platform

import androidx.compose.ui.platform.ClipEntry

/** Wraps [text] as a plain-text [ClipEntry] for [androidx.compose.ui.platform.LocalClipboard]. */
expect fun plainTextClipEntry(text: String): ClipEntry
