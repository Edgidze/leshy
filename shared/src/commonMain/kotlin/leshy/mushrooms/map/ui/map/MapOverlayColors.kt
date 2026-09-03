package leshy.mushrooms.map.ui.map

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Цвета того, что приложение рисует ПОВЕРХ карты — трека, маркера текущей позиции, линии до
 * выбранного места, рамок офлайн-участков. Карта под ними в тёмной теме уходит почти в чёрное
 * (`data/style/MapStyleDarkener.kt`), и тёмно-зелёный трек `#1B4332` на ней переставал читаться.
 *
 * Тот же приём, каким уже осветляется булавка места: `PlaceMarkerIcon.kt` берёт фон из
 * `MaterialTheme.colorScheme.primary` и потому меняется вместе с темой сам. Здесь он сведён в одно
 * место, потому что цвета делят между собой три карты.
 *
 * **В светлой теме не меняется ни один цвет.** `primary` светлой схемы — это ровно `#1B4332`
 * (`LeshyGreen` в `Theme.kt`), а `error` — материаловский `#B3261E`, то есть те самые константы,
 * что стояли здесь раньше; светлая карта остаётся побайтово прежней, а тёмная получает
 * `#8DCFA9` и `#F2B8B5` из `DarkColors`.
 *
 * Синий и янтарный своего слота в схеме не имеют — она вся зелёно-коричневая, а эти два цвета
 * держатся не за фирменный стиль, а за общую конвенцию («ты здесь» синим, направление янтарным),
 * поэтому им заведена явная пара «светлый/тёмный».
 */
@Immutable
data class MapOverlayColors(
    /** Трек прогулки, маршруты в «Архиве»/«Карте», рамка скачанного офлайн-участка. */
    val track: Color,
    /** Маркер «ты здесь» и рамка участка, который сейчас качается. */
    val currentLocation: Color,
    /** Линия от текущей позиции к выбранному месту. */
    val navigationLine: Color,
    /** Рамка участка, чья загрузка сломалась. */
    val error: Color,
)

private val CURRENT_LOCATION_LIGHT = Color(0xFF2196F3)
private val CURRENT_LOCATION_DARK = Color(0xFF64B5F6)
private val NAVIGATION_LINE_LIGHT = Color(0xFFFF8F00)
private val NAVIGATION_LINE_DARK = Color(0xFFFFB74D)

@Composable
fun rememberMapOverlayColors(): MapOverlayColors {
    val scheme = MaterialTheme.colorScheme
    // `isSystemInDarkTheme()` здесь спрашивать нельзя: тема приложения может быть явно выбрана
    // вопреки системной. Признак темы берётся у самой схемы — тёмная светлее по поверхности.
    val dark = scheme.surface.luminance() < 0.5f
    return remember(scheme.primary, scheme.error, dark) {
        MapOverlayColors(
            track = scheme.primary,
            currentLocation = if (dark) CURRENT_LOCATION_DARK else CURRENT_LOCATION_LIGHT,
            navigationLine = if (dark) NAVIGATION_LINE_DARK else NAVIGATION_LINE_LIGHT,
            error = scheme.error,
        )
    }
}
