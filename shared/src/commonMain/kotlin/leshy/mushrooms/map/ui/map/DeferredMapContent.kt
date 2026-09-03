package leshy.mushrooms.map.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Насколько отложить добавление исторических слоёв. Чуть больше длительности перехода между
 * экранами (`NAV_TRANSITION_DURATION_MS` в `ui/navigation/LeshyNavHost.kt`) — намеренно НЕ ссылка
 * на ту константу: связывать анимацию навигации и стоимость мутаций стиля карты нечем, совпадение
 * порядка величин тут случайное, и жёсткая связь только запутала бы.
 */
private val HISTORY_REVEAL_DELAY = 250.milliseconds

/**
 * `true`, когда пора добавлять на карту тяжёлые исторические слои: не в том же кадре, что
 * открывает экран, а после того, как переход отработал. Сбрасывается на каждое изменение [key]
 * (то есть самих данных), чтобы смена фильтра тоже не била по кадру.
 *
 * **Отложить — можно, растянуть — нельзя.** Первая версия этого приёма (2026-09-02) выдавала слои
 * ПО ОДНОМУ ЗА КАДР через `withFrameNanos`, и это оказалось радикально хуже болезни: каждая
 * правка стиля заставляет MapLibre перевалидировать стиль целиком, поэтому десятки правок подряд
 * загнали главный поток внутрь MapLibre на 57 секунд CPU (плюс 44 секунды на
 * `RenderingDispatchQueue` в Compose-редрорере и GC Kotlin/Native). Главный поток при этом держал
 * `os_unfair_lock`, на котором стоял `com.apple.uikit.eventfetch-thread`: подложка карты белела,
 * приложение переставало отвечать, а следом системный watchdog валил SpringBoard и backboardd
 * (`bug_type 309`, три репорта за 9 секунд). Разбор и стеки —
 * `.claude/investigations/ios-maplibre-background-watchdog/README.md`, «Разбор 2026-09-02:
 * постепенная выдача слоёв».
 *
 * Отсюда правило: слои добавлять **одной пачкой**, сдвинутой во времени. Дробить пачку по кадрам
 * не пытаться — суммарная работа от этого не уменьшается, а цена перевалидации умножается.
 */
@Composable
internal fun rememberHistoryRevealed(key: Any?): Boolean {
    var revealed by remember(key) { mutableStateOf(false) }
    LaunchedEffect(key) {
        delay(HISTORY_REVEAL_DELAY)
        revealed = true
    }
    return revealed
}
