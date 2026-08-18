package compose.project.leshy.data.repository

import compose.project.leshy.data.local.dao.CollectionDao
import compose.project.leshy.data.local.entity.CategoryCollectionCrossRef
import compose.project.leshy.data.local.entity.CollectionEntity
import compose.project.leshy.domain.model.CategoryCollectionMembership
import compose.project.leshy.domain.model.Collection
import compose.project.leshy.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CollectionRepositoryImpl(
    private val collectionDao: CollectionDao,
) : CollectionRepository {
    override fun observeAll(): Flow<List<Collection>> =
        collectionDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeAllMemberships(): Flow<List<CategoryCollectionMembership>> =
        collectionDao.observeAllMemberships().map { crossRefs ->
            crossRefs.map { CategoryCollectionMembership(categoryId = it.categoryId, collectionId = it.collectionId) }
        }

    override suspend fun getByNameKey(nameKey: String): Collection? = collectionDao.getByNameKey(nameKey)?.toDomain()

    // Real UPDATE for existing rows — see CategoryRepositoryImpl.upsert for why REPLACE (delete+
    // reinsert) is unsafe now that category_collections cascades off collections.id too.
    override suspend fun upsert(collection: Collection): Long {
        val entity = collection.toEntity()
        return if (collection.id == 0L) {
            collectionDao.insert(entity)
        } else {
            collectionDao.update(entity)
            collection.id
        }
    }

    override suspend fun addMember(categoryId: Long, collectionId: Long) =
        collectionDao.insertMember(CategoryCollectionCrossRef(categoryId = categoryId, collectionId = collectionId))

    override suspend fun getMemberCategoryIds(collectionId: Long): List<Long> =
        collectionDao.getMemberCategoryIds(collectionId)
}

private fun CollectionEntity.toDomain() = Collection(id = id, nameKey = nameKey, order = order)

private fun Collection.toEntity() = CollectionEntity(id = id, nameKey = nameKey, order = order)
