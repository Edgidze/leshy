package leshy.mushrooms.map.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import leshy.mushrooms.map.domain.model.ThemeMode

/**
 * `MainActivity` зовёт `enableEdgeToEdge()` один раз в `onCreate()`, до `setContent` — то есть
 * системные панели получают цвет по СИСТЕМНОЙ теме на момент запуска и больше не меняются.
 * Этот `SideEffect` — единственное, что делает их реактивными на выбор внутри приложения.
 *
 * Навигационная панель красится вместе со статус-баром: в edge-to-edge жестовая «палочка»
 * рисуется поверх нашего фона тем же способом, что и значки статус-бара, и тёмная палочка на
 * тёмном фоне пропадает ровно так же.
 */
@Composable
actual fun ApplySystemBarsAppearance(mode: ThemeMode) {
    val darkTheme = mode.isDark()
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        // В @Preview контекст — не Activity; там красить нечего, а падать тем более незачем.
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}
