package leshy.mushrooms.map.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Палитра «Грибных прогулок» — отдельным файлом рядом с `Theme.kt`, а не ветвлением внутри него.
 *
 * Это условие мержабельности из раздела 5 `.claude/plans/russia-edition.md`: добавленный файл при
 * мердже не конфликтует, отредактированный общий — конфликтует всегда. Разбор концепции —
 * `docs/russia-edition/design.md`, раздел 4.
 *
 * ## Алый — это рама, а не действие
 *
 * Главное решение палитры. В иконке владельца красный — паспарту, а не сюжет. Сделай алый
 * основным цветом действия — он столкнётся с красным цветом ошибки, и ошибки перестанут читаться
 * как ошибки. Поэтому действия несёт **лазурь**, ошибка остаётся красной и заметно темнее
 * фирменного алого, а сам алый живёт отдельным токеном рамы (появится вместе с рамами), а не в
 * `ColorScheme`.
 *
 * Исходные тона — флаг 1991–1993 (`#fff`/`#088ce8`/`#da1525`); для экрана они углублены, потому
 * что чистый `#088ce8` на светлом фоне не даёт контраста для текста.
 *
 * ## Значения сверены по контрасту, а не взяты из таблицы дословно
 *
 * `design.md` называет свою таблицу стартом, а не каноном, и требует проверки WCAG AA (раздел 13).
 * Проверка прогнана по всем парам «текст/подложка» в обеих темах; два значения от таблицы
 * отличаются, оба — в сторону контраста:
 *
 * - **`primary` `#0B6FBF` → `#0960A5`.** На земле (`background #E8D9BE`) исходный давал 3.74 —
 *   мало для текста ссылки, а ссылка на земле в приложении есть («Политика конфиденциальности»).
 *   Углублённый даёт 4.68 на земле и 6.40 на мате.
 * - **`outline` тёмной темы `#7A6144` → `#8F7352`.** Исходный давал 2.67 на поверхности — ниже
 *   порога 3.0 даже для рамок, то есть рамка была бы не видна. Стало 3.49.
 *
 * `outlineVariant` тёмной темы намеренно остаётся низкоконтрастным (1.43): это цвет разделителей,
 * и у самого Material в тёмных схемах он такой же по построению.
 */
private val RussiaLightColors: ColorScheme = lightColorScheme(
    // Лазурь действия.
    primary = Color(0xFF0960A5),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD3E8FA),
    onPrimaryContainer = Color(0xFF06304F),
    inversePrimary = Color(0xFF6BB6F0),
    // Дерево.
    secondary = Color(0xFF8A5A33),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEADBC4),
    onSecondaryContainer = Color(0xFF3A2413),
    // Мох.
    tertiary = Color(0xFF3E6B33),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD7E8CF),
    onTertiaryContainer = Color(0xFF10240C),
    // Земля — под ней позже ляжет текстура дерева (токен `groundTexture`).
    background = Color(0xFFE8D9BE),
    onBackground = Color(0xFF2A1F14),
    // Мат — почти белый тёплый. На нём живёт весь текст.
    surface = Color(0xFFFFFDF7),
    onSurface = Color(0xFF2A1F14),
    surfaceVariant = Color(0xFFEADBC4),
    onSurfaceVariant = Color(0xFF4A3524),
    surfaceTint = Color(0xFF0960A5),
    inverseSurface = Color(0xFF3A2E20),
    inverseOnSurface = Color(0xFFF5ECDC),
    // Темнее фирменного алого — чтобы ошибка не сливалась с рамами фотографий.
    error = Color(0xFF9B1B1B),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFBDAD6),
    onErrorContainer = Color(0xFF410E0B),
    // «Мебельная» рама.
    outline = Color(0xFF8A5A33),
    outlineVariant = Color(0xFFC9A87C),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFFFFDF7),
    surfaceDim = Color(0xFFDFCDAE),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFBF5EA),
    surfaceContainer = Color(0xFFF7EFDF),
    surfaceContainerHigh = Color(0xFFF1E6D2),
    surfaceContainerHighest = Color(0xFFEADBC4),
)

/** Мореное дерево. Те же роли, что в светлой, разобраны в KDoc [RussiaLightColors]. */
private val RussiaDarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF6BB6F0),
    onPrimary = Color(0xFF04324F),
    primaryContainer = Color(0xFF0B4E86),
    onPrimaryContainer = Color(0xFFCFE6FA),
    inversePrimary = Color(0xFF0960A5),
    secondary = Color(0xFFC9A87C),
    onSecondary = Color(0xFF3A2413),
    secondaryContainer = Color(0xFF5A3F26),
    onSecondaryContainer = Color(0xFFEADBC4),
    tertiary = Color(0xFF9CC28A),
    onTertiary = Color(0xFF17300F),
    tertiaryContainer = Color(0xFF2F4A26),
    onTertiaryContainer = Color(0xFFD7E8CF),
    background = Color(0xFF211A12),
    onBackground = Color(0xFFEDE2CE),
    surface = Color(0xFF2B231A),
    onSurface = Color(0xFFEDE2CE),
    surfaceVariant = Color(0xFF4A3B28),
    onSurfaceVariant = Color(0xFFD6C4A4),
    surfaceTint = Color(0xFF6BB6F0),
    inverseSurface = Color(0xFFEDE2CE),
    inverseOnSurface = Color(0xFF2B231A),
    error = Color(0xFFE88A8A),
    onError = Color(0xFF4A0F0F),
    errorContainer = Color(0xFF6B1A1A),
    onErrorContainer = Color(0xFFF8D7D7),
    outline = Color(0xFF8F7352),
    outlineVariant = Color(0xFF4A3B28),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF4A3D2D),
    surfaceDim = Color(0xFF1B150E),
    surfaceContainerLowest = Color(0xFF191309),
    surfaceContainerLow = Color(0xFF251E15),
    surfaceContainer = Color(0xFF2B231A),
    surfaceContainerHigh = Color(0xFF362C20),
    surfaceContainerHighest = Color(0xFF423527),
)

internal fun russiaColorScheme(useDarkTheme: Boolean): ColorScheme =
    if (useDarkTheme) RussiaDarkColors else RussiaLightColors
