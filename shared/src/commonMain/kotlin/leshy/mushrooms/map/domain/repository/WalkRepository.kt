package leshy.mushrooms.map.domain.repository

import leshy.mushrooms.map.domain.model.Walk
import kotlinx.coroutines.flow.Flow

interface WalkRepository {
    fun observeAll(): Flow<List<Walk>>
    fun observeById(id: Long): Flow<Walk?>
    suspend fun getById(id: Long): Walk?
    suspend fun insert(walk: Walk): Long
    suspend fun update(walk: Walk)
    suspend fun delete(walk: Walk)
}
