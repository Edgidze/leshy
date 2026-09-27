package klev.fishing.map.data.repository

import klev.fishing.map.data.local.dao.TripTrackPointDao
import klev.fishing.map.data.local.entity.TripTrackPointEntity
import klev.fishing.map.domain.model.TripTrackPoint
import klev.fishing.map.domain.repository.TripTrackPointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TripTrackPointRepositoryImpl(private val dao: TripTrackPointDao) : TripTrackPointRepository {
    override fun observeByTrip(tripId: Long): Flow<List<TripTrackPoint>> =
        dao.observeByTrip(tripId).map { list -> list.map { it.toDomain() } }

    override suspend fun getByTrip(tripId: Long): List<TripTrackPoint> = dao.getByTrip(tripId).map { it.toDomain() }

    override suspend fun lastOf(tripId: Long): TripTrackPoint? = dao.lastOf(tripId)?.toDomain()

    override suspend fun append(tripId: Long, lat: Double, lon: Double, at: Long) {
        dao.insert(TripTrackPointEntity(tripId = tripId, lat = lat, lon = lon, timestamp = at))
    }
}
