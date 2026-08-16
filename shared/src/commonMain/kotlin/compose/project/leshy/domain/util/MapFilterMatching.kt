package compose.project.leshy.domain.util

import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.MapFilter
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.domain.usecase.MISC_CATEGORY_NAME_KEY
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * The date-range slider (see `MapFilterDialog.kt`) only expresses whole calendar days, not exact
 * timestamps — every date comparison here and in the filter UI must round to day granularity
 * through this same constant, or "the full range" as dragged on the slider (day-floor millis)
 * would never exactly match a walk's actual (sub-day-precision) `startTime`.
 */
const val MILLIS_PER_DAY = 86_400_000L

private fun Long.toDayBucket(): Long = this / MILLIS_PER_DAY

@OptIn(ExperimentalTime::class)
fun Walk.matchesDateAndSeason(filter: MapFilter): Boolean {
    if (filter.startMillis != null && startTime.toDayBucket() < filter.startMillis.toDayBucket()) return false
    if (filter.endMillis != null && startTime.toDayBucket() > filter.endMillis.toDayBucket()) return false
    val monthFrom = filter.monthFrom
    val monthTo = filter.monthTo
    if (monthFrom != null && monthTo != null) {
        val month = Instant.fromEpochMilliseconds(startTime)
            .toLocalDateTime(TimeZone.currentSystemDefault()).month.number
        if (month !in monthFrom..monthTo) return false
    }
    return true
}

/**
 * "Filters: N" badge count: +1 if the selected date range is narrower than the full span of
 * [allWalks] (compared by calendar day, see [MILLIS_PER_DAY]), +1 if the selected month range is
 * narrower than 1..12, +1 if at least one real species is excluded (`isActive == false`) — the
 * synthetic `category_misc` bucket (PHOTO/POI marks, never shown as a toggle) is deliberately
 * excluded from this check since it's always seeded `isActive = false` and isn't a species the
 * user can "deselect".
 */
fun computeFilterCount(filter: MapFilter, allWalks: List<Walk>, allCategories: List<Category>): Int {
    var count = 0
    val walkDays = allWalks.map { it.startTime.toDayBucket() }
    if (walkDays.isNotEmpty()) {
        val minDay = walkDays.min()
        val maxDay = walkDays.max()
        val effectiveStartDay = filter.startMillis?.toDayBucket() ?: minDay
        val effectiveEndDay = filter.endMillis?.toDayBucket() ?: maxDay
        if (effectiveStartDay > minDay || effectiveEndDay < maxDay) count++
    }
    val effectiveMonthFrom = filter.monthFrom ?: 1
    val effectiveMonthTo = filter.monthTo ?: 12
    if (effectiveMonthFrom > 1 || effectiveMonthTo < 12) count++
    if (allCategories.any { it.nameKey != MISC_CATEGORY_NAME_KEY && !it.isActive }) count++
    return count
}
