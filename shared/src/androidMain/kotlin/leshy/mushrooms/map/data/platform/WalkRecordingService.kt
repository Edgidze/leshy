package leshy.mushrooms.map.data.platform

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.string
import leshy.mushrooms.map.shared.R
import leshy.mushrooms.map.ui.util.formatDistanceKm

/**
 * Важность канала — `IMPORTANCE_HIGH`, и это решение владельца: пока идёт запись, отметка находок
 * для человека главное занятие, а не фоновая мелочь. Практическая разница против `DEFAULT` — место
 * в шторке (уведомление стоит выше и не уезжает в «тихие»), полноценный показ на замке и то, что
 * система не свернёт его в одну строку среди прочих.
 *
 * Звука и вибрации у канала при этом нет (`setSound(null, null)`, `enableVibration(false)`):
 * важность здесь нужна ради места и видимости, а не ради того, чтобы уведомление о собственной
 * прогулке звенело. Всплывающий баннер (heads-up) при HIGH показывается ОДИН раз, на старте
 * прогулки, — за это отвечает `setOnlyAlertOnce(true)` на самом уведомлении, без которого баннер
 * выскакивал бы на каждую находку и на каждые несколько десятков метров.
 *
 * **Каждое изменение важности требует НОВОГО id канала.** Android разрешает приложению только
 * понижать важность существующего канала; поднять — нельзя, и удалить-пересоздать нельзя тоже
 * (канал с тем же id «воскресает» со старыми настройками, это защита ровно от такого приёма).
 * Поэтому id пронумерован, а все прежние удаляются, чтобы не висели в настройках мёртвыми:
 * `walk_recording` был LOW (на LOW уведомление считается «тихим» и не показывается на замке, если
 * в системе выбрано «скрывать тихие»), `walk_recording_v2` — DEFAULT.
 */
private const val NOTIFICATION_CHANNEL_ID = "walk_recording_v3"
private val LEGACY_NOTIFICATION_CHANNEL_IDS = listOf("walk_recording", "walk_recording_v2")
private const val NOTIFICATION_ID = 1
private const val EXTRA_LANGUAGE = "language"

/** Прозрачность «−» у вида, которого в этой прогулке ещё не отмечали: убирать нечего. */
private const val DISABLED_BUTTON_ALPHA = 90
private const val ENABLED_BUTTON_ALPHA = 255

/**
 * A foreground service whose sole job is to keep this app out of Android's "background" state
 * while a walk is being recorded. Without a running foreground service of type `location`,
 * Android throttles/stops location callbacks once the screen turns off or another app comes to
 * the front — the GPS subscription itself still lives in [leshy.mushrooms.map.presentation.record.RecordViewModel]
 * (via [AndroidLocationTracker]), this service just keeps the app exempt from that throttling.
 *
 * Второе (и с точки зрения пользователя — главное) его дело: рисовать само уведомление. Оно
 * кастомное (`RemoteViews`), а не стандартное, потому что стандартный шаблон даёт максимум три
 * кнопки на всё уведомление, а здесь их нужно по две на каждый из
 * [leshy.mushrooms.map.data.platform.MAX_RECORDING_NOTIFICATION_SPECIES] видов. Содержимое
 * приходит снимками из [RecordingNotificationBus] — см. `androidMain/CLAUDE.md`.
 */
class WalkRecordingService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var snapshotJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Second line of defence behind AndroidBackgroundRecordingController.start()'s own check:
        // a foreground service of type `location` may only be started while the app actually holds
        // a location permission, and the system enforces that by THROWING out of startForeground()
        // — SecurityException("Starting FGS with type location ... requires permissions ..."),
        // which crashes the process. Reproduced on API 37: revoke location, tap "Start". Nothing
        // useful is left for this service to do without the permission anyway (it exists purely to
        // keep GPS callbacks flowing in the background), so stop instead of starting.
        if (!hasLocationPermission()) {
            stopSelf()
            return START_NOT_STICKY
        }
        val language = intent?.getStringExtra(EXTRA_LANGUAGE)
            ?.let { runCatching { AppLanguage.valueOf(it) }.getOrNull() }
            ?: RecordingNotificationBus.language
        ensureChannel(language)
        val notification = buildNotification(RecordingNotificationBus.snapshot.value, language)
        // Still guarded: the permission can be lost between the check above and this call, and
        // Android 12+ can also refuse a background start outright
        // (ForegroundServiceStartNotAllowedException). Neither is worth a crash — the walk itself
        // is recorded by RecordViewModel and Room, not by this service.
        val started = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        }.isSuccess
        if (!started) {
            stopSelf()
            return START_NOT_STICKY
        }
        observeSnapshots(language)
        // NOT_STICKY: if the process is killed mid-recording, RecordViewModel's in-memory walkId
        // is gone too (nothing persists it), so a system-driven restart of just this service would
        // resurrect "Идёт запись прогулки" with no walk behind it — see androidMain/CLAUDE.md.
        return START_NOT_STICKY
    }

    /**
     * Подписка на снимки от `RecordViewModel` — ровно одна на весь срок жизни сервиса, даже если
     * `onStartCommand` позвали повторно.
     *
     * Первое значение НЕ пропускается, хотя уведомление с ним уже ушло в `startForeground`: между
     * чтением `snapshot.value` там и подпиской здесь успевает пролезть новый снимок (`start()`
     * контроллера и первый `update()` из `RecordViewModel` разделены одним переключением
     * корутины), и `drop(1)` выбросил бы именно его — уведомление осталось бы с устаревшими
     * показателями до следующего изменения, которого на паузе может и не случиться. Лишний
     * `notify()` тем же содержимым не стоит ничего.
     */
    private fun observeSnapshots(startLanguage: AppLanguage) {
        if (snapshotJob != null) return
        snapshotJob = scope.launch {
            RecordingNotificationBus.snapshot.collect { snapshot ->
                if (snapshot == null) return@collect
                val manager = getSystemService(NotificationManager::class.java)
                // Молча ничего не делает, если пользователь не дал POST_NOTIFICATIONS, — запись при
                // этом идёт как шла.
                runCatching { manager.notify(NOTIFICATION_ID, buildNotification(snapshot, startLanguage)) }
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Swiping the task away from Recents keeps this process alive (that's what a foreground
        // service is for) but really destroys MainActivity, which clears RecordViewModel's
        // viewModelScope — the GPS collector is gone for good. Nothing else tells THIS service to
        // stop (only RecordViewModel.finish() does, and its own next-launch self-heal only runs
        // once the app is reopened), so without this override "Идёт запись прогулки" is left
        // showing with nothing behind it until then — see androidMain/CLAUDE.md.
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    /**
     * [fallbackLanguage] нужен ровно до первого снимка — на те миллисекунды между
     * `startForegroundService` и первым `update()` из `RecordViewModel`, когда показывать ещё
     * нечего. Дальше язык берётся из самого снимка, чтобы переключение языка посреди прогулки
     * доезжало и до заголовка (см. [RecordingNotificationSnapshot.language]).
     */
    private fun buildNotification(
        snapshot: RecordingNotificationSnapshot?,
        fallbackLanguage: AppLanguage,
    ): Notification {
        val language = snapshot?.language ?: fallbackLanguage
        val contentIntent = packageManager.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        val title = string(StringKey.BackgroundRecordingNotificationTitle, language)
        val paused = snapshot?.isPaused == true
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            // Заголовок и текст не рисуются, пока разметка своя, но остаются единственным, что
            // видно там, где кастомная разметка не доезжает: часы, авто, старые оболочки.
            .setContentTitle(title)
            .setContentText(string(StringKey.BackgroundRecordingNotificationText, language))
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            // Каналов до Android 8 нет — там важность уведомления задаётся только этим, поэтому
            // без него на API 24–25 канал был бы HIGH, а уведомление осталось бы обычным.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            // Показывать содержимое на заблокированном экране целиком: PRIVATE (умолчание) на
            // телефоне, настроенном скрывать чувствительное, оставил бы вместо строк видов
            // системную заглушку — а вместе с ними и кнопки, ради которых всё и затевалось.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            // Уведомление перестраивается на каждую находку и каждые несколько десятков метров;
            // без этого каждая перестройка считалась бы новым поводом «привлечь внимание».
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(collapsedView(title, paused, snapshot, language))
            .setCustomBigContentView(expandedView(title, paused, snapshot, language))
            .build()
    }

    private fun collapsedView(
        title: String,
        paused: Boolean,
        snapshot: RecordingNotificationSnapshot?,
        language: AppLanguage,
    ): RemoteViews = RemoteViews(packageName, R.layout.notification_walk_recording_collapsed)
        .also { bindHeader(it, title, paused, snapshot, language) }

    private fun expandedView(
        title: String,
        paused: Boolean,
        snapshot: RecordingNotificationSnapshot?,
        language: AppLanguage,
    ): RemoteViews {
        val views = RemoteViews(packageName, R.layout.notification_walk_recording_expanded)
        bindHeader(views, title, paused, snapshot, language)
        // Строки добавляются, а не выбираются из заранее разложенных в xml: их число меняется по
        // ходу прогулки, и пустая строка-заглушка съедала бы высоту, которой и так впритык.
        snapshot?.species?.forEachIndexed { index, species -> views.addView(R.id.recording_species, speciesRow(index, species)) }
        return views
    }

    /** Заголовок и строка показателей — общая часть свёрнутого и развёрнутого видов. */
    private fun bindHeader(
        views: RemoteViews,
        title: String,
        paused: Boolean,
        snapshot: RecordingNotificationSnapshot?,
        language: AppLanguage,
    ) {
        views.setTextViewText(
            R.id.recording_title,
            if (paused) "$title · ${string(StringKey.RecordPause, language)}" else title,
        )
        // Chronometer'у отдаётся точка отсчёта в шкале elapsedRealtime, дальше он считает сам,
        // внутри SystemUI, — приложение из-за времени не просыпается вовсе. На паузе он
        // останавливается, но показывает накопленное: setBase перерисовывает текст и у
        // остановленного.
        views.setChronometer(
            R.id.recording_time,
            SystemClock.elapsedRealtime() - (snapshot?.elapsedMillis ?: 0L),
            null,
            !paused,
        )
        views.setTextViewText(R.id.recording_distance, snapshot?.distanceText ?: formatDistanceKm(0.0, language))
        views.setTextViewText(R.id.recording_finds, (snapshot?.totalFinds ?: 0).toString())
    }

    private fun speciesRow(index: Int, species: RecordingNotificationSpecies): RemoteViews {
        val row = RemoteViews(packageName, R.layout.notification_walk_recording_species_row)
        row.setTextViewText(R.id.species_name, species.name)
        row.setTextViewText(R.id.species_count, species.count.toString())
        row.setOnClickPendingIntent(R.id.species_add, actionIntent(index, ACTION_ADD_MUSHROOM, species.categoryId))
        row.setOnClickPendingIntent(R.id.species_remove, actionIntent(index, ACTION_REMOVE_MUSHROOM, species.categoryId))
        // Нажатие «−» на нуле безвредно (RemoveLastMushroomMarkUseCase не найдёт что удалять), но
        // кнопка должна говорить об этом до нажатия, а не после.
        row.setInt(
            R.id.species_remove,
            "setImageAlpha",
            if (species.count > 0) ENABLED_BUTTON_ALPHA else DISABLED_BUTTON_ALPHA,
        )
        return row
    }

    /**
     * `PendingIntent` сравниваются по [Intent.filterEquals], а он не смотрит на extras — четыре
     * «плюса», отличающиеся только `categoryId`, оказались бы одним и тем же отложенным интентом,
     * и все четыре добавляли бы первый вид. Различает их `data`; `FLAG_UPDATE_CURRENT` вдобавок
     * обновляет extras у переиспользованного интента, когда список видов сдвинулся.
     */
    private fun actionIntent(index: Int, action: String, categoryId: Long): PendingIntent {
        val intent = Intent(this, WalkRecordingActionReceiver::class.java)
            .setAction(action)
            .setData(Uri.parse("leshy://recording/$action/$categoryId"))
            .putExtra(EXTRA_CATEGORY_ID, categoryId)
        return PendingIntent.getBroadcast(
            this,
            index,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun ensureChannel(language: AppLanguage) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        LEGACY_NOTIFICATION_CHANNEL_IDS.forEach { id ->
            runCatching { manager.deleteNotificationChannel(id) }
        }
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            string(StringKey.BackgroundRecordingChannelName, language),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            setSound(null, null)
            enableVibration(false)
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannel(channel)
    }

    private fun hasLocationPermission(): Boolean = hasLocationPermission(this)

    companion object {
        fun intent(context: Context, language: AppLanguage): Intent =
            Intent(context, WalkRecordingService::class.java).putExtra(EXTRA_LANGUAGE, language.name)
    }
}

/** Coarse is enough for the system's foreground-service-type check; the tracker itself asks for fine. */
internal fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

class AndroidBackgroundRecordingController(private val context: Context) : BackgroundRecordingController {

    override val commands: Flow<RecordingCommand> = RecordingNotificationBus.commands

    override fun start(language: AppLanguage) {
        // Without a location permission the service can't legally start at all (see
        // WalkRecordingService.onStartCommand) and would have nothing to do — a walk recorded with
        // no GPS is just a mushroom tally with a timer, which needs no service. Skipping the start
        // here is what keeps the app from crashing when the user pressed "Start" after denying
        // location.
        if (!hasLocationPermission(context)) return
        RecordingNotificationBus.language = language
        // Именно здесь, а не в stop(): сервис может подняться раньше первого снимка, и без сброса
        // он показал бы показатели предыдущей прогулки. Обнулять на stop() было бы недостаточно —
        // startupHealJob зовёт stop() и до того, как что-то вообще запускалось.
        RecordingNotificationBus.snapshot.value = null
        runCatching {
            ContextCompat.startForegroundService(context, WalkRecordingService.intent(context, language))
        }
    }

    override fun update(snapshot: RecordingNotificationSnapshot) {
        RecordingNotificationBus.snapshot.value = snapshot
    }

    override fun stop() {
        RecordingNotificationBus.snapshot.value = null
        runCatching { context.stopService(Intent(context, WalkRecordingService::class.java)) }
    }
}
