package leshy.mushrooms.map.data.platform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import leshy.mushrooms.map.domain.model.GeoPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private const val MIN_INTERVAL_MILLIS = 3000L
private val MIN_DISTANCE_METERS = LOCATION_MIN_DISTANCE_METERS.toFloat()

/**
 * Насколько старее текущего должен быть накопленный фикс, чтобы новый принимался независимо от
 * точности. Две минуты — столько же, сколько в примере «getBetterLocation» из документации
 * Android, откуда взято и само правило отбора: если то, что у нас есть, устарело настолько, брать
 * надо любые свежие координаты, даже грубые, потому что грубое «здесь» полезнее точного «там, где
 * я был две минуты назад».
 */
private const val STALE_FIX_MILLIS = 2 * 60 * 1000L

/**
 * Насколько новый фикс может быть хуже текущего по точности и всё-таки быть принят. Ноль сюда
 * ставить нельзя: точность у соседних фиксов одного и того же провайдера гуляет на метры, и
 * строгое сравнение отбрасывало бы половину нормальных обновлений.
 */
private const val ACCURACY_SLACK_METERS = 10f

class AndroidLocationTracker(private val context: Context) : LocationTracker {

    /**
     * **Требует именно точного разрешения — ровно того, что требует [track].** Раньше здесь
     * стояло «точное ИЛИ грубое», и это была дыра, а не послабление: с одним лишь грубым
     * разрешением [track] молча закрывал поток (ему нужно точное), фиксов не приходило вообще, а
     * `isAvailable()` продолжал отвечать «да» — то есть предупреждающая полоса на «Записи» не
     * появлялась. Снаружи это выглядело как «GPS просто не работает, и приложение об этом молчит»,
     * а находки при этом писались в нулевую точку. Два ответа на один вопрос обязаны совпадать.
     */
    override fun isAvailable(): Boolean {
        if (!hasFineLocationPermission(context)) return false
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return runCatching {
            candidateProviders(locationManager).any { locationManager.isProviderEnabled(it) }
        }.getOrDefault(false)
    }

    /**
     * Подписка сразу на ВСЕ доступные провайдеры, а не на один выбранный.
     *
     * Так это устроено после регрессии, стоившей владельцу целой прогулки: с 31.08.2026 провайдер
     * выбирался из `allProviders` (список всех существующих), а не по `isProviderEnabled`. В
     * `allProviders` `gps` есть всегда — значит выбор всегда падал на `gps`, и отката на `network`
     * не оставалось ни при выключенном GPS-провайдере, ни под крышей, где спутников не видно.
     * Приложение молчало и писало находки в нулевую точку, тогда как другие карты на том же
     * телефоне местоположение показывали (они ходят через fused-провайдер Google).
     *
     * Выбирать «лучший» провайдер заранее нельзя в принципе: какой из них ответит первым, зависит
     * от того, где человек стоит, а не от того, что мы решили на старте. Поэтому подписываемся на
     * все и отбираем по факту — [isBetterFix]. Причина, по которой выбор одного провайдера
     * когда-то сделали намеренно (чтобы включённая посреди прогулки геолокация подхватилась сама),
     * сохранена: `requestLocationUpdates` штатно принимает выключенный провайдер и начинает
     * доставлять фиксы после включения, поэтому подписка ставится на все существующие, а не
     * только на включённые сейчас.
     *
     * `FUSED_PROVIDER` (Android 12+) — тот самый, которым пользуются карты: он сам сводит GPS,
     * сеть и датчики. Ниже 12 его нет, там остаются `gps` и `network`.
     */
    override fun track(): Flow<GeoPoint> = callbackFlow {
        if (!hasFineLocationPermission(context)) {
            close()
            return@callbackFlow
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = candidateProviders(locationManager)
        if (providers.isEmpty()) {
            close()
            return@callbackFlow
        }

        // Последний принятый фикс — общий на все провайдеры: именно относительно него решается,
        // стоит ли отдавать очередной. Трогается только из колбэков, а они все приходят на
        // главный поток (Looper.getMainLooper() ниже), так что синхронизация не нужна.
        var accepted: Location? = null
        fun offer(location: Location) {
            if (!isBetterFix(location, accepted)) return
            accepted = location
            trySend(location.toGeoPoint())
        }

        // requestLocationUpdates зовёт слушателя только на СЛЕДУЮЩЕМ фиксе — без этого карта
        // стоит без местоположения до первого свежего обновления (например, ещё до старта
        // прогулки), хотя у системы уже есть недавний фикс в кэше.
        providers.forEach { provider ->
            runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()?.let(::offer)
        }

        val listeners = providers.mapNotNull { provider ->
            val listener = LocationListener { location -> offer(location) }
            val registered = runCatching {
                locationManager.requestLocationUpdates(
                    provider,
                    MIN_INTERVAL_MILLIS,
                    MIN_DISTANCE_METERS,
                    listener,
                    Looper.getMainLooper(),
                )
            }.isSuccess
            if (registered) listener else null
        }
        // Ни один не подписался — поток закрывается, а не висит молча пустым.
        if (listeners.isEmpty()) {
            close()
            return@callbackFlow
        }
        awaitClose { listeners.forEach { runCatching { locationManager.removeUpdates(it) } } }
    }
}

/** Провайдеры, на которые вообще есть смысл подписываться, в порядке предпочтения — см. [track]. */
private fun candidateProviders(locationManager: LocationManager): List<String> {
    val existing = runCatching { locationManager.allProviders }.getOrDefault(emptyList())
    return buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) add(LocationManager.FUSED_PROVIDER)
        add(LocationManager.GPS_PROVIDER)
        add(LocationManager.NETWORK_PROVIDER)
    }.filter { it in existing }
}

/**
 * Стоит ли отдать [candidate] наверх при уже принятом [accepted].
 *
 * Фильтр нужен именно потому, что провайдеров теперь несколько: грубый сетевой фикс с точностью в
 * километр не должен перебивать свежий спутниковый с точностью в пять метров — иначе трек начнёт
 * скакать между двумя источниками, а километраж прогулки распухнет на ровном месте (та же беда,
 * что уже была на iOS из-за отсутствия `distanceFilter`).
 *
 * Порядок проверок — по убыванию силы довода:
 * 1. первого фикса ещё не было — брать;
 * 2. накопленный устарел ([STALE_FIX_MILLIS]) — брать любой свежий, даже грубый;
 * 3. кандидат старше принятого — не брать;
 * 4. иначе брать, только если точность не хуже принятой больше чем на [ACCURACY_SLACK_METERS].
 */
private fun isBetterFix(candidate: Location, accepted: Location?): Boolean {
    if (accepted == null) return true
    val ageDelta = candidate.time - accepted.time
    if (ageDelta > STALE_FIX_MILLIS) return true
    if (ageDelta < 0) return false
    val candidateAccuracy = if (candidate.hasAccuracy()) candidate.accuracy else Float.MAX_VALUE
    val acceptedAccuracy = if (accepted.hasAccuracy()) accepted.accuracy else Float.MAX_VALUE
    return candidateAccuracy <= acceptedAccuracy + ACCURACY_SLACK_METERS
}

internal fun hasFineLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

private fun Location.toGeoPoint() = GeoPoint(
    lat = latitude,
    lon = longitude,
    elevation = if (hasAltitude()) altitude else null,
    timestamp = time,
)
