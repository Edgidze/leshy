package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.CategoryCollectionMembership
import compose.project.leshy.domain.model.Collection
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun observeAll(): Flow<List<Collection>>
    fun observeAllMemberships(): Flow<List<CategoryCollectionMembership>>
    suspend fun getByNameKey(nameKey: String): Collection?
    suspend fun upsert(collection: Collection): Long
    suspend fun addMember(categoryId: Long, collectionId: Long)
    suspend fun getMemberCategoryIds(collectionId: Long): List<Long>
}
