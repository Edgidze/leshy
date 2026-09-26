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
 * как ошибки. Поэтому алый живёт отдельным токеном рамы, а не в `ColorScheme`.
 *
 * ## Действие — тёплое дерево, а не лазурь (решение владельца 2026-09-26)
 *
 * Изначально действие несла лазурь флага 1991–1993 (`#088ce8`, углублённая до `#0960A5` ради
 * контраста). На собранном приложении владелец увидел то, чего не видно в таблице цветов:
 * **синие кнопки и переключатели холодные и чужие** на тёплой деревянной земле — единственное
 * место в интерфейсе, где цвет спорит с материалом. Лазурь оставлена там, где она и была
 * осмысленна, — на самом логотипе, — а действия перешли на тёмное дерево `#6E4424`: тот же тон,
 * что у доски жетонов и кнопок, то есть цвет и текстура теперь говорят одно и то же.
 *
 * Довод против алого этим не отменяется и на дерево не распространяется: коричневый с красным
 * цветом ошибки не путается, а красным по-прежнему остаётся только ошибка.
 *
 * Контраст пересчитан заново, не унаследован: белое на `#6E4424` — 8.35, само `#6E4424` на земле
 * (`#E8D9BE`) — 6.00, на мате (`#FFFDF7`) — 8.21; в тёмной теме `#D9B486` на фоне — 8.86, на
 * поверхности — 7.97, тёмная подпись на нём — 7.51. Все пары выше порога AA 4.5 с запасом.
 *
 * ## Значения сверены по контрасту, а не взяты из таблицы дословно
 *
 * `design.md` называет свою таблицу стартом, а не каноном, и требует проверки WCAG AA (раздел 13).
 * Проверка прогнана по всем парам «текст/подложка» в обеих темах; два значения от таблицы
 * отличаются, оба — в сторону контраста:
 *
 * - **`outline` тёмной темы `#7A6144` → `#8F7352`.** Исходный давал 2.67 на поверхности — ниже
 *   порога 3.0 даже для рамок, то есть рамка была бы не видна. Стало 3.49.
 *
 * `outlineVariant` тёмной темы намеренно остаётся низкоконтрастным (1.43): это цвет разделителей,
 * и у самого Material в тёмных схемах он такой же по построению.
 */
private val RussiaLightColors: ColorScheme = lightColorScheme(
    // Действие — тёмное дерево, тот же тон, что у доски жетонов и кнопок.
    primary = Color(0xFF6E4424),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF0DCC0),
    onPrimaryContainer = Color(0xFF3A2413),
    inversePrimary = Color(0xFFD9B486),
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
    surfaceTint = Color(0xFF6E4424),
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
    primary = Color(0xFFD9B486),
    onPrimary = Color(0xFF3A2413),
    primaryContainer = Color(0xFF5A3F26),
    onPrimaryContainer = Color(0xFFEADBC4),
    inversePrimary = Color(0xFF6E4424),
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
    surfaceTint = Color(0xFFD9B486),
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
