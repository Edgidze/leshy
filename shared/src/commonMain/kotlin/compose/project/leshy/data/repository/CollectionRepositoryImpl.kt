package compose.project.leshy.data.repository

import compose.project.leshy.data.local.dao.CollectionDao
import compose.project.leshy.data.local.entity.CategoryCollectionCrossRef
import compose.project.leshy.data.local.entity.CollectionEntity
import compose.project.leshy.domain.model.Collection
import compose.project.leshy.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CollectionRepositoryImpl(
    private val collectionDao: CollectionDao,
) : CollectionRepository {
    override fun observeAll(): Flow<List<Collection>> =
        collectionDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getByNameKey(nameKey: String): Collection? = collectionDao.getByNameKey(nameKey)?.toDomain()

    override suspend fun upsert(collection: Collection): Long = collectionDao.insert(collection.toEntity())

    override suspend fun addMember(categoryId: Long, collectionId: Long) =
        collectionDao.insertMember(CategoryCollectionCrossRef(categoryId = categoryId, collectionId = collectionId))

    override suspend fun getMemberCategoryIds(collectionId: Long): List<Long> =
        collectionDao.getMemberCategoryIds(collectionId)
}

private fun CollectionEntity.toDomain() = Collection(id = id, nameKey = nameKey, order = order)

private fun Collection.toEntity() = CollectionEntity(id = id, nameKey = nameKey, order = order)
