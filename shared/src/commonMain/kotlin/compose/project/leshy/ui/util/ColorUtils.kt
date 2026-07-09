package compose.project.leshy.ui.util

import androidx.compose.ui.graphics.Color

fun parseHexColor(hex: String, fallback: Color = Color.Gray): Color =
    runCatching { Color(("ff" + hex.removePrefix("#")).toLong(16)) }.getOrDefault(fallback)
