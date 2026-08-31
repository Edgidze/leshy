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
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.string

private const val NOTIFICATION_CHANNEL_ID = "walk_recording"
private const val NOTIFICATION_ID = 1
private const val EXTRA_LANGUAGE = "language"

/**
 * A foreground service whose sole job is to keep this app out of Android's "background" state
 * while a walk is being recorded. Without a running foreground service of type `location`,
 * Android throttles/stops location callbacks once the screen turns off or another app comes to
 * the front — the GPS subscription itself still lives in [leshy.mushrooms.map.presentation.record.RecordViewModel]
 * (via [AndroidLocationTracker]), this service just keeps the app exempt from that throttling.
 */
class WalkRecordingService : Service() {

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
            ?: AppLanguage.EN
        val notification = buildNotification(language)
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
        // NOT_STICKY: if the process is killed mid-recording, RecordViewModel's in-memory walkId
        // is gone too (nothing persists it), so a system-driven restart of just this service would
        // resurrect "Идёт запись прогулки" with no walk behind it — see androidMain/CLAUDE.md.
        return START_NOT_STICKY
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

    private fun buildNotification(language: AppLanguage): Notification {
        ensureChannel(language)
        val contentIntent = packageManager.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(string(StringKey.BackgroundRecordingNotificationTitle, language))
            .setContentText(string(StringKey.BackgroundRecordingNotificationText, language))
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .build()
    }

    private fun ensureChannel(language: AppLanguage) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            string(StringKey.BackgroundRecordingChannelName, language),
            NotificationManager.IMPORTANCE_LOW,
        )
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
    override fun start(language: AppLanguage) {
        // Without a location permission the service can't legally start at all (see
        // WalkRecordingService.onStartCommand) and would have nothing to do — a walk recorded with
        // no GPS is just a mushroom tally with a timer, which needs no service. Skipping the start
        // here is what keeps the app from crashing when the user pressed "Start" after denying
        // location.
        if (!hasLocationPermission(context)) return
        runCatching {
            ContextCompat.startForegroundService(context, WalkRecordingService.intent(context, language))
        }
    }

    override fun stop() {
        runCatching { context.stopService(Intent(context, WalkRecordingService::class.java)) }
    }
}
