package klev.fishing.map.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import klev.fishing.map.domain.model.Cloudiness
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.model.Precipitation
import klev.fishing.map.domain.model.PressureTrend
import klev.fishing.map.domain.model.WeatherProvenance
import klev.fishing.map.domain.model.WindDirection

/**
 * Погода разложена по колонкам самой рыбалки, а не вынесена в свою таблицу: у выезда её ровно одна,
 * связь один-к-одному, а по давлению и ветру нужно фильтровать и сортировать обычным SQL («покажи
 * все рыбалки при падающем давлении»). Отдельная таблица дала бы join на каждый такой запрос, не
 * дав ничего взамен.
 */
@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String?,
    val startTime: Long,
    val endTime: Long?,
    val method: FishingMethod,
    val waterBody: String?,
    val distanceMeters: Double,
    val startLat: Double,
    val startLon: Double,
    val endLat: Double?,
    val endLon: Double?,
    val thumbnailPath: String? = null,
    val airTempC: Double? = null,
    val waterTempC: Double? = null,
    val pressureHpa: Double? = null,
    val pressureTrend: PressureTrend? = null,
    val windSpeedMps: Double? = null,
    val windDirection: WindDirection? = null,
    val cloudiness: Cloudiness? = null,
    val precipitation: Precipitation? = null,
    val weatherProvenance: WeatherProvenance? = null,
)
