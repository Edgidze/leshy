package klev.fishing.map.domain.usecase

import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.repository.CatchRepository
import klev.fishing.map.domain.repository.TripRepository
import klev.fishing.map.domain.repository.TripTrackPointRepository
import leshy.mushrooms.map.domain.util.haversineMeters

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
/**
 * Физически возможная скорость перемещения на рыбалке, м/с. 60 м/с — это 216 км/ч: машиной между
 * точками доехать можно, телепортироваться нельзя. Всё, что выше, — скачок приёмника, а не путь.
 */
private const val MAX_PLAUSIBLE_SPEED_MPS = 60.0

/**
 * После какого перерыва между фиксами скачок перестаёт быть скачком. Приложение могло не получать
 * геопозицию полчаса (в кармане, без неба над головой, с выключенной службой), и за это время
 * человек действительно уехал за сто километров — новая координата верна, а вот пути между ними мы
 * не видели и приписывать его к пройденному не имеем права.
 */
private const val STALE_GAP_SECONDS = 300L

class RecordTripPointUseCase(
    private val points: TripTrackPointRepository,
    private val trips: TripRepository,
) {
    /**
     * Записать точку трека и обновить пройденное.
     *
     * **Скачки приёмника сюда не попадают, и это не перестраховка.** Первый же прогон на эмуляторе
     * дал 633 км за две секунды: приёмник отдал фикс из прошлого места, и он честно лёг в сумму.
     * На телефоне это выглядит так же — «холодный» первый фикс, переход с вышек на спутники, выход
     * из-под моста. Рыбалка, где написано 600 км пути, бесполезна: числу перестают верить целиком.
     *
     * Грибное приложение такой проверки не делает (`RecordTrackPointUseCase` в `:shared` складывает
     * что дали) — это не образец, а место, где рыбалке нужно строже: у неё выезд длится часы и
     * телефон эти часы лежит в кармане.
     *
     * Расстояние считается по ПОЛНОМУ треку и хранится в самой рыбалке: на отрисовку трек
     * прореживается (`TrackDecimation` в `:shared`), и сумма по прореженному тем меньше настоящей,
     * чем сильнее прореживание.
     */
    suspend operator fun invoke(tripId: Long, lat: Double, lon: Double, at: Long) {
        val previous = points.lastOf(tripId)
        if (previous == null) {
            points.append(tripId, lat, lon, at)
            return
        }
        val grown = haversineMeters(previous.lat, previous.lon, lat, lon)
        val elapsedSeconds = ((at - previous.timestamp) / 1000).coerceAtLeast(1L)
        val impliedSpeed = grown / elapsedSeconds
        when {
            impliedSpeed <= MAX_PLAUSIBLE_SPEED_MPS -> {
                points.append(tripId, lat, lon, at)
                val current = trips.getById(tripId)?.distanceMeters ?: return
                trips.setDistance(tripId, current + grown)
            }
            // Перерыва не было — значит это скачок приёмника. Точку не пишем вовсе: иначе она
            // прочертит через весь экран линию, которой не было.
            elapsedSeconds < STALE_GAP_SECONDS -> Unit
            // Перерыв был: координата верна, путь между ними не наш.
            else -> points.append(tripId, lat, lon, at)
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
