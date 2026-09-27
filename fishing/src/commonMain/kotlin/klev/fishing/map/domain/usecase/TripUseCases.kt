package klev.fishing.map.domain.usecase

import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.repository.CatchRepository
import klev.fishing.map.domain.repository.TripRepository
import klev.fishing.map.domain.repository.TripTrackPointRepository
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.util.TrackStep
import leshy.mushrooms.map.domain.util.trackStep

/**
 * Начать рыбалку. Возвращает её id.
 *
 * Стартовая координата может быть неизвестна (нет фикса, отказано в доступе) — рыбалка всё равно
 * начинается: пользователь нажал кнопку, и потерять из-за GPS весь выезд недопустимо. Нули в таком
 * случае честно означают «не знаем», как и у грибной прогулки.
 */
class StartTripUseCase(private val trips: TripRepository) {
    suspend operator fun invoke(method: FishingMethod, lat: Double?, lon: Double?, at: Long): Long =
        trips.start(method, lat ?: 0.0, lon ?: 0.0, at)
}

/**
 * Записать точку трека и обновить пройденное расстояние.
 *
 * Расстояние считается ЗДЕСЬ и складывается в рыбалку, а не пересчитывается при показе: трек
 * прореживается для отрисовки (`TrackDecimation` в `:shared`), и сумма по прореженному треку тем
 * меньше настоящей, чем сильнее прореживание.
 */
class RecordTripPointUseCase(
    private val points: TripTrackPointRepository,
    private val trips: TripRepository,
) {
    /**
     * Записать точку трека и обновить пройденное.
     *
     * **Скачки приёмника сюда не попадают.** Правило и его разбор — `trackStep` в `:shared`
     * (`domain/util/TrackPlausibility.kt`); оно общее с грибными прогулками сознательно, чтобы две
     * копии не разъехались. Поймано первым же прогоном на эмуляторе: 633 км за две секунды.
     *
     * Расстояние хранится в самой рыбалке, а не считается по треку при показе: на отрисовку трек
     * прореживается (`TrackDecimation` в `:shared`), и сумма по прореженному тем меньше настоящей,
     * чем сильнее прореживание.
     */
    suspend operator fun invoke(tripId: Long, lat: Double, lon: Double, at: Long) {
        val previous = points.lastOf(tripId)
        if (previous == null) {
            points.append(tripId, lat, lon, at)
            return
        }
        val step = trackStep(
            previous = GeoPoint(previous.lat, previous.lon, elevation = null, timestamp = previous.timestamp),
            next = GeoPoint(lat, lon, elevation = null, timestamp = at),
        )
        when (step) {
            // Скачок приёмника: точку не пишем вовсе — иначе она прочертит через весь экран линию,
            // которой не было, а следующий фикс будет мериться от выброшенной координаты.
            TrackStep.ReceiverJump -> Unit
            // Вернулись после перерыва: координата верна, пути между точками мы не видели.
            TrackStep.ResumedAfterGap -> points.append(tripId, lat, lon, at)
            is TrackStep.Continues -> {
                points.append(tripId, lat, lon, at)
                val current = trips.getById(tripId)?.distanceMeters ?: return
                trips.setDistance(tripId, current + step.meters)
            }
        }
    }
}

/** Закончить рыбалку. Погоду спрашивает экран — отдельным шагом, уже после закрытия. */
class FinishTripUseCase(
    private val trips: TripRepository,
    private val points: TripTrackPointRepository,
) {
    suspend operator fun invoke(tripId: Long, at: Long) {
        val last = points.lastOf(tripId)
        val distance = trips.getById(tripId)?.distanceMeters ?: 0.0
        trips.finish(tripId, last?.lat, last?.lon, distance, at)
    }
}

/**
 * Добавить улов. Пишется в базу немедленно — правило проекта №1 («каждая находка коммитится сразу»)
 * действует здесь ровно так же: процесс может быть убит в любой момент, и всё, что рыбак успел
 * отметить, обязано сохраниться без штатного закрытия рыбалки.
 */
class AddCatchUseCase(private val catches: CatchRepository) {
    suspend operator fun invoke(item: Catch): Long = catches.add(item)
}

class DeleteTripUseCase(private val trips: TripRepository) {
    suspend operator fun invoke(tripId: Long) = trips.delete(tripId)
}
