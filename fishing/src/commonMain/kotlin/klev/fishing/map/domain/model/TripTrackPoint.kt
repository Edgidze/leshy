package klev.fishing.map.domain.model

/**
 * Точка трека рыбалки. Отдельная от грибной `TrackPoint` только потому, что ссылается на свою
 * таблицу рыбалок; поля те же, и прореживание для отрисовки берётся из `:shared`
 * (`domain/util/TrackDecimation.kt`) без изменений.
 */
data class TripTrackPoint(
    val id: Long,
    val tripId: Long,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
)
