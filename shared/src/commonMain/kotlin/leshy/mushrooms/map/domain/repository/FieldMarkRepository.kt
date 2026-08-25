package leshy.mushrooms.map.domain.repository

import leshy.mushrooms.map.domain.model.FieldMark
import kotlinx.coroutines.flow.Flow

interface FieldMarkRepository {
    fun observeAll(): Flow<List<FieldMark>>
    fun observeByWalkId(walkId: Long): Flow<List<FieldMark>>
    suspend fun countMushroomsByWalkAndCategory(walkId: Long, categoryId: Long): Int
    suspend fun addMark(mark: FieldMark): Long
    suspend fun updateMark(mark: FieldMark)
    suspend fun deleteMark(mark: FieldMark)
    suspend fun removeLastMushroomMark(walkId: Long, categoryId: Long): FieldMark?
    suspend fun reassignCategory(oldCategoryId: Long, newCategoryId: Long)
}
