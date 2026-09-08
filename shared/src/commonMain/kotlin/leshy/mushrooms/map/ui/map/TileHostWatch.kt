package leshy.mushrooms.map.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import leshy.mushrooms.map.data.repository.MapStyleCacheRepository
import leshy.mushrooms.map.data.repository.TileHostStatus
import leshy.mushrooms.map.i18n.StringKey
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.seconds

/** Как часто перепроверять хост, пока баннер висит. Проба стоит один запрос на 43 КБ, так что
 * чаще не нужно, а реже — и пользователь будет смотреть на предупреждение, которое уже неправда. */
private val RECHECK_DELAY = 20.seconds

/**
 * Состояние баннера «с подложкой беда» для одного экрана с картой.
 *
 * **Почему это вообще класс, а не пара `var` в каждом из трёх экранов** (чем оно и было): у
 * доступности тайлов появилось три состояния вместо двух, у каждого свой текст, плюс своя пара
 * текстов у «Подготовки» — и всё это было скопировано трижды.
 */
@Stable
internal class TileHostWatch(private val preparation: Boolean) {
    var status by mutableStateOf(TileHostStatus.Reachable)
        private set
    private var dismissed by mutableStateOf(false)

    /** Ключ строки для баннера, либо `null`, когда показывать нечего. */
    val bannerMessage: StringKey?
        get() = when {
            dismissed -> null
            status == TileHostStatus.Reachable -> null
            status == TileHostStatus.Slow ->
                if (preparation) StringKey.MapTilesLoadSlowPreparation else StringKey.MapTilesLoadSlow
            else ->
                if (preparation) StringKey.MapTilesLoadFailedPreparation else StringKey.MapTilesLoadFailed
        }

    fun dismiss() {
        dismissed = true
    }

    /** Стиль теперь всегда локальный (запиненный или запасной), так что это срабатывает только на
     * испорченной копии на диске — редкость, но молчать о ней нельзя, а лучшего текста, чем «карта
     * не загрузилась», для неё всё равно нет. */
    fun onStyleLoadFailed() {
        status = TileHostStatus.Unreachable
    }

    internal fun update(probed: TileHostStatus) {
        status = probed
    }

    internal val keepProbing: Boolean get() = !dismissed && status != TileHostStatus.Reachable
}

/**
 * Заводит [TileHostWatch] и следит за хостом, пока экран жив.
 *
 * Две вещи, из-за которых это переписано (репорт 2026-09-08, мобильный интернет в России):
 *
 * - **`ensureLoaded()` больше не стоит перед пробой, а идёт параллельно ей.** Раньше они были
 *   одной последовательностью, и на первом запуске проба ждала, пока отработают сетевые запросы
 *   стиля и TileJSON: на iOS у `NSURLSession` дефолтный таймаут запроса 60 с на каждый — то есть
 *   предупреждение о недоступной карте появлялось спустя минуты, когда пользователь давно
 *   перестал понимать, что происходит.
 * - **Проба повторяется, пока баннер висит.** Раньше она была разовой: связь, восстановившаяся
 *   через минуту, оставляла на экране предупреждение до тех пор, пока его не закроют руками, а
 *   единственное, что его снимало (`onMapLoadFinished`), к тайлам отношения не имеет вовсе и
 *   срабатывало на локально загруженном стиле — то есть могло погасить верное предупреждение
 *   просто из-за смены языка или темы. Этот сброс убран.
 */
@Composable
internal fun rememberTileHostWatch(preparation: Boolean = false): TileHostWatch {
    val repository = koinInject<MapStyleCacheRepository>()
    val watch = remember(preparation) { TileHostWatch(preparation) }
    LaunchedEffect(watch) {
        launch { repository.ensureLoaded() }
        while (true) {
            watch.update(repository.probeTileHost())
            if (!watch.keepProbing) break
            delay(RECHECK_DELAY)
        }
    }
    return watch
}
