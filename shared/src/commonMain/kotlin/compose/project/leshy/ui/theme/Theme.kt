package compose.project.leshy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LeshyGreen = Color(0xFF1B4332)
private val LeshyGreenLight = Color(0xFF40916C)

private val LightColors = lightColorScheme(
    primary = LeshyGreen,
    secondary = LeshyGreenLight,
)

private val DarkColors = darkColorScheme(
    primary = LeshyGreenLight,
    secondary = LeshyGreen,
)

@Composable
fun LeshyTheme(useDarkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors,
        content = content,
    )
}
