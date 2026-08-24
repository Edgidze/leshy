package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.CategoryCollectionMembership
import compose.project.leshy.domain.model.Collection
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun observeAll(): Flow<List<Collection>>
    fun observeAllMemberships(): Flow<List<CategoryCollectionMembership>>
    /** One-shot read of every collection — see `CollectionDao.getAll`. */
    suspend fun getAll(): List<Collection>
    suspend fun getByNameKey(nameKey: String): Collection?
    suspend fun count(): Int
    suspend fun upsert(collection: Collection): Long

    /** Batch [upsert]: inserts the `id == 0` ones and updates the rest, each group in one
     * transaction — see `CategoryRepository.upsertAll`. */
    suspend fun upsertAll(collections: List<Collection>)
    suspend fun addMember(categoryId: Long, collectionId: Long)

    /** Batch [addMember], safe to call with memberships that already exist — see
     * `CollectionDao.insertMembers`. */
    suspend fun addMembers(memberships: List<CategoryCollectionMembership>)
    suspend fun getMemberCategoryIds(collectionId: Long): List<Long>
}
