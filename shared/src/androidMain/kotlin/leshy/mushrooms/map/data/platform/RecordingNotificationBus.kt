package leshy.mushrooms.map.data.platform

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import leshy.mushrooms.map.domain.model.AppLanguage

/**
 * Общая точка встречи трёх независимых сущностей, у которых нет и не может быть ссылок друг на
 * друга: [AndroidBackgroundRecordingController] (живёт в графе Koin, его зовёт `RecordViewModel`),
 * [WalkRecordingService] (создаётся системой) и [WalkRecordingActionReceiver] (создаётся системой
 * на каждый broadcast и живёт микросекунды).
 *
 * Почему `object`, а не ещё один singleton в Koin: получателю broadcast'а иначе пришлось бы
 * гарантировать, что граф Koin уже поднят, — а он поднимается в `LeshyApplication`, и рассуждать
 * о порядке ради трёх полей не хочется. Состояние здесь процессное по своей природе: и сервис, и
 * уведомление существуют ровно в одном экземпляре на процесс.
 *
 * Время жизни привязано к прогулке, а не к процессу: [AndroidBackgroundRecordingController.stop]
 * сбрасывает [snapshot] в `null`, поэтому уведомление никогда не показывает данные прошлой
 * прогулки, даже если сервис успел стартовать раньше первого снимка.
 */
internal object RecordingNotificationBus {

    /** Последнее, что прислал `RecordViewModel`. `null` — записи нет либо снимка ещё не было. */
    val snapshot = MutableStateFlow<RecordingNotificationSnapshot?>(null)

    /**
     * Нажатия «+»/«−» из уведомления, от [WalkRecordingActionReceiver] к `RecordViewModel`.
     *
     * Без `replay`: подписчик — `RecordViewModel`, а он по построению жив всё время, пока висит
     * уведомление (сервис не переживает смерть процесса — `START_NOT_STICKY`, — и снимается в
     * `onTaskRemoved`, который срабатывает как раз тогда, когда `MainActivity` и её
     * `ViewModelStore` уничтожаются). Буфер на всякий случай: `tryEmit` в получателе broadcast'а
     * не имеет права ждать, а частые нажатия должны доходить все.
     */
    val commands = MutableSharedFlow<RecordingCommand>(
        extraBufferCapacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /** Язык, на котором построено текущее уведомление, — из настроек, через `start()`. */
    @Volatile
    var language: AppLanguage = AppLanguage.EN
}
