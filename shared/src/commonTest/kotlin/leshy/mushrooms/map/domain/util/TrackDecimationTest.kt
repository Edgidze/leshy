package leshy.mushrooms.map.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TrackDecimationTest {

    @Test
    fun keepsEveryStrideThPoint() {
        assertEquals(listOf(0, 3, 6, 9), decimateTrack((0..9).toList(), stride = 3))
    }

    /** Хвост маршрута обязан выживать: иначе линия обрывается, не доходя до конца прогулки. */
    @Test
    fun alwaysKeepsTheLastPointEvenWhenItsIndexIsNotAMultipleOfTheStride() {
        assertEquals(listOf(0, 3, 6, 7), decimateTrack((0..7).toList(), stride = 3))
        assertEquals(listOf(0, 4, 8, 10), decimateTrack((0..10).toList(), stride = 4))
    }

    /**
     * Ровно тот случай, что был воспроизведён на эмуляторе с прежним SQL-прореживанием
     * (`sequence % 4 = 0` на прогулке из 4 точек): выживала одна точка, а `LineLayer` рисуется
     * только от двух — маршрут короткой прогулки пропадал с карты целиком.
     */
    @Test
    fun neverDropsAShortTrackBelowTheTwoPointsALineNeeds() {
        assertEquals(listOf(0, 3), decimateTrack(listOf(0, 1, 2, 3), stride = 4))
        (2..12).forEach { size ->
            (1..8).forEach { stride ->
                assertTrue(
                    decimateTrack((0 until size).toList(), stride).size >= 2,
                    "size=$size stride=$stride",
                )
            }
        }
    }

    @Test
    fun keepsBothEndsOfTheTrack() {
        val points = (0..99).toList()
        (1..10).forEach { stride ->
            val result = decimateTrack(points, stride)
            assertEquals(0, result.first(), "stride=$stride")
            assertEquals(99, result.last(), "stride=$stride")
        }
    }

    /** Ничего не выбрасывать и ничего не аллоцировать, когда прореживать нечего. */
    @Test
    fun returnsTheSourceListUntouchedWhenThereIsNothingToThinOut() {
        val points = listOf(1, 2, 3, 4, 5)
        assertSame(points, decimateTrack(points, stride = 1))
        val two = listOf(1, 2)
        assertSame(two, decimateTrack(two, stride = 5))
        val empty = emptyList<Int>()
        assertSame(empty, decimateTrack(empty, stride = 5))
    }

    @Test
    fun rejectsANonPositiveStride() {
        assertFailsWith<IllegalArgumentException> { decimateTrack(listOf(1, 2, 3), stride = 0) }
    }
}
