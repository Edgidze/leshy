package compose.project.leshy.data.repository

import compose.project.leshy.data.local.dao.ObjectDao
import compose.project.leshy.data.local.entity.ObjectEntity
import compose.project.leshy.data.local.entity.ObjectType
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.repository.FieldMarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FieldMarkRepositoryImpl(
    private val objectDao: ObjectDao,
) : FieldMarkRepository {
    override fun observeByWalkId(walkId: Long): Flow<List<FieldMark>> =
        objectDao.observeByWalkId(walkId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun countMushroomsByWalkAndCategory(walkId: Long, categoryId: Long): Int =
        objectDao.countByWalkAndCategory(walkId, categoryId)

    override suspend fun addMark(mark: FieldMark): Long = objectDao.insert(mark.toEntity())

    override suspend fun removeLastMushroomMark(walkId: Long, categoryId: Long): Boolean {
        val last = objectDao.getLastByWalkAndCategory(walkId, categoryId) ?: return false
        objectDao.delete(last)
        return true
    }
}

private fun ObjectEntity.toDomain() = FieldMark(
    id = id,
    walkId = walkId,
    categoryId = categoryId,
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    type = MarkType.valueOf(type.name),
    photoPath = photoPath,
)

private fun FieldMark.toEntity() = ObjectEntity(
    id = id,
    walkId = walkId,
    categoryId = categoryId,
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    type = ObjectType.valueOf(type.name),
    photoPath = photoPath,
)
