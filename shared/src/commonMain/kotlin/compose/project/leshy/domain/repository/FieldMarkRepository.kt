package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.FieldMark
import kotlinx.coroutines.flow.Flow

interface FieldMarkRepository {
    fun observeByWalkId(walkId: Long): Flow<List<FieldMark>>
    suspend fun countMushroomsByWalkAndCategory(walkId: Long, categoryId: Long): Int
    suspend fun addMark(mark: FieldMark): Long
    suspend fun removeLastMushroomMark(walkId: Long, categoryId: Long): Boolean
}
