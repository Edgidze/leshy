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

fun formatSpeedKmh(metersPerSecond: Double): String {
    val kmh = metersPerSecond * 3.6
    val rounded = (kmh * 10).toLong() / 10.0
    val whole = rounded.toLong()
    val fraction = ((rounded - whole) * 10).toLong().let { if (it < 0) -it else it }
    return "$whole.$fraction km/h"
}

fun formatDistanceKm(meters: Double): String {
    val km = meters / 1000.0
    val rounded = (km * 100).toLong() / 100.0
    val whole = rounded.toLong()
    val fraction = ((rounded - whole) * 100).toLong().let { if (it < 0) -it else it }
    return "$whole.${fraction.toString().padStart(2, '0')} km"
}

private fun Long.pad(): String = if (this < 10) "0$this" else toString()
