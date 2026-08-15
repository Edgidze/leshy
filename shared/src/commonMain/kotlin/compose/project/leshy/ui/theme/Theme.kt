package compose.project.leshy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LeshyGreen = Color(0xFF1B4332)

// Earthy forest palette anchored on the logo green (UI_REVIEW.md #5) — replaces
// the Material3 baseline lavender/purple that unspecified lightColorScheme()/
// darkColorScheme() params fall back to.
private val LightColors = lightColorScheme(
    primary = LeshyGreen,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA8D5BA),
    onPrimaryContainer = Color(0xFF06261A),
    inversePrimary = Color(0xFF8DCFA9),
    secondary = Color(0xFF6F4E37),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEAD9C8),
    onSecondaryContainer = Color(0xFF2B1B0F),
    tertiary = Color(0xFF6B7A3F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDCE6C0),
    onTertiaryContainer = Color(0xFF263300),
    background = Color(0xFFF4F1E8),
    onBackground = Color(0xFF201C15),
    surface = Color(0xFFF4F1E8),
    onSurface = Color(0xFF201C15),
    surfaceVariant = Color(0xFFE3DCC8),
    onSurfaceVariant = Color(0xFF4C4739),
    surfaceTint = LeshyGreen,
    inverseSurface = Color(0xFF34302A),
    inverseOnSurface = Color(0xFFF5F0E7),
    outline = Color(0xFF7C7666),
    outlineVariant = Color(0xFFCDC6B4),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFF4F1E8),
    surfaceDim = Color(0xFFD8D3C4),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEEEADD),
    surfaceContainer = Color(0xFFE8E3D4),
    surfaceContainerHigh = Color(0xFFE2DDCE),
    surfaceContainerHighest = Color(0xFFDCD7C8),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8DCFA9),
    onPrimary = Color(0xFF08361F),
    primaryContainer = LeshyGreen,
    onPrimaryContainer = Color(0xFFA8D5BA),
    inversePrimary = LeshyGreen,
    secondary = Color(0xFFD8C0A8),
    onSecondary = Color(0xFF3E2B1A),
    secondaryContainer = Color(0xFF56402C),
    onSecondaryContainer = Color(0xFFEAD9C8),
    tertiary = Color(0xFFC0CC9E),
    onTertiary = Color(0xFF2C3300),
    tertiaryContainer = Color(0xFF414A20),
    onTertiaryContainer = Color(0xFFDCE6C0),
    background = Color(0xFF1C1B15),
    onBackground = Color(0xFFE6E1D3),
    surface = Color(0xFF1C1B15),
    onSurface = Color(0xFFE6E1D3),
    surfaceVariant = Color(0xFF4C4739),
    onSurfaceVariant = Color(0xFFCDC6B4),
    surfaceTint = Color(0xFF8DCFA9),
    inverseSurface = Color(0xFFE6E1D3),
    inverseOnSurface = Color(0xFF34302A),
    outline = Color(0xFF96907E),
    outlineVariant = Color(0xFF4C4739),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF43413A),
    surfaceDim = Color(0xFF1C1B15),
    surfaceContainerLowest = Color(0xFF16150F),
    surfaceContainerLow = Color(0xFF201F19),
    surfaceContainer = Color(0xFF24231C),
    surfaceContainerHigh = Color(0xFF2F2E26),
    surfaceContainerHighest = Color(0xFF3A3831),
)

@Composable
fun LeshyTheme(useDarkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors,
        content = content,
    )
}
