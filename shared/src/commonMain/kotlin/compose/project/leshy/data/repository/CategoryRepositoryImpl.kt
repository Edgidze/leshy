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

    override suspend fun getById(id: Long): Category? = categoryDao.getById(id)?.toDomain()

    override suspend fun getByNameKey(nameKey: String): Category? = categoryDao.getByNameKey(nameKey)?.toDomain()

    override suspend fun count(): Int = categoryDao.count()

    override suspend fun upsert(category: Category): Long = categoryDao.insert(category.toEntity())

    override suspend fun delete(category: Category) = categoryDao.delete(category.toEntity())
}

private fun CategoryEntity.toDomain() = Category(
    id = id,
    nameKey = nameKey,
    colorHex = colorHex,
    iconRef = iconRef,
    order = order,
    isActive = isActive,
)

private fun Category.toEntity() = CategoryEntity(
    id = id,
    nameKey = nameKey,
    colorHex = colorHex,
    iconRef = iconRef,
    order = order,
    isActive = isActive,
)
