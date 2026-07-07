package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeAll(): Flow<List<Category>>
    fun observeActive(): Flow<List<Category>>
    suspend fun getById(id: Long): Category?
    suspend fun upsert(category: Category): Long
    suspend fun delete(category: Category)
}
