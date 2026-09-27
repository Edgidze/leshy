package klev.fishing.map.data.weather

import klev.fishing.map.domain.model.Cloudiness
import klev.fishing.map.domain.model.Precipitation
import klev.fishing.map.domain.model.PressureTrend
import klev.fishing.map.domain.model.TripWeather
import klev.fishing.map.domain.model.WeatherProvenance
import klev.fishing.map.domain.model.WindDirection
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import leshy.mushrooms.map.data.platform.HttpTextFetcher
import leshy.mushrooms.map.data.platform.currentTimeMillis
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Подсказка погоды через Open-Meteo.
 *
 * **Лицензионное ограничение, требующее решения владельца перед публикацией.** Бесплатный тариф
 * Open-Meteo — ТОЛЬКО для некоммерческого использования; в их условиях коммерческим прямо названо
 * «operating websites or apps that have subscriptions or display advertisements». Рыбацкое
 * приложение с рекламой под бесплатный тариф не попадает: нужен платный план Open-Meteo либо другой
 * источник. Данные под CC-BY 4.0 — атрибуция обязательна в любом случае (проверено 2026-09-27 по
 * open-meteo.com/en/pricing и /en/terms).
 *
 * Поэтому класс подключается в Koin как ВЫБИРАЕМЫЙ провайдер, а не как единственно возможный, и
 * приложение целиком работоспособно с [NoWeatherSuggestions]: погоду всё равно вписывает человек.
 *
 * Берётся `forecast` с `past_days`, а не архив: архив ERA5 отстаёт на несколько суток, то есть
 * ничего не знает про сегодняшнюю рыбалку — именно ту, ради которой кнопку и нажимают.
 */
class OpenMeteoWeatherSource(
    private val http: HttpTextFetcher,
) : WeatherSuggestionSource {

    override suspend fun suggest(lat: Double, lon: Double, atMillis: Long): TripWeather? {
        val daysBack = ((nowMillisCeiling(atMillis) - atMillis) / MILLIS_PER_DAY).toInt() + 1
        if (daysBack > MAX_PAST_DAYS) return null
        val url = buildString {
            append("https://api.open-meteo.com/v1/forecast")
            append("?latitude=").append(format2(lat))
            append("&longitude=").append(format2(lon))
            append("&hourly=temperature_2m,surface_pressure,wind_speed_10m,wind_direction_10m,cloud_cover,precipitation,snowfall")
            append("&wind_speed_unit=ms&timeformat=unixtime&timezone=UTC")
            append("&past_days=").append(daysBack.coerceIn(1, MAX_PAST_DAYS))
            append("&forecast_days=1")
        }
        val body = runCatching { http.fetchText(url) }.getOrNull() ?: return null
        val parsed = runCatching { WeatherJson.decodeFromString<Response>(body) }.getOrNull() ?: return null
        val hourly = parsed.hourly ?: return null
        val times = hourly.time ?: return null
        if (times.isEmpty()) return null

        val targetSeconds = atMillis / 1000
        val index = times.indices.minByOrNull { abs(times[it] - targetSeconds) } ?: return null
        // Мимо запрошенного часа больше чем на сутки — это уже не «погода на рыбалке».
        if (abs(times[index] - targetSeconds) > SECONDS_PER_DAY) return null

        val pressure = hourly.surfacePressure?.getOrNull(index)
        return TripWeather(
            airTempC = hourly.temperature?.getOrNull(index),
            // Температуру ВОДЫ подсказка не даёт сознательно: модель приземного слоя её не знает, а
            // рыбак знает — он её мерил или трогал рукой.
            waterTempC = null,
            pressureHpa = pressure,
            pressureTrend = trendOf(hourly.surfacePressure, times, index),
            windSpeedMps = hourly.windSpeed?.getOrNull(index),
            windDirection = hourly.windDirection?.getOrNull(index)?.let(::rhumbOf),
            cloudiness = hourly.cloudCover?.getOrNull(index)?.let(::cloudinessOf),
            precipitation = precipitationOf(
                hourly.precipitation?.getOrNull(index),
                hourly.snowfall?.getOrNull(index),
            ),
            provenance = WeatherProvenance.SUGGESTED,
        )
    }

    /** Тенденция за сутки — то, что рыболовы обсуждают чаще абсолютного давления. */
    private fun trendOf(series: List<Double?>?, times: List<Long>, index: Int): PressureTrend? {
        val now = series?.getOrNull(index) ?: return null
        val dayAgoSeconds = times[index] - SECONDS_PER_DAY
        val earlierIndex = times.indices.minByOrNull { abs(times[it] - dayAgoSeconds) } ?: return null
        if (abs(times[earlierIndex] - dayAgoSeconds) > SECONDS_PER_HOUR * 3) return null
        val earlier = series.getOrNull(earlierIndex) ?: return null
        val delta = now - earlier
        return when {
            delta > PRESSURE_TREND_THRESHOLD_HPA -> PressureTrend.RISING
            delta < -PRESSURE_TREND_THRESHOLD_HPA -> PressureTrend.FALLING
            else -> PressureTrend.STEADY
        }
    }

    private fun cloudinessOf(percent: Double): Cloudiness = when {
        percent < 20.0 -> Cloudiness.CLEAR
        percent < 80.0 -> Cloudiness.PARTLY
        else -> Cloudiness.OVERCAST
    }

    private fun precipitationOf(millimetres: Double?, snowCentimetres: Double?): Precipitation? {
        if (millimetres == null && snowCentimetres == null) return null
        if ((snowCentimetres ?: 0.0) > 0.0) return Precipitation.SNOW
        val mm = millimetres ?: 0.0
        return when {
            mm <= 0.0 -> Precipitation.NONE
            mm < 0.5 -> Precipitation.DRIZZLE
            mm < 4.0 -> Precipitation.RAIN
            else -> Precipitation.SHOWER
        }
    }

    private fun rhumbOf(degrees: Double): WindDirection {
        val normalized = ((degrees % 360.0) + 360.0) % 360.0
        val sector = ((normalized + 22.5) / 45.0).toInt() % 8
        return WindDirection.entries[sector]
    }

    // Округление до двух знаков: точнее сетка модели всё равно не бывает, а короткий URL
    // не зависит от локали форматирования.
    private fun format2(value: Double): String {
        val scaled = (value * 100.0).roundToInt()
        return "${scaled / 100}.${(abs(scaled) % 100).toString().padStart(2, '0')}"
    }

    // Подставляется «сейчас» с запасом кверху: точное время не нужно, нужно число прошедших суток.
    private fun nowMillisCeiling(atMillis: Long): Long = maxOf(atMillis, currentTimeMillis())

    @Serializable
    private data class Response(val hourly: Hourly? = null)

    @Serializable
    private data class Hourly(
        val time: List<Long>? = null,
        @kotlinx.serialization.SerialName("temperature_2m") val temperature: List<Double?>? = null,
        @kotlinx.serialization.SerialName("surface_pressure") val surfacePressure: List<Double?>? = null,
        @kotlinx.serialization.SerialName("wind_speed_10m") val windSpeed: List<Double?>? = null,
        @kotlinx.serialization.SerialName("wind_direction_10m") val windDirection: List<Double?>? = null,
        @kotlinx.serialization.SerialName("cloud_cover") val cloudCover: List<Double?>? = null,
        val precipitation: List<Double?>? = null,
        val snowfall: List<Double?>? = null,
    )

    private companion object {
        val WeatherJson = Json { ignoreUnknownKeys = true }
        const val MILLIS_PER_DAY = 86_400_000L
        const val SECONDS_PER_DAY = 86_400L
        const val SECONDS_PER_HOUR = 3_600L
        const val MAX_PAST_DAYS = 92
        const val PRESSURE_TREND_THRESHOLD_HPA = 2.0
    }
}
