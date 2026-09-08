package leshy.mushrooms.map.ui.util

import androidx.compose.runtime.Composable
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.string
import leshy.mushrooms.map.i18n.stringResource
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private val DATE_TIME_FORMAT = kotlinx.datetime.LocalDateTime.Format {
    day()
    char('.')
    monthNumber()
    char('.')
    year()
    char(' ')
    hour()
    char(':')
    minute()
}

private val DATE_ONLY_FORMAT = kotlinx.datetime.LocalDateTime.Format {
    day()
    char('.')
    monthNumber()
    char('.')
    year()
}

private val TIME_ONLY_FORMAT = kotlinx.datetime.LocalDateTime.Format {
    hour()
    char(':')
    minute()
}

@OptIn(ExperimentalTime::class)
fun formatDateTime(epochMillis: Long): String =
    Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(DATE_TIME_FORMAT)

@OptIn(ExperimentalTime::class)
fun formatDateOnly(epochMillis: Long): String =
    Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(DATE_ONLY_FORMAT)

/**
 * Время без даты — для мест, где дата уже стоит рядом и повторять её незачем: экран детализации
 * пишет дату прогулки заголовком, а старт и финиш под ним показывает одним временем. Звать только
 * там, где дата видна из окружения; иначе [formatDateTime], потому что «07:14» само по себе не
 * говорит, какого дня оно было.
 */
@OptIn(ExperimentalTime::class)
fun formatTimeOnly(epochMillis: Long): String =
    Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(TIME_ONLY_FORMAT)

/** Localized month name, 1-12. Shared by the Map filter dialog and the export walks picker. */
@Composable
fun monthName(month: Int): String = stringResource(
    when (month) {
        1 -> StringKey.MonthJanuary
        2 -> StringKey.MonthFebruary
        3 -> StringKey.MonthMarch
        4 -> StringKey.MonthApril
        5 -> StringKey.MonthMay
        6 -> StringKey.MonthJune
        7 -> StringKey.MonthJuly
        8 -> StringKey.MonthAugust
        9 -> StringKey.MonthSeptember
        10 -> StringKey.MonthOctober
        11 -> StringKey.MonthNovember
        else -> StringKey.MonthDecember
    },
)

fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "$hours:${minutes.pad()}:${seconds.pad()}"
    } else {
        "${minutes.pad()}:${seconds.pad()}"
    }
}

/** Days/hours/minutes with unit labels, omitting any leading unit that is zero (minutes always shown). */
@Composable
fun formatDurationLabeled(millis: Long): String {
    val totalMinutes = millis / 60_000
    val days = totalMinutes / (24 * 60)
    val hours = (totalMinutes % (24 * 60)) / 60
    val minutes = totalMinutes % 60

    val parts = mutableListOf<String>()
    if (days > 0) parts += "$days ${stringResource(StringKey.WalkDetailDurationDays)}"
    if (hours > 0) parts += "$hours ${stringResource(StringKey.WalkDetailDurationHours)}"
    parts += "$minutes ${stringResource(StringKey.WalkDetailDurationMinutes)}"
    return parts.joinToString(" ")
}

/** Hours/minutes with short unit labels for compact spots (e.g. the archive walk card): "3 ч 10 мин" */
@Composable
fun formatDurationShort(millis: Long): String {
    val totalMinutes = millis / 60_000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return if (hours > 0) {
        "$hours ${stringResource(StringKey.WalkCardDurationHours)} " +
            "$minutes ${stringResource(StringKey.WalkCardDurationMinutes)}"
    } else {
        "$minutes ${stringResource(StringKey.WalkCardDurationMinutes)}"
    }
}

@Composable
fun formatSpeedKmh(metersPerSecond: Double): String {
    val kmh = metersPerSecond * 3.6
    val rounded = (kmh * 10).toLong() / 10.0
    val whole = rounded.toLong()
    val fraction = ((rounded - whole) * 10).toLong().let { if (it < 0) -it else it }
    return "$whole.$fraction ${stringResource(StringKey.UnitKmh)}"
}

/**
 * Километраж с точностью, убывающей по мере роста числа, — так запись никогда не длиннее трёх
 * цифр: `0.05`, `9.99`, `12.3`, `247`.
 *
 * Постоянные две цифры после точки этого не давали: `123.45 км` — шесть знаков, и на шапке
 * «Записи», где километраж делит строку со временем и счётчиком находок, третий показатель на
 * узком экране просто не помещался. Точность теряется ровно там, где не нужна: десять метров важны,
 * когда прошёл сто, и ничего не значат, когда прошёл пятьдесят километров.
 *
 * Ведущего нуля нет намеренно. Была промежуточная редакция с ним (`00.05`) — ради того, чтобы блок
 * километража повторял по ширине блок времени `00:00` и оба конца шапки выглядели одинаково
 * набранными. Отвергнута владельцем: километраж так не пишут, и в строке «Километраж: 00.05 км» на
 * экране детализации ведущий ноль выглядит опечаткой, а не выравниванием.
 *
 * Дробь **отсекается, а не округляется**. Разница принципиальна на границе разрядов: округление
 * превратило бы 9.999 в `10.00`, то есть в ту самую четвёртую цифру, ради отсутствия которой всё и
 * затевалось.
 *
 * Сотни километров — это уже не одна прогулка, а сумма всех: столько показывает статистика раздела
 * «Карта» (`stats.totalDistanceMeters`). Для неё десятые доли тем более не значат ничего.
 */
@Composable
fun formatDistanceKm(meters: Double): String =
    "${formatDistanceKmValue(meters)} ${stringResource(StringKey.UnitKilometers)}"

/**
 * Не-`@Composable` двойник [formatDistanceKm], которому язык передают явно, — для текста, который
 * собирается вне композиции. Такое место одно: снимок уведомления идущей записи
 * ([leshy.mushrooms.map.data.platform.RecordingNotificationSnapshot]), который целиком строится во
 * `RecordViewModel`, чтобы платформенный слой не ходил в i18n.
 */
fun formatDistanceKm(meters: Double, language: AppLanguage): String =
    "${formatDistanceKmValue(meters)} ${string(StringKey.UnitKilometers, language)}"

/**
 * То же число, но без единицы — для мест, где подпись «км» не помещается и снимается, а само
 * значение остаётся. Такое место одно: шапка «Записи» на узких экранах, см. `RecordScreen.kt`,
 * `STAT_ROW_UNIT_LABEL_MIN_WIDTH`. Везде остальном звать [formatDistanceKm]: число без единицы
 * само по себе не читается, «0.05» может быть чем угодно.
 *
 * Не `@Composable`, в отличие от [formatDistanceKm]: единственное, за чем та ходит в композицию, —
 * это локализованная единица.
 */
fun formatDistanceKmValue(meters: Double): String {
    val km = meters / 1000.0
    val absKm = if (km < 0) -km else km
    return when {
        absKm < 10 -> truncatedToScale(km, scale = 100, decimals = 2)
        absKm < 100 -> truncatedToScale(km, scale = 10, decimals = 1)
        else -> km.toLong().toString()
    }
}

/**
 * [value], отсечённое до [decimals] знаков после запятой; [scale] — 10 в степени [decimals].
 * Собирается из целой и дробной частей вручную, потому что в общем коде KMP нет
 * `String.format`/локалезависимого форматтера чисел.
 */
private fun truncatedToScale(value: Double, scale: Int, decimals: Int): String {
    val truncated = (value * scale).toLong()
    val whole = truncated / scale
    val fraction = (truncated % scale).let { if (it < 0) -it else it }
    return "$whole.${fraction.toString().padStart(decimals, '0')}"
}

/** "2.4x3.1 км" — the ground size of an area, so "small region" stops being a guess. Deliberately
 * one decimal and no thousands separator: this is a glance-value next to a size estimate, not a
 * measurement. */
@Composable
fun formatKilometersExtent(widthMeters: Double, heightMeters: Double): String {
    fun km(meters: Double): String {
        val rounded = (meters / 100).toLong() / 10.0
        val whole = rounded.toLong()
        val fraction = ((rounded - whole) * 10).toLong().let { if (it < 0) -it else it }
        return "$whole.$fraction"
    }
    return "${km(widthMeters)}\u00D7${km(heightMeters)} ${stringResource(StringKey.UnitKilometers)}"
}

@Composable
fun formatMegabytes(bytes: Long): String {
    val mb = bytes / 1_000_000.0
    val rounded = (mb * 10).toLong() / 10.0
    val whole = rounded.toLong()
    val fraction = ((rounded - whole) * 10).toLong().let { if (it < 0) -it else it }
    return "$whole.$fraction ${stringResource(StringKey.UnitMegabytes)}"
}

/** "lat, lon" at fixed 6-decimal precision (~0.1 m), e.g. "55.751244, 37.618423". */
fun formatCoordinates(lat: Double, lon: Double): String = "${formatCoordinate(lat)}, ${formatCoordinate(lon)}"

private fun formatCoordinate(value: Double): String {
    val negative = value < 0
    val scaled = ((if (negative) -value else value) * 1_000_000).toLong()
    val sign = if (negative) "-" else ""
    return "$sign${scaled / 1_000_000}.${(scaled % 1_000_000).toString().padStart(6, '0')}"
}

private fun Long.pad(): String = if (this < 10) "0$this" else toString()
