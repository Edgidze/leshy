package klev.fishing.map.data.repository

import klev.fishing.map.data.local.dao.CatchDao
import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.repository.CatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CatchRepositoryImpl(private val dao: CatchDao) : CatchRepository {
    override fun observeByTrip(tripId: Long): Flow<List<Catch>> =
        dao.observeByTrip(tripId).map { list -> list.map { it.toDomain() } }

    override fun observeAll(): Flow<List<Catch>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeRecentBaits(): Flow<List<String>> = dao.observeRecentBaits()

    override suspend fun add(item: Catch): Long = dao.insert(item.toEntity().copy(id = 0))

    override suspend fun update(item: Catch) = dao.update(item.toEntity())

    override suspend fun getById(id: Long): Catch? = dao.getById(id)?.toDomain()

    override suspend fun delete(id: Long) = dao.deleteById(id)
}
