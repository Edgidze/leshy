package compose.project.leshy.data.repository

import compose.project.leshy.data.local.dao.TrackPointDao
import compose.project.leshy.data.local.entity.TrackPointEntity
import compose.project.leshy.domain.model.TrackPoint
import compose.project.leshy.domain.repository.TrackPointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackPointRepositoryImpl(
    private val trackPointDao: TrackPointDao,
) : TrackPointRepository {
    override fun observeByWalkId(walkId: Long): Flow<List<TrackPoint>> =
        trackPointDao.observeByWalkId(walkId).map { entities -> entities.map { it.toDomain() } }

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
