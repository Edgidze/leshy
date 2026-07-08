package compose.project.leshy.ui.util

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

@OptIn(ExperimentalTime::class)
fun formatDateTime(epochMillis: Long): String =
    Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(DATE_TIME_FORMAT)

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

fun formatDistanceKm(meters: Double): String {
    val km = meters / 1000.0
    val rounded = (km * 100).toLong() / 100.0
    val whole = rounded.toLong()
    val fraction = ((rounded - whole) * 100).toLong().let { if (it < 0) -it else it }
    return "$whole.${fraction.toString().padStart(2, '0')} km"
}

private fun Long.pad(): String = if (this < 10) "0$this" else toString()
