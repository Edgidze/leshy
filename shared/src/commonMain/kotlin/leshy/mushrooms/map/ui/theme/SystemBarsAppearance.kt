package leshy.mushrooms.map.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import leshy.mushrooms.map.domain.model.ThemeMode

/**
 * Единственное место, где выбор пользователя превращается в «тёмный ли сейчас интерфейс».
 * Живёт здесь, а не строчкой в `App`, потому что второй потребитель — Android-`actual`
 * [ApplySystemBarsAppearance]: иконки системных панелей и [LeshyTheme] обязаны решать одинаково.
 */
@Composable
fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
}

/**
 * Красит содержимое системных панелей под текущее оформление. До тёмной темы обработки
 * статус-бара в проекте не было вообще: фон всегда светлый, дефолтные тёмные иконки на нём
 * читаемы — на тёмном фоне они стали бы невидимы.
 *
 * **Принимает [ThemeMode], а не готовый `Boolean` — намеренно, и упрощать обратно нельзя.**
 * iOS-`actual` красит панель, переключая `overrideUserInterfaceStyle` у окна, а
 * `isSystemInDarkTheme()` на iOS читает `userInterfaceStyle` из трейтов того же окна. С
 * булевым параметром режим «Как в системе» защёлкнулся бы после первого же кадра: окну
 * прописали бы Light, окно перестало бы наследовать системный стиль, и переключение
 * тёмного режима в самой iOS больше не дошло бы до приложения. Различать здесь LIGHT/DARK
 * (жёсткий override) и SYSTEM (`Unspecified`, окно снова наследует систему) может только
 * тот, кто видит сам [ThemeMode].
 */
@Composable
expect fun ApplySystemBarsAppearance(mode: ThemeMode)
