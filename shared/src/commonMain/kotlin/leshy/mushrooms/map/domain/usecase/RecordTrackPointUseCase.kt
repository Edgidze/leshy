package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.TrackPoint
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import leshy.mushrooms.map.domain.util.TrackStep
import leshy.mushrooms.map.domain.util.trackStep

/** Что случилось с очередным фиксом — см. [RecordTrackPointUseCase.invoke]. */
sealed interface TrackPointResult {
    /** Точка записана. [deltaMeters] уже прибавлены к пройденному в прогулке. */
    data class Recorded(val deltaMeters: Double) : TrackPointResult

    /**
     * Точка отброшена как скачок приёмника. Зовущий обязан НЕ двигать свою «последнюю точку» и не
     * увеличивать счётчик последовательности: следующий фикс меряется от последней достоверной.
     */
    data object Skipped : TrackPointResult
}

class RecordTrackPointUseCase(
    private val trackPointRepository: TrackPointRepository,
    private val walkRepository: WalkRepository,
) {
    /**
     * Записывает точку трека и обновляет пройденное.
     *
     * Скачки приёмника сюда не попадают — правило и его причина в
     * [leshy.mushrooms.map.domain.util.trackStep]. До 2026-09-28 проверки не было вовсе, и фикс из
     * другого места честно ложился в сумму пройденного.
     */
    suspend operator fun invoke(
        walkId: Long,
        point: GeoPoint,
        sequence: Int,
        previous: GeoPoint?,
    ): TrackPointResult {
        val step = previous?.let { trackStep(it, point) }
        if (step is TrackStep.ReceiverJump) return TrackPointResult.Skipped

        trackPointRepository.addPoint(
            TrackPoint(
                id = 0,
                walkId = walkId,
                lat = point.lat,
                lon = point.lon,
                timestamp = point.timestamp,
                elevation = point.elevation,
                sequence = sequence,
            ),
        )
        val deltaMeters = (step as? TrackStep.Continues)?.meters ?: 0.0
        if (deltaMeters > 0.0) {
            val walk = walkRepository.getById(walkId) ?: return TrackPointResult.Recorded(deltaMeters)
            walkRepository.update(walk.copy(distanceMeters = walk.distanceMeters + deltaMeters))
        }
        return TrackPointResult.Recorded(deltaMeters)
    }
}
