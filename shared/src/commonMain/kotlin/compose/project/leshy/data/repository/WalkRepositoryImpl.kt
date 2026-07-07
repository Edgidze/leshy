package compose.project.leshy.data.repository

import compose.project.leshy.data.local.dao.WalkDao
import compose.project.leshy.data.local.entity.WalkEntity
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.domain.repository.WalkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WalkRepositoryImpl(
    private val walkDao: WalkDao,
) : WalkRepository {
    override fun observeAll(): Flow<List<Walk>> =
        walkDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Walk?> =
        walkDao.observeById(id).map { it?.toDomain() }

    override suspend fun getById(id: Long): Walk? = walkDao.getById(id)?.toDomain()

    override suspend fun insert(walk: Walk): Long = walkDao.insert(walk.toEntity())

    override suspend fun update(walk: Walk) = walkDao.update(walk.toEntity())

    override suspend fun delete(walk: Walk) = walkDao.delete(walk.toEntity())
}

private fun WalkEntity.toDomain() = Walk(
    id = id,
    name = name,
    startTime = startTime,
    endTime = endTime,
    distanceMeters = distanceMeters,
    avgSpeed = avgSpeed,
    startLat = startLat,
    startLon = startLon,
    endLat = endLat,
    endLon = endLon,
    mushroomCount = mushroomCount,
)

private fun Walk.toEntity() = WalkEntity(
    id = id,
    name = name,
    startTime = startTime,
    endTime = endTime,
    distanceMeters = distanceMeters,
    avgSpeed = avgSpeed,
    startLat = startLat,
    startLon = startLon,
    endLat = endLat,
    endLon = endLon,
    mushroomCount = mushroomCount,
)
