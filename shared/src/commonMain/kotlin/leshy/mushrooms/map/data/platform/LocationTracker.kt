package leshy.mushrooms.map.data.platform

import leshy.mushrooms.map.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

/**
 * Минимальный сдвиг между двумя фиксами, ниже которого платформа не обязана присылать обновление.
 * Общая на обе реализации намеренно: пока она была задана в каждой отдельно, платформы разошлись —
 * у Android фильтр стоял, а на iOS не был задан вовсе, и `kCLDistanceFilterNone` присылал фикс
 * примерно раз в секунду **даже на неподвижном телефоне**. Каждый такой фикс шёл в
 * `RecordTrackPointUseCase`, у которого своего порога нет, — то есть дрожание GPS честно
 * складывалось в километраж прогулки. Проверено владельцем 2026-09-03 на двух телефонах разом,
 * лежащих рядом: трек рос только на iPhone.
 *
 * Значение — 5 метров, как было на Android. Оно же задаёт и частоту: при шаге около 4 км/ч пять
 * метров набираются примерно за 4–5 секунд, что близко к тамошнему `MIN_INTERVAL_MILLIS`.
 *
 * Ограничения по времени у `CLLocationManager` нет вообще — только `distanceFilter`, поэтому
 * android-овский потолок «не чаще 3 с» на iOS не воспроизводится и намеренно не эмулируется. Это
 * расходится при быстром движении: на 90 км/ч пять метров набираются за 0.2 с, и iOS будет слать
 * фиксы чаще, чем Android. Для приложения про пешую прогулку случай посторонний, но если запись
 * когда-нибудь окажется включённой в машине — искать здесь.
 */
const val LOCATION_MIN_DISTANCE_METERS = 5.0

interface LocationTracker {
    fun track(): Flow<GeoPoint>

    /**
     * Whether the platform can deliver fixes at all right now — the app holds a location
     * permission and the system's location services are on. `false` is the difference between
     * "no fix yet" (normal, resolves itself in seconds) and "no fix ever, until the user changes
     * something in system settings", which is what the Record screen warns about.
     *
     * A snapshot, not a flow: nothing on either platform notifies about a permission change
     * without also restarting the process or resuming the screen, and both of those already
     * re-read this.
     */
    fun isAvailable(): Boolean
}
