package klev.fishing.map.data.repository

import klev.fishing.map.data.local.dao.TripDao
import klev.fishing.map.data.local.entity.TripEntity
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.model.Trip
import klev.fishing.map.domain.model.TripWeather
import klev.fishing.map.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TripRepositoryImpl(private val dao: TripDao) : TripRepository {
    override fun observeAll(): Flow<List<Trip>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeActive(): Flow<Trip?> = dao.observeActive().map { it?.toDomain() }

    override fun observeById(id: Long): Flow<Trip?> = dao.observeById(id).map { it?.toDomain() }

    override suspend fun findActive(): Trip? = dao.findActive()?.toDomain()

    override suspend fun getById(id: Long): Trip? = dao.getById(id)?.toDomain()

    override suspend fun start(method: FishingMethod, startLat: Double, startLon: Double, at: Long): Long =
        dao.insert(
            TripEntity(
                title = null,
                startTime = at,
                endTime = null,
                method = method,
                waterBody = null,
                distanceMeters = 0.0,
                startLat = startLat,
                startLon = startLon,
                endLat = null,
                endLon = null,
            )
        )

    override suspend fun finish(id: Long, endLat: Double?, endLon: Double?, distanceMeters: Double, at: Long) {
        val current = dao.getById(id) ?: return
        dao.update(current.copy(endTime = at, endLat = endLat, endLon = endLon, distanceMeters = distanceMeters))
    }

    override suspend fun setTitle(id: Long, title: String?) = edit(id) { it.copy(title = title?.ifBlank { null }) }

    override suspend fun setWaterBody(id: Long, waterBody: String?) =
        edit(id) { it.copy(waterBody = waterBody?.ifBlank { null }) }

    override suspend fun setMethod(id: Long, method: FishingMethod) = edit(id) { it.copy(method = method) }

    override suspend fun setThumbnail(id: Long, path: String?) = edit(id) { it.copy(thumbnailPath = path) }

    override suspend fun setDistance(id: Long, distanceMeters: Double) =
        edit(id) { it.copy(distanceMeters = distanceMeters) }

    override suspend fun setWeather(id: Long, weather: TripWeather?) = edit(id) { current ->
        current.copy(
            airTempC = weather?.airTempC,
            waterTempC = weather?.waterTempC,
            pressureHpa = weather?.pressureHpa,
            pressureTrend = weather?.pressureTrend,
            windSpeedMps = weather?.windSpeedMps,
            windDirection = weather?.windDirection,
            cloudiness = weather?.cloudiness,
            precipitation = weather?.precipitation,
            weatherProvenance = weather?.provenance,
        )
    }

    override suspend fun delete(id: Long) = dao.deleteById(id)

    private suspend inline fun edit(id: Long, transform: (TripEntity) -> TripEntity) {
        val current = dao.getById(id) ?: return
        dao.update(transform(current))
    }
}
