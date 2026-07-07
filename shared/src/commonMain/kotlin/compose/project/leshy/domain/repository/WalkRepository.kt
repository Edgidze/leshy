package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.Walk
import kotlinx.coroutines.flow.Flow

interface WalkRepository {
    fun observeAll(): Flow<List<Walk>>
    fun observeById(id: Long): Flow<Walk?>
    suspend fun getById(id: Long): Walk?
    suspend fun insert(walk: Walk): Long
    suspend fun update(walk: Walk)
    suspend fun delete(walk: Walk)
}
