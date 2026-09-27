package leshy.mushrooms.map.domain.util

import leshy.mushrooms.map.domain.model.GeoPoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

private const val SECOND = 1000L

private fun point(lat: Double, lon: Double, atMillis: Long) =
    GeoPoint(lat = lat, lon = lon, elevation = null, timestamp = atMillis)

class TrackPlausibilityTest {

    @Test
    fun walkingPaceContinuesTheTrack() {
        // Примерно 1.1 м/с — обычный шаг.
        val from = point(59.9300, 30.3100, 0)
        val to = point(59.9301, 30.3100, 10 * SECOND)
        val step = trackStep(from, to)
        assertIs<TrackStep.Continues>(step)
        assertTrue(step.meters in 5.0..20.0, "ожидался шаг порядка десятка метров, вышло ${step.meters}")
    }

    @Test
    fun carTransferBetweenSpotsStillCounts() {
        // 25 м/с (90 км/ч) — переезд между местами, а не скачок: путь настоящий и должен считаться.
        val from = point(59.9300, 30.3100, 0)
        val to = point(60.0200, 30.3100, 400 * SECOND)
        val step = trackStep(from, to)
        assertIs<TrackStep.Continues>(step)
    }

    @Test
    fun receiverJumpIsDropped() {
        // Ровно тот случай, ради которого правило и появилось: сотни километров за две секунды.
        val from = point(59.9300, 30.3100, 0)
        val to = point(55.7500, 37.6200, 2 * SECOND)
        assertEquals(TrackStep.ReceiverJump, trackStep(from, to))
    }

    @Test
    fun jumpAfterLongGapIsKeptButNotCounted() {
        // Телефон полчаса не отдавал геопозицию, человек за это время уехал. Координата верна,
        // но пути мы не видели — точка записывается, расстояние не растёт.
        val from = point(59.9300, 30.3100, 0)
        val to = point(55.7500, 37.6200, 3600 * SECOND)
        assertEquals(TrackStep.ResumedAfterGap, trackStep(from, to))
    }

    @Test
    fun sameTimestampDoesNotDivideByZero() {
        // Два фикса с одной меткой времени: знаменатель зажат единицей, падения быть не должно.
        val from = point(59.9300, 30.3100, 1_000)
        val to = point(59.9301, 30.3100, 1_000)
        assertIs<TrackStep.Continues>(trackStep(from, to))
    }

    @Test
    fun clockMovedBackwardsIsNotTreatedAsSpeed() {
        // Часы устройства перевели назад: отрицательная разница не должна превращаться в
        // отрицательную скорость и молча проходить проверку.
        val from = point(59.9300, 30.3100, 10_000)
        val to = point(55.7500, 37.6200, 0)
        assertEquals(TrackStep.ReceiverJump, trackStep(from, to))
    }

    @Test
    fun standingStillIsZeroDistance() {
        val from = point(59.9300, 30.3100, 0)
        val to = point(59.9300, 30.3100, 30 * SECOND)
        val step = trackStep(from, to)
        assertIs<TrackStep.Continues>(step)
        assertEquals(0.0, step.meters)
    }
}
