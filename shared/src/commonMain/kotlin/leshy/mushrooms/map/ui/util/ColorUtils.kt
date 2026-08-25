package leshy.mushrooms.map.ui.util

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

fun parseHexColor(hex: String, fallback: Color = Color.Gray): Color =
    runCatching { Color(("ff" + hex.removePrefix("#")).toLong(16)) }.getOrDefault(fallback)

fun colorToHex(color: Color): String {
    fun channel(value: Float) = (value * 255f).roundToInt().coerceIn(0, 255).toString(16).padStart(2, '0')
    return "#${channel(color.red)}${channel(color.green)}${channel(color.blue)}"
}

/** Hue in degrees `[0, 360)`, the only component the species color spectrum picker
 * (`SpeciesFormDialog`) varies — saturation/value are fixed constants there, so this is what's
 * needed to seed the slider position from a stored/detected color. */
fun hueOf(color: Color): Float {
    val max = maxOf(color.red, color.green, color.blue)
    val min = minOf(color.red, color.green, color.blue)
    val delta = max - min
    if (delta == 0f) return 0f
    val hue = when (max) {
        color.red -> 60f * (((color.green - color.blue) / delta).mod(6f))
        color.green -> 60f * (((color.blue - color.red) / delta) + 2f)
        else -> 60f * (((color.red - color.green) / delta) + 4f)
    }
    return if (hue < 0f) hue + 360f else hue
}
