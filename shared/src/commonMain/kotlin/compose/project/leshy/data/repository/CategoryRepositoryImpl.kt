package compose.project.leshy.data.repository

import compose.project.leshy.data.local.dao.CategoryDao
import compose.project.leshy.data.local.entity.CategoryEntity
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
) : CategoryRepository {
    override fun observeAll(): Flow<List<Category>> =
        categoryDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeActive(): Flow<List<Category>> =
        categoryDao.observeActive().map { entities -> entities.map { it.toDomain() } }

    override fun observeFilterEligible(): Flow<List<Category>> =
        categoryDao.observeFilterEligible().map { entities -> entities.map { it.toDomain() } }

    override fun observeNonCatalog(): Flow<List<Category>> =
        categoryDao.observeNonCatalog().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAll(): List<Category> = categoryDao.getAll().map { it.toDomain() }

    override suspend fun getById(id: Long): Category? = categoryDao.getById(id)?.toDomain()

    override suspend fun getByNameKey(nameKey: String): Category? = categoryDao.getByNameKey(nameKey)?.toDomain()

    override suspend fun count(): Int = categoryDao.count()

    // A real UPDATE for existing rows, not `insert(..., OnConflictStrategy.REPLACE)` — REPLACE is a
    // delete+reinsert at the SQLite level, which would fire category_collections' ON DELETE CASCADE
    // and silently wipe that category's collection memberships on every plain isActive/isPicked
    // toggle (see .claude/plans/mushroom-collections.md, Phase 1 postmortem).
    override suspend fun upsert(category: Category): Long {
        val entity = category.toEntity()
        return if (category.id == 0L) {
            categoryDao.insert(entity)
        } else {
            categoryDao.update(entity)
            category.id
        }
    }

    // Same insert-or-update split as `upsert` above (and the same reason for it), just batched:
    // two statements for the whole catalog instead of 408 round trips.
    override suspend fun upsertAll(categories: List<Category>) {
        val (new, existing) = categories.partition { it.id == 0L }
        if (new.isNotEmpty()) categoryDao.insertAll(new.map { it.toEntity() })
        if (existing.isNotEmpty()) categoryDao.updateAll(existing.map { it.toEntity() })
    }

    override suspend fun delete(category: Category) = categoryDao.delete(category.toEntity())
}

private fun CategoryEntity.toDomain() = Category(
    id = id,
    nameKey = nameKey,
    colorHex = colorHex,
    iconRef = iconRef,
    order = order,
    isActive = isActive,
    isPicked = isPicked,
    isFilterEligible = isFilterEligible,
    source = source,
    customNames = customNames,
    scientificName = scientificName,
    iconFile = iconFile,
)

private fun Category.toEntity() = CategoryEntity(
    id = id,
    nameKey = nameKey,
    colorHex = colorHex,
    iconRef = iconRef,
    order = order,
    isActive = isActive,
    isPicked = isPicked,
    isFilterEligible = isFilterEligible,
    source = source,
    customNames = customNames,
    scientificName = scientificName,
    iconFile = iconFile,
)
