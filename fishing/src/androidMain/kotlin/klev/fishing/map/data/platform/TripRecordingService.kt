package klev.fishing.map.data.platform

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishString
import klev.fishing.map.shared.R
import leshy.mushrooms.map.domain.model.AppLanguage

private const val CHANNEL_ID = "trip_recording"
private const val NOTIFICATION_ID = 1
private const val EXTRA_LANGUAGE = "language"

/**
 * Foreground-сервис, единственная задача которого — не дать Android посчитать приложение фоновым,
 * пока идёт рыбалка. Без запущенного сервиса типа `location` система душит колбэки геопозиции,
 * как только экран погас или сверху оказалось другое приложение; сама подписка на GPS живёт в
 * `TripViewModel` через `LocationTracker` из `:shared`.
 *
 * Для рыбалки это не удобство, а условие работоспособности: выезд длится часы, и телефон эти часы
 * лежит в кармане или в чехле с погашенным экраном.
 *
 * **Свой, а не грибной `WalkRecordingService`** — хотя тот делает то же самое и переиспользовался
 * бы даром. Причина в том, что он показывает СОДЕРЖАТЕЛЬНОЕ уведомление: строки «вид — счётчик —
 * −/+», заголовок про прогулку, кнопки отметки грибов. Для рыбалки это неверно по смыслу (улов не
 * счётчик) и неверно по тексту, а поправить его значило бы править `:shared` — чего аддитивная
 * сборка не делает. Уведомление здесь намеренно простое: заголовок и подпись, без кнопок.
 */
class TripRecordingService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val language = intent?.getStringExtra(EXTRA_LANGUAGE)
            ?.let { code -> AppLanguage.entries.firstOrNull { it.name == code } }
            ?: AppLanguage.EN
        ensureChannel()
        val notification = buildNotification(language)
        // На Android 10+ тип сервиса обязателен, а на Android 14+ запуск сервиса типа `location`
        // без выданного разрешения — исключение, а не тихий отказ. Поэтому проверка перед стартом:
        // рыбалка должна начинаться и без геопозиции, просто без трека.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!hasLocationPermission()) {
                stopSelf()
                return START_NOT_STICKY
            }
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        return START_STICKY
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    private fun ensureChannel() {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        // IMPORTANCE_DEFAULT без звука и вибрации: уведомление должно занимать место в шторке и
        // быть видно на замке, но звенеть из-за собственной рыбалки ему незачем.
        val channel = NotificationChannel(
            CHANNEL_ID,
            fishString(FishStringKey.RecordTitle, AppLanguage.EN),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            setSound(null, null)
            enableVibration(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(language: AppLanguage): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notif_ic_trip)
            .setContentTitle(fishString(FishStringKey.RecordTitle, language))
            .setContentText(fishString(FishStringKey.RecordIdleHint, language))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .build()

    companion object {
        fun start(context: Context, language: AppLanguage) {
            val intent = Intent(context, TripRecordingService::class.java)
                .putExtra(EXTRA_LANGUAGE, language.name)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, TripRecordingService::class.java))
        }
    }
}
