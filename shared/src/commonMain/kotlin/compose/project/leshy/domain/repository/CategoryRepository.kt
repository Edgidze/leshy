package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeAll(): Flow<List<Category>>
    fun observeActive(): Flow<List<Category>>
    fun observeFilterEligible(): Flow<List<Category>>

    /** User-created and imported species — see `CategoryDao.observeNonCatalog`. */
    fun observeNonCatalog(): Flow<List<Category>>
    /** One-shot read of every category — see `CategoryDao.getAll`. */
    suspend fun getAll(): List<Category>
    suspend fun getById(id: Long): Category?
    suspend fun getByNameKey(nameKey: String): Category?
    suspend fun count(): Int
    suspend fun upsert(category: Category): Long

    /** Batch [upsert]: inserts the `id == 0` ones and updates the rest, each group in one
     * transaction. Ids of the inserted rows aren't reported back — callers that need them should
     * use [upsert] per row. */
    suspend fun upsertAll(categories: List<Category>)
    suspend fun delete(category: Category)
}
