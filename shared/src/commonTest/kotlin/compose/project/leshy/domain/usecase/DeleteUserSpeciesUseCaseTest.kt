package compose.project.leshy.domain.usecase

import compose.project.leshy.data.platform.PhotoStorage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.CategorySource
import compose.project.leshy.domain.model.EdibilityStatus
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

private class DeleteSpeciesFakeFieldMarkRepository(seed: List<FieldMark>) : FieldMarkRepository {
    private val state = MutableStateFlow(seed)
    override fun observeAll(): Flow<List<FieldMark>> = state
    override fun observeByWalkId(walkId: Long): Flow<List<FieldMark>> = state.map { it.filter { m -> m.walkId == walkId } }
    override suspend fun countMushroomsByWalkAndCategory(walkId: Long, categoryId: Long): Int = 0
    override suspend fun addMark(mark: FieldMark): Long = error("not needed")
    override suspend fun updateMark(mark: FieldMark) = state.update { list -> list.map { if (it.id == mark.id) mark else it } }
    override suspend fun deleteMark(mark: FieldMark) = state.update { it.filterNot { m -> m.id == mark.id } }
    override suspend fun removeLastMushroomMark(walkId: Long, categoryId: Long): FieldMark? = null
    override suspend fun reassignCategory(oldCategoryId: Long, newCategoryId: Long) =
        state.update { list -> list.map { if (it.categoryId == oldCategoryId) it.copy(categoryId = newCategoryId) else it } }

    private fun MutableStateFlow<List<FieldMark>>.update(transform: (List<FieldMark>) -> List<FieldMark>) {
        value = transform(value)
    }
}

private class DeleteSpeciesFakeCategoryRepository(seed: List<Category>) : CategoryRepository {
    private val state = MutableStateFlow(seed)
    override fun observeAll(): Flow<List<Category>> = state
    override fun observeActive(): Flow<List<Category>> = state.map { it.filter { c -> c.isActive } }
    override fun observeFilterEligible(): Flow<List<Category>> = state.map { it.filter { c -> c.isFilterEligible } }
    override fun observeNonCatalog(): Flow<List<Category>> =
        state.map { it.filter { c -> c.source != CategorySource.APP } }
    override suspend fun getById(id: Long): Category? = state.value.find { it.id == id }
    override suspend fun getByNameKey(nameKey: String): Category? = state.value.find { it.nameKey == nameKey }
    override suspend fun count(): Int = state.value.size
    override suspend fun upsert(category: Category): Long = error("not needed")
    override suspend fun delete(category: Category) {
        state.value = state.value.filterNot { it.id == category.id }
    }
}

private class DeleteSpeciesFakePhotoStorage : PhotoStorage {
    override fun resolvePath(fileName: String): String = "/$fileName"
}

private val unknownMushroom = Category(
    id = 99,
    nameKey = UNKNOWN_MUSHROOM_NAME_KEY,
    colorHex = "#9E9E8C",
    iconRef = "unknown_mushroom",
    order = 30,
    isActive = true,
    edibilityStatus = EdibilityStatus.NOT_SPECIFIED,
)

private val userSpecies = Category(
    id = 1,
    nameKey = "user_123_456",
    colorHex = "#112233",
    iconRef = null,
    order = 500,
    isActive = true,
    edibilityStatus = EdibilityStatus.NOT_SPECIFIED,
    source = CategorySource.USER,
    customNames = mapOf(),
    iconFile = "catimg_user_123_456.png",
)

class DeleteUserSpeciesUseCaseTest {
    @Test
    fun deletingASpeciesMovesItsFindsToUnknownMushroomAndKeepsTheirPhotos() = runBlocking {
        val fileSystem = FakeFileSystem()
        fileSystem.createDirectories("/".toPath())
        fileSystem.write("/mark.jpg".toPath()) { write(ByteArray(10)) }
        fileSystem.write("/catimg_user_123_456.png".toPath()) { write(ByteArray(10)) }

        val mark = FieldMark(
            id = 1, walkId = 1, categoryId = userSpecies.id, lat = 0.0, lon = 0.0, timestamp = 0,
            type = MarkType.MUSHROOM, photoPath = "/mark.jpg", name = null, description = null,
        )
        val categories = DeleteSpeciesFakeCategoryRepository(listOf(unknownMushroom, userSpecies))
        val fieldMarks = DeleteSpeciesFakeFieldMarkRepository(listOf(mark))
        val useCase = DeleteUserSpeciesUseCase(categories, fieldMarks, DeleteSpeciesFakePhotoStorage(), fileSystem)

        useCase(userSpecies)

        val survivingMark = fieldMarks.observeAll().first().single()
        assertEquals(unknownMushroom.id, survivingMark.categoryId)
        assertEquals("/mark.jpg", survivingMark.photoPath)
        assertEquals(true, fileSystem.exists("/mark.jpg".toPath()))

        assertNull(categories.getById(userSpecies.id))
        assertFalse(fileSystem.exists("/catimg_user_123_456.png".toPath()))
    }
}
