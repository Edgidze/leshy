package leshy.mushrooms.map.data.repository

import leshy.mushrooms.map.data.local.dao.CollectionDao
import leshy.mushrooms.map.data.local.entity.CategoryCollectionCrossRef
import leshy.mushrooms.map.data.local.entity.CollectionEntity
import leshy.mushrooms.map.domain.model.CategoryCollectionMembership
import leshy.mushrooms.map.domain.model.Collection
import leshy.mushrooms.map.domain.model.CollectionSource
import leshy.mushrooms.map.domain.repository.CollectionRepository
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

    override suspend fun getAll(): List<Collection> = collectionDao.getAll().map { it.toDomain() }

    override suspend fun getByNameKey(nameKey: String): Collection? = collectionDao.getByNameKey(nameKey)?.toDomain()

    override suspend fun getById(id: Long): Collection? = collectionDao.getById(id)?.toDomain()

    override suspend fun countBySource(source: CollectionSource): Int = collectionDao.countBySource(source)

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

    // Same insert-or-update split as `upsert` above, batched — see CategoryRepositoryImpl.upsertAll.
    override suspend fun upsertAll(collections: List<Collection>) {
        val (new, existing) = collections.partition { it.id == 0L }
        if (new.isNotEmpty()) collectionDao.insertAll(new.map { it.toEntity() })
        if (existing.isNotEmpty()) collectionDao.updateAll(existing.map { it.toEntity() })
    }

    override suspend fun addMember(categoryId: Long, collectionId: Long) =
        collectionDao.insertMember(CategoryCollectionCrossRef(categoryId = categoryId, collectionId = collectionId))

    override suspend fun addMembers(memberships: List<CategoryCollectionMembership>) =
        collectionDao.insertMembers(
            memberships.map { CategoryCollectionCrossRef(categoryId = it.categoryId, collectionId = it.collectionId) },
        )

    override suspend fun getMemberCategoryIds(collectionId: Long): List<Long> =
        collectionDao.getMemberCategoryIds(collectionId)

    override suspend fun getMemberCollectionIds(categoryId: Long): List<Long> =
        collectionDao.getMemberCollectionIds(categoryId)

    override suspend fun countMembers(collectionId: Long): Int = collectionDao.countMembers(collectionId)

    override suspend fun removeMember(categoryId: Long, collectionId: Long) =
        collectionDao.removeMember(categoryId, collectionId)

    override suspend fun delete(collection: Collection) = collectionDao.delete(collection.toEntity())
}

private fun CollectionEntity.toDomain() =
    Collection(id = id, nameKey = nameKey, order = order, source = source, name = name)

private fun Collection.toEntity() =
    CollectionEntity(id = id, nameKey = nameKey, order = order, source = source, name = name)
