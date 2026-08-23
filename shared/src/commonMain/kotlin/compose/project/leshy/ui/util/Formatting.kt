package compose.project.leshy.ui.util

import androidx.compose.runtime.Composable
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
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

@Composable
fun formatDistanceKm(meters: Double): String {
    val km = meters / 1000.0
    val rounded = (km * 100).toLong() / 100.0
    val whole = rounded.toLong()
    val fraction = ((rounded - whole) * 100).toLong().let { if (it < 0) -it else it }
    return "$whole.${fraction.toString().padStart(2, '0')} ${stringResource(StringKey.UnitKilometers)}"
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
