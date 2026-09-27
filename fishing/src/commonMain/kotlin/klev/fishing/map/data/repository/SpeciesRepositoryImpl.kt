package klev.fishing.map.data.repository

import klev.fishing.map.data.local.dao.SpeciesDao
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.repository.SpeciesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SpeciesRepositoryImpl(private val dao: SpeciesDao) : SpeciesRepository {
    override fun observeAll(): Flow<List<FishSpecies>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getAll(): List<FishSpecies> = dao.getAll().map { it.toDomain() }

    override suspend fun getById(id: Long): FishSpecies? = dao.getById(id)?.toDomain()

    override suspend fun setActive(id: Long, isActive: Boolean) {
        val current = dao.getById(id) ?: return
        dao.update(current.copy(isActive = isActive))
    }
}
