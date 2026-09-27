package klev.fishing.map.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import leshy.mushrooms.map.domain.model.Edition
import leshy.mushrooms.map.ui.theme.LeshyTheme

/**
 * Тема рыбацкого продукта — ВОДА, а не грибная земля.
 *
 * **Почему обёртка вокруг `LeshyTheme`, а не своя тема с нуля.** Композаблы `:shared`, которыми
 * рыбацкое приложение пользуется целиком (подписи-обводки на плитках, размеры диалогов, карта,
 * форматирование), читают `LeshyTheme.tokens` — форму скруглений, соотношение площадки фото,
 * ступень типографической шкалы. Без `LeshyTheme` снаружи они остались бы без токенов. Поэтому
 * каркас берётся у мировой грибной редакции, а ЦВЕТА переопределяются своей `ColorScheme` поверх:
 * `MaterialTheme` внутри `MaterialTheme` — законная конструкция, вложенный выигрывает, а
 * типографика и формы сознательно наследуются (`MaterialTheme.typography`/`shapes`), чтобы шрифты
 * и скругления остались теми же, что у общих композаблов.
 *
 * Своя палитра разрешена владельцем 2026-09-28 (до этого правило было «оформление рисует
 * владелец»); числа ниже — отправная точка, которую он будет править, а не окончательный вид.
 */
@Composable
fun FishingTheme(useDarkTheme: Boolean, content: @Composable () -> Unit) {
    LeshyTheme(edition = Edition.WORLD, useDarkTheme = useDarkTheme) {
        MaterialTheme(
            colorScheme = if (useDarkTheme) fishingDarkColors else fishingLightColors,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            content = content,
        )
    }
}

/*
 * Палитра. Ведущий цвет — глубокая вода: он же цвет трека на карте и заливки кнопок, поэтому
 * выбран тёмным настолько, чтобы белый текст на нём читался (контраст ≥ 4.5:1), и холодным
 * настолько, чтобы не спорить с цветами видов на плитках — те тёплые и насыщенные.
 *
 * Второй цвет — латунь/дерево снасти: тёплый, но приглушённый; он отвечает за второстепенные
 * действия и не претендует на внимание.
 *
 * `surfaceContainerHighest` задан явно в обеих схемах, потому что на нём лежит площадка плитки
 * вида (`SpeciesTile`): силуэт рыбы рисуется цветом вида ПОВЕРХ него, и «глаз» рыбы берёт этот же
 * цвет — то есть это не декоративный оттенок, а участник рисунка.
 */

private val fishingLightColors = lightColorScheme(
    primary = Color(0xFF12607A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFBEE5F2),
    onPrimaryContainer = Color(0xFF032F3E),
    secondary = Color(0xFF7A5B2E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF3E3C6),
    onSecondaryContainer = Color(0xFF2B1D06),
    tertiary = Color(0xFF3F6B4A),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFCCE8D3),
    onTertiaryContainer = Color(0xFF10291A),
    background = Color(0xFFF2F7F9),
    onBackground = Color(0xFF101C21),
    surface = Color(0xFFF7FBFC),
    onSurface = Color(0xFF101C21),
    surfaceVariant = Color(0xFFD9E5EB),
    onSurfaceVariant = Color(0xFF3B4A51),
    surfaceContainerLow = Color(0xFFEDF4F7),
    surfaceContainer = Color(0xFFE6EFF3),
    surfaceContainerHigh = Color(0xFFDFEAEF),
    surfaceContainerHighest = Color(0xFFD5E3E9),
    outline = Color(0xFF6D7C83),
    outlineVariant = Color(0xFFBECDD4),
)

private val fishingDarkColors = darkColorScheme(
    primary = Color(0xFF7FD1E8),
    onPrimary = Color(0xFF00323F),
    primaryContainer = Color(0xFF0B4A5E),
    onPrimaryContainer = Color(0xFFC6EDF8),
    secondary = Color(0xFFE0C08A),
    onSecondary = Color(0xFF3B2A0B),
    secondaryContainer = Color(0xFF55401C),
    onSecondaryContainer = Color(0xFFF7E4C2),
    tertiary = Color(0xFFA6CFAE),
    onTertiary = Color(0xFF17331F),
    tertiaryContainer = Color(0xFF2C4A34),
    onTertiaryContainer = Color(0xFFC3E8CB),
    background = Color(0xFF0A1418),
    onBackground = Color(0xFFE2ECF0),
    surface = Color(0xFF0E1C21),
    onSurface = Color(0xFFE2ECF0),
    surfaceVariant = Color(0xFF22323A),
    onSurfaceVariant = Color(0xFFBECDD4),
    surfaceContainerLow = Color(0xFF13232A),
    surfaceContainer = Color(0xFF172A31),
    surfaceContainerHigh = Color(0xFF1D333B),
    surfaceContainerHighest = Color(0xFF243D46),
    outline = Color(0xFF87979E),
    outlineVariant = Color(0xFF3B4A51),
)
