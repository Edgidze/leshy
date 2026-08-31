package leshy.mushrooms.map.data.repository

import leshy.mushrooms.map.data.local.dao.TrackPointDao
import leshy.mushrooms.map.data.local.entity.TrackPointEntity
import leshy.mushrooms.map.domain.model.TrackPoint
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Room разворачивает Collection в список плейсхолдеров, а у SQLite есть потолок на их число в
// одном запросе. Разбивка на порции снимает вопрос независимо от того, каков потолок в
// конкретной сборке SQLite: сотни прогулок — достижимое число, особенно после импорта архива.
private const val WALK_ID_CHUNK_SIZE = 900

class TrackPointRepositoryImpl(
    private val trackPointDao: TrackPointDao,
) : TrackPointRepository {
    override fun observeAll(): Flow<List<TrackPoint>> =
        trackPointDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeByWalkId(walkId: Long): Flow<List<TrackPoint>> =
        trackPointDao.observeByWalkId(walkId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getPoints(walkIds: Collection<Long>): List<TrackPoint> {
        if (walkIds.isEmpty()) return emptyList()
        return walkIds.chunked(WALK_ID_CHUNK_SIZE).flatMap { chunk ->
            trackPointDao.getByWalkIds(chunk).map { it.toDomain() }
        }
    }

    override suspend fun addPoint(point: TrackPoint): Long = trackPointDao.insert(point.toEntity())
}

private fun TrackPointEntity.toDomain() = TrackPoint(
    id = id,
    walkId = walkId,
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    elevation = elevation,
    sequence = sequence,
)

private fun TrackPoint.toEntity() = TrackPointEntity(
    id = id,
    walkId = walkId,
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    elevation = elevation,
    sequence = sequence,
)
