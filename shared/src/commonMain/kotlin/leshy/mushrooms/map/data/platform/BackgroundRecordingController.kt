package leshy.mushrooms.map.data.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import leshy.mushrooms.map.domain.model.AppLanguage

/**
 * Сколько строк «вид — счётчик — −/+» помещается в развёрнутое уведомление Android.
 *
 * Не вкусовое число: начиная с Android 12 содержимое кастомного уведомления обрезается системой
 * по 256dp в развёрнутом виде (в свёрнутом — 48dp), а над строками стоят ещё заголовок и строка
 * показателей. Четыре строки по 44dp плюс шапка укладываются в лимит с запасом; пятая — уже нет,
 * и обрезалась бы молча. Список строит [leshy.mushrooms.map.presentation.record.RecordViewModel]
 * (там же, где известны имена видов и порядок плиток), поэтому предел живёт здесь, в общем коде, а
 * не в `androidMain`.
 */
const val MAX_RECORDING_NOTIFICATION_SPECIES = 4

/** Одна строка «миниатюра — вид — −/N/+» в уведомлении идущей записи. */
data class RecordingNotificationSpecies(
    val categoryId: Long,
    /** Уже локализованное имя вида — `androidMain` в i18n не ходит. */
    val name: String,
    val count: Int,
    /**
     * Откуда платформе взять миниатюру вида — ровно те два поля
     * [leshy.mushrooms.map.domain.model.Category], по которым это решает
     * [resolveCategoryIconBytes]: `iconRef` у вида каталога, `iconFile` у пользовательского.
     *
     * Именно ссылки, а не готовые байты: снимок сравнивается целиком (`distinctUntilChanged` в
     * `RecordViewModel`), а у `ByteArray` равенство ссылочное — картинка в снимке ломала бы
     * сравнение, и уведомление пересобиралось бы на каждый GPS-фикс. Декодированием и кешем
     * занимается `AndroidBackgroundRecordingController`.
     */
    val iconRef: String? = null,
    val iconFile: String? = null,
)

/**
 * Всё, что показывает уведомление идущей прогулки, — готовым к отрисовке: строки локализованы и
 * отформатированы на стороне общего кода.
 *
 * [elapsedMillis] здесь — не «текущее время на секунду отправки», а точка отсчёта: Android
 * пересчитывает её в базу системного `Chronometer`, который дальше тикает сам, внутри процесса
 * SystemUI. Поэтому снимок НЕ обязан (и не должен) присылаться раз в секунду — только когда
 * меняется что-то из остального.
 */
data class RecordingNotificationSnapshot(
    /**
     * Язык, на котором собран этот снимок. Едет вместе с ним, а не запоминается платформой один
     * раз при старте прогулки: язык переключается в «Настройках» посреди прогулки, и уведомление
     * иначе осталось бы наполовину на старом — имена видов пришли бы уже переведёнными, а
     * заголовок нет.
     */
    val language: AppLanguage,
    val elapsedMillis: Long,
    val isPaused: Boolean,
    /** Расстояние с единицей измерения, как в шапке «Записи», — например «3.72 км». */
    val distanceText: String,
    val totalFinds: Int,
    val species: List<RecordingNotificationSpecies>,
)

/** Нажатие кнопки в уведомлении идущей записи (Android; на iOS поток команд всегда пуст). */
sealed interface RecordingCommand {
    data class AddMushroom(val categoryId: Long) : RecordingCommand
    data class RemoveMushroom(val categoryId: Long) : RecordingCommand

    /** Кнопка «пауза»/«продолжить» в заголовке уведомления — одна кнопка на оба состояния. */
    data object TogglePause : RecordingCommand
}

/**
 * Keeps GPS updates flowing while a walk is being recorded and the app is not in the foreground
 * (screen off, another app on top). Android needs an active foreground service of type `location`
 * for this — without it the OS throttles/stops location callbacks once the app leaves the
 * foreground. iOS achieves the same via the `location` background mode + `CLLocationManager`
 * flags set directly on the tracker, so its implementation is a no-op.
 */
interface BackgroundRecordingController {
    /** [language] is used for the Android persistent-notification text; unused on iOS. */
    fun start(language: AppLanguage)

    /**
     * Обновляет содержимое уведомления идущей записи. No-op, если запись не идёт (в частности, на
     * iOS — там уведомления нет вовсе).
     */
    fun update(snapshot: RecordingNotificationSnapshot) = Unit

    fun stop()

    /**
     * Нажатия «+»/«−» в уведомлении. Обрабатывает их
     * [leshy.mushrooms.map.presentation.record.RecordViewModel] — теми же методами, что и плитки на
     * экране, чтобы у находки был ровно один путь в Room независимо от того, откуда её отметили.
     */
    val commands: Flow<RecordingCommand> get() = emptyFlow()
}
