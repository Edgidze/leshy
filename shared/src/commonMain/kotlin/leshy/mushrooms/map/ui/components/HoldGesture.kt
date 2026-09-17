package leshy.mushrooms.map.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import leshy.mushrooms.map.ui.util.holdCompleted
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Сколько удерживать, прежде чем появится индикатор.
 *
 * Ноль сюда ставить нельзя: тогда индикатор мигает при каждом обычном нажатии — карточка архива
 * открывается по короткому тапу, и вспышка заливки перед уходом на другой экран читалась бы как
 * дефект отрисовки. 400 мс — уже заведомо не тап (Material считает длинным нажатием 500 мс), но
 * ещё заметно раньше любого из двух порогов приложения.
 */
private val HOLD_INDICATOR_DELAY = 400.milliseconds

/**
 * Шаг перерисовки индикатора. Сам порог этим шагом НЕ отмеряется — сколько прошло, каждый раз
 * спрашивается у монотонных часов, поэтому накопленной ошибки не возникает и заливка доходит до
 * края ровно тогда же, когда срабатывает действие.
 *
 * Кадровые часы (`withFrameNanos`) были бы точнее по фазе, но требуют `MonotonicFrameClock` в
 * контексте корутины жеста — а это уже допущение о том, как устроен хост композиции, и проверить
 * его можно только на живом устройстве. Разницы в 16 мс на глаз тут нет, а лишнего допущения в
 * жесте, который держат по пять секунд, лучше не иметь.
 */
private val HOLD_PROGRESS_TICK = 16.milliseconds

/**
 * Жест «короткое нажатие против удержания» с прогрессом удержания — общий для двух мест, где такой
 * жест в приложении есть: 2 с на «+» плитки гриба (массовое добавление) и 5 с на карточке архива
 * (режим выделения). Оба места до этого несли собственную копию одного и того же цикла.
 *
 * Почему не `combinedClickable`: у встроенного длинного нажатия порог не настраивается и равен
 * долям секунды, а здесь нужны секунды. Таймер гонится с настоящим отпусканием пальца — кто
 * пришёл первым, тот и выиграл.
 *
 * Каждый примитив (`awaitFirstDown`/`waitForUpOrCancellation`) требует своего вызова
 * `awaitPointerEventScope` из внешней области: блок `awaitPointerEventScope` помечен
 * `@RestrictsSuspension` и сам ни `launch`, ни `coroutineScope` позвать не может.
 *
 * **`pointerInput` ключуется на `Unit`, а не на колбэках** — и это не мелочь. Во время записи
 * прогулки каждая GPS-точка обновляет состояние экрана «Записи», из-за чего замыкания плиток
 * пересоздаются по нескольку раз в секунду. Ключ на лямбдах (так была написана первая версия
 * кнопки «+») перезапускал корутину распознавания на каждой такой точке и убивал начатое
 * удержание до того, как оно успевало дойти до порога: удержание «работало» только на паузе. Отсюда
 * же [rememberUpdatedState] на всех параметрах — колбэки обязаны быть свежими без перезапуска
 * цикла.
 *
 * @param holdDuration порог удержания.
 * @param onTap короткое нажатие (отпустили раньше порога).
 * @param onHold порог взят. Тактильный отклик [holdCompleted] даётся здесь же — жест обязан
 *   подтверждать себя сам, независимо от того, что покажет вызывающий.
 * @param enabled `false` — жест распознаётся, но не даёт ни колбэков, ни отклика (кнопка
 *   упёрлась в предел находок). Цикл при этом не перезапускается, когда предел снова разожмётся.
 * @param holdEnabled `false` — удержание не считается вовсе: ни таймера, ни индикатора, ни
 *   отклика, только короткое нажатие. Для случая, когда действию удержания сейчас не на чем
 *   сработать (массовое добавление до старта прогулки).
 * @param interactionSource куда слать [PressInteraction] — чтобы кнопка со своим `indication`
 *   продолжала показывать обычную рябь во время удержания.
 * @param onHoldProgress доля удержания от 0 до 1, ноль — индикатора нет. До
 *   [HOLD_INDICATOR_DELAY] не зовётся ни разу, после взятия порога сбрасывается в ноль: действие
 *   уже случилось, показывать больше нечего.
 */
@Composable
fun Modifier.tapOrHold(
    holdDuration: Duration,
    onTap: () -> Unit,
    onHold: () -> Unit,
    enabled: Boolean = true,
    holdEnabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onHoldProgress: (Float) -> Unit = {},
): Modifier {
    val haptics = LocalHapticFeedback.current
    val currentOnTap = rememberUpdatedState(onTap)
    val currentOnHold = rememberUpdatedState(onHold)
    val currentEnabled = rememberUpdatedState(enabled)
    val currentHoldEnabled = rememberUpdatedState(holdEnabled)
    val currentOnHoldProgress = rememberUpdatedState(onHoldProgress)
    return this.pointerInput(Unit) {
        while (true) {
            val down = awaitPointerEventScope { awaitFirstDown(requireUnconsumed = false) }
            if (!currentEnabled.value) {
                awaitPointerEventScope { waitForUpOrCancellation() }
                continue
            }
            val press = PressInteraction.Press(down.position)
            interactionSource?.tryEmit(press)
            var holdFired = false
            val up = coroutineScope {
                val holdJob = launch {
                    if (!currentHoldEnabled.value) return@launch
                    // Прогресс и само срабатывание считаются от ОДНИХ часов. Отдельная анимация
                    // под тот же порог рано или поздно разъедется со своим таймером, и заливка,
                    // дошедшая до края чуть раньше действия, читается как поломка.
                    val pressedAt = TimeSource.Monotonic.markNow()
                    while (true) {
                        val elapsed = pressedAt.elapsedNow()
                        if (elapsed >= holdDuration) break
                        if (elapsed >= HOLD_INDICATOR_DELAY) {
                            currentOnHoldProgress.value((elapsed / holdDuration).toFloat().coerceIn(0f, 1f))
                        }
                        delay(HOLD_PROGRESS_TICK)
                    }
                    holdFired = true
                    currentOnHoldProgress.value(0f)
                    haptics.holdCompleted()
                    currentOnHold.value()
                }
                val result = awaitPointerEventScope { waitForUpOrCancellation() }
                holdJob.cancel()
                result
            }
            currentOnHoldProgress.value(0f)
            if (up != null) {
                interactionSource?.tryEmit(PressInteraction.Release(press))
                if (!holdFired) currentOnTap.value()
            } else {
                interactionSource?.tryEmit(PressInteraction.Cancel(press))
            }
        }
    }
}

/** Доля непрозрачности заливки удержания. */
private const val HOLD_WIPE_ALPHA = 0.22f

/**
 * Индикатор удержания: заливка, идущая по объекту слева направо и доходящая до правого края ровно
 * к порогу.
 *
 * Один приём на оба жеста — так удержание становится узнаваемым языком, а не двумя разными
 * фокусами. Заливка, а не кольцо вокруг самой кнопки: палец во время удержания закрывает то, на
 * что нажимает, и кольцо диаметром 40dp под собственным большим пальцем не видно вообще. Плитка
 * (120dp) и карточка архива (во всю ширину) из-под пальца торчат, и заливка на них видна.
 *
 * [progress] — лямбда, а не значение: она читается в фазе отрисовки, поэтому кадры удержания
 * перерисовывают объект, но не рекомпозируют его. На «Записи» это важно вдвойне — лента плиток и
 * так рекомпозируется на каждой GPS-точке.
 *
 * [clip] обязателен: заливка рисуется поверх уже отрисованного содержимого этим же узлом, и без
 * обрезки по форме она легла бы квадратом на скруглённые углы карточки.
 */
fun Modifier.holdProgressWipe(shape: Shape, color: Color, progress: () -> Float): Modifier =
    this.clip(shape).drawWithContent {
        drawContent()
        val fraction = progress()
        if (fraction > 0f) {
            drawRect(
                color = color.copy(alpha = HOLD_WIPE_ALPHA),
                size = Size(size.width * fraction, size.height),
            )
        }
    }

/** Куда идёт перелив короткого нажатия: вверх — гриб добавлен, вниз — убавлен. */
enum class TapFlashDirection { UP, DOWN }

/**
 * Длительность перелива короткого нажатия. Верхняя граница поставлена владельцем — «до секунды»;
 * взято заметно меньше, и не из экономии. Плитку жмут очередями (нашёл поляну — десяток находок
 * подряд), а перелив перезапускается с нуля на каждом нажатии: растяни его на секунду, и при
 * частых нажатиях он бы никогда не доходил до конца — видна была бы только вечно растущая заливка
 * без завершения, то есть ровно то, что читается как зависший индикатор, а не как отклик.
 */
private const val TAP_FLASH_DURATION_MILLIS = 600

/**
 * Какую долю длительности занимает рост заливки. Остаток — угасание уже полной: без него заливка,
 * дойдя до края, пропадала бы скачком, и вместо «прошло насквозь» читалась бы оборванная
 * анимация.
 */
private const val TAP_FLASH_GROW_FRACTION = 0.55f

/** Доля непрозрачности заливки перелива — плотнее, чем у удержания: она живёт доли секунды,
 * и при [HOLD_WIPE_ALPHA] её просто не успеваешь заметить. */
private const val TAP_FLASH_ALPHA = 0.34f

/**
 * Состояние перелива: одно на объект, направление задаётся каждым вызовом [flash].
 *
 * Счётчик, а не `Boolean`: очередное нажатие обязано перезапускать анимацию с нуля, а не
 * игнорироваться из-за того, что предыдущая ещё идёт (см. [TAP_FLASH_DURATION_MILLIS] — очереди
 * нажатий тут обычное дело). Увеличенный счётчик перезапускает [LaunchedEffect] в
 * [rememberTapFlash], тот отменяет прошлую анимацию и начинает новую.
 */
@Stable
class TapFlashState internal constructor() {
    internal val progress = Animatable(0f)
    internal var direction by mutableStateOf(TapFlashDirection.UP)
    internal var signal by mutableIntStateOf(0)
        private set

    fun flash(direction: TapFlashDirection) {
        this.direction = direction
        signal++
    }
}

/** Заводит [TapFlashState] и держит его анимацию — см. [tapFlashWipe], который её рисует. */
@Composable
fun rememberTapFlash(): TapFlashState {
    val state = remember { TapFlashState() }
    LaunchedEffect(state.signal) {
        // Нулевой сигнал — «ещё ни разу не нажимали»: без этой проверки перелив проигрывался бы
        // сам собой при появлении плитки на экране.
        if (state.signal == 0) return@LaunchedEffect
        state.progress.snapTo(0f)
        // Linear, а не умолчательный `FastOutSlowIn`: заливка изображает движение сквозь плитку с
        // постоянной скоростью, а замедление к концу читалось бы как застревание.
        state.progress.animateTo(1f, tween(TAP_FLASH_DURATION_MILLIS, easing = LinearEasing))
    }
    return state
}

/**
 * Перелив короткого нажатия — заливка, проходящая по объекту снизу вверх ([TapFlashDirection.UP],
 * добавление) или сверху вниз ([TapFlashDirection.DOWN], убавление) и угасающая к концу.
 *
 * Пара к [holdProgressWipe], и намеренно другая по всем трём осям сразу — ось, направление и
 * скорость: удержание ползёт слева направо и стоит на месте, пока палец не отпустят, короткое
 * нажатие проходит поперёк и само заканчивается. Спутать их нельзя даже краем глаза, а язык
 * остаётся одним — заливка по объекту, а не значок поверх него (палец закрывает то, на что
 * нажимает, — см. [holdProgressWipe]).
 *
 * Состояние читается в фазе ОТРИСОВКИ, как и у [holdProgressWipe]: кадры перелива перерисовывают
 * плитку, но не рекомпозируют её. На «Записи» это обязательно — лента и так рекомпозируется на
 * каждой GPS-точке.
 */
fun Modifier.tapFlashWipe(shape: Shape, upColor: Color, downColor: Color, state: TapFlashState): Modifier =
    this.clip(shape).drawWithContent {
        drawContent()
        val elapsed = state.progress.value
        if (elapsed <= 0f || elapsed >= 1f) return@drawWithContent
        val grown = (elapsed / TAP_FLASH_GROW_FRACTION).coerceAtMost(1f)
        val faded = ((elapsed - TAP_FLASH_GROW_FRACTION) / (1f - TAP_FLASH_GROW_FRACTION)).coerceIn(0f, 1f)
        val up = state.direction == TapFlashDirection.UP
        val filledHeight = size.height * grown
        drawRect(
            color = (if (up) upColor else downColor).copy(alpha = TAP_FLASH_ALPHA * (1f - faded)),
            topLeft = Offset(0f, if (up) size.height - filledHeight else 0f),
            size = Size(size.width, filledHeight),
        )
    }
