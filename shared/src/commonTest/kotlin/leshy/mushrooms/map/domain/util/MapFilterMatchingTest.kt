package leshy.mushrooms.map.domain.util

import leshy.mushrooms.map.domain.model.MapFilter
import leshy.mushrooms.map.domain.model.Walk
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

/**
 * Прогулка, начавшаяся в полдень указанного месяца — полдень, а не полночь, сознательно: у
 * полуночи любое расхождение часовых поясов между сборкой времени и его разбором перебросило бы
 * дату на соседний месяц, и тест ловил бы часовой пояс машины, а не логику фильтра.
 */
@OptIn(ExperimentalTime::class)
private fun walkStartedIn(year: Int, month: Int, day: Int = 15): Walk {
    val start = LocalDateTime(year, month, day, 12, 0)
        .toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
    return Walk(
        id = 1,
        name = "walk",
        startTime = start,
        endTime = start + 3_600_000,
        distanceMeters = 0.0,
        avgSpeed = 0.0,
        startLat = 0.0,
        startLon = 0.0,
        endLat = null,
        endLon = null,
        mushroomCount = 0,
        thumbnailPath = null,
        description = null,
    )
}

class MapFilterMatchingTest {

    /**
     * Тот самый баг, ради которого тест и написан (найден пользователем, 2026-09-04): слайдер
     * сезона хранит крайние границы как `null` (см. `MapFilterViewModel.setMonthRange`), а
     * `matchesDateAndSeason` требовал НЕ-null с ОБЕИХ сторон — поэтому любой диапазон, задевший
     * январь или декабрь, не отфильтровывал вообще ничего, хотя счётчик «Фильтры: N» его считал.
     */
    @Test
    fun seasonRangeTouchingAnEdgeStillFilters() {
        val january = walkStartedIn(2026, 1)
        val september = walkStartedIn(2026, 9)

        // «с января по август» — верхняя граница задана, нижняя открыта.
        val untilAugust = MapFilter(monthFrom = null, monthTo = 8)
        assertTrue(january.matchesDateAndSeason(untilAugust))
        assertFalse(september.matchesDateAndSeason(untilAugust))

        // «с мая по декабрь» — наоборот.
        val fromMay = MapFilter(monthFrom = 5, monthTo = null)
        assertFalse(january.matchesDateAndSeason(fromMay))
        assertTrue(september.matchesDateAndSeason(fromMay))
    }

    @Test
    fun seasonRangeInsideTheYearFiltersBothEnds() {
        val season = MapFilter(monthFrom = 8, monthTo = 10)
        assertFalse(walkStartedIn(2026, 7).matchesDateAndSeason(season))
        assertTrue(walkStartedIn(2026, 8).matchesDateAndSeason(season))
        assertTrue(walkStartedIn(2026, 10).matchesDateAndSeason(season))
        assertFalse(walkStartedIn(2026, 11).matchesDateAndSeason(season))
    }

    /** Обе границы открыты — сезон не задан, проходит что угодно. */
    @Test
    fun fullSeasonRangeFiltersNothing() {
        val noSeason = MapFilter()
        (1..12).forEach { month ->
            assertTrue(walkStartedIn(2026, month).matchesDateAndSeason(noSeason), "month=$month")
        }
    }

    /**
     * Сезон и диапазон дат сужают выборку ВМЕСТЕ: прогулка обязана попасть в оба, а не в любой
     * из них.
     */
    @Test
    fun dateRangeAndSeasonNarrowTogether() {
        val septemberLastYear = walkStartedIn(2025, 9)
        val septemberThisYear = walkStartedIn(2026, 9)
        val mayThisYear = walkStartedIn(2026, 5)

        val thisYearAutumn = MapFilter(
            startMillis = walkStartedIn(2026, 1, day = 1).startTime,
            endMillis = walkStartedIn(2026, 12, day = 31).startTime,
            monthFrom = 9,
            monthTo = null,
        )
        assertFalse(septemberLastYear.matchesDateAndSeason(thisYearAutumn), "год вне диапазона дат")
        assertFalse(mayThisYear.matchesDateAndSeason(thisYearAutumn), "месяц вне сезона")
        assertTrue(septemberThisYear.matchesDateAndSeason(thisYearAutumn))
    }

    /** Границы диапазона дат сравниваются по календарным суткам — см. [MILLIS_PER_DAY]. */
    @Test
    fun dateRangeIncludesBothEdgeDaysWholeNotFromMidnight() {
        val walk = walkStartedIn(2026, 9, day = 20)
        val sameDayBothEnds = MapFilter(
            startMillis = walk.startTime / MILLIS_PER_DAY * MILLIS_PER_DAY,
            endMillis = walk.startTime / MILLIS_PER_DAY * MILLIS_PER_DAY,
        )
        assertTrue(walk.matchesDateAndSeason(sameDayBothEnds))
    }
}
