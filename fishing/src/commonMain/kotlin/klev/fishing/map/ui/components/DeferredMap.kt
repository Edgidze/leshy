package klev.fishing.map.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Пауза перед созданием карты на только что открытом экране. Чуть больше длительности перехода
 * между экранами.
 */
private val MAP_REVEAL_DELAY = 350.milliseconds

/**
 * `true`, когда карту на этом экране пора создавать: не в том же кадре, что открывает экран, а
 * после того, как переход отработал.
 *
 * **Зачем.** Уничтожение одного `MapLibre`-вью в тот же кадр, что создаётся другое, вешает главный
 * поток намертво. Воспроизведено на эмуляторе 2026-09-28, стек главного потока из ANR:
 *
 *     java.lang.Object.wait
 *     org.maplibre.android.maps.renderer.textureview.TextureViewRenderThread.onSurfaceTextureDestroyed
 *     org.maplibre.android.maps.renderer.textureview.VulkanTextureViewRenderThread...
 *     android.view.TextureView.onDetachedFromWindowInternal
 *     androidx.compose.ui.platform.AndroidComposeView.removeAndroidView
 *     ...CompositionImpl.applyChanges
 *
 * То есть Compose сносит `TextureView` уходящего экрана, тот ждёт подтверждения от своего
 * рендер-потока — и не дожидается. Приложение перестаёт отвечать, дальше ANR.
 *
 * Путь, на котором это ловится: экран детализации рыбалки (на нём карта) → вкладка «Карта» (на ней
 * тоже карта). Переход между разделами такого не даёт: там прежний экран не уничтожается, а
 * сохраняется (`saveState`), и его вью остаётся жить.
 *
 * **`TextureView` при этом не заменить на `SurfaceView`**, хотя тот такого не делает: `SurfaceView`
 * не участвует в alpha-переходах Compose Navigation и «просвечивает» сквозь fade (корневой
 * `CLAUDE.md`). Поэтому разводим во времени — тот же приём, которым в `:shared` откладывается
 * выдача тяжёлых слоёв (`ui/map/DeferredMapContent.kt`).
 *
 * **Проверено только на эмуляторе, и только с Vulkan-рендером** (`VulkanTextureViewRenderThread` в
 * стеке). На телефоне с GL-путём картина может отличаться — это в списке того, что надо проверить
 * вживую.
 */
@Composable
fun rememberMapRevealed(): Boolean {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(MAP_REVEAL_DELAY)
        revealed = true
    }
    return revealed
}
