package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.CategoryCollectionMembership
import leshy.mushrooms.map.domain.model.Collection
import leshy.mushrooms.map.domain.model.CollectionSource
import leshy.mushrooms.map.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class InMemoryCollectionRepository(seed: List<Collection> = emptyList()) : CollectionRepository {
    private val state = MutableStateFlow(seed)
    private val members = MutableStateFlow(emptyList<CategoryCollectionMembership>())
    private var nextId = (seed.maxOfOrNull { it.id } ?: 0L) + 1

    override fun observeAll(): Flow<List<Collection>> = state
    override fun observeAllMemberships(): Flow<List<CategoryCollectionMembership>> = members
    override suspend fun getAll(): List<Collection> = state.value
    override suspend fun getByNameKey(nameKey: String): Collection? = state.value.find { it.nameKey == nameKey }
    override suspend fun getById(id: Long): Collection? = state.value.find { it.id == id }
    override suspend fun countBySource(source: CollectionSource): Int = state.value.count { it.source == source }
    override suspend fun upsert(collection: Collection): Long {
        if (collection.id != 0L) {
            state.update { list -> list.map { if (it.id == collection.id) collection else it } }
            return collection.id
        }
        val id = nextId++
        state.update { it + collection.copy(id = id) }
        return id
    }
    override suspend fun upsertAll(collections: List<Collection>) = error("not needed")
    override suspend fun addMember(categoryId: Long, collectionId: Long) {
        members.update { existing ->
            val membership = CategoryCollectionMembership(categoryId, collectionId)
            if (membership in existing) existing else existing + membership
        }
    }
    override suspend fun addMembers(memberships: List<CategoryCollectionMembership>) = error("not needed")
    override suspend fun getMemberCategoryIds(collectionId: Long): List<Long> =
        members.value.filter { it.collectionId == collectionId }.map { it.categoryId }
    override suspend fun getMemberCollectionIds(categoryId: Long): List<Long> =
        members.value.filter { it.categoryId == categoryId }.map { it.collectionId }
    override suspend fun countMembers(collectionId: Long): Int = members.value.count { it.collectionId == collectionId }
    override suspend fun removeMember(categoryId: Long, collectionId: Long) {
        members.update { it.filterNot { m -> m.categoryId == categoryId && m.collectionId == collectionId } }
    }
    override suspend fun delete(collection: Collection) {
        state.update { it.filterNot { c -> c.id == collection.id } }
        members.update { it.filterNot { m -> m.collectionId == collection.id } }
    }
}

private fun userCollection(id: Long, name: String?) = Collection(
    id = id,
    nameKey = "usercoll_$id",
    order = 1000,
    source = CollectionSource.USER,
    name = name,
)

class AssignSpeciesToCollectionUseCaseTest {

    @Test
    fun blankNameCreatesAndReusesTheOtherCollection() = runBlocking {
        val collections = InMemoryCollectionRepository()
        val useCase = AssignSpeciesToCollectionUseCase(collections)

        useCase(categoryId = 1, collectionName = "")
        useCase(categoryId = 2, collectionName = "   ")

        val other = collections.getAll().single()
        assertEquals(OTHER_COLLECTION_NAME_KEY, other.nameKey)
        assertNull(other.name)
        assertEquals(listOf(1L, 2L), collections.getMemberCategoryIds(other.id))
    }

    /** Имя набирается руками каждый раз — регистр и пробелы не должны плодить двойников. */
    @Test
    fun nameMatchesAnExistingCollectionIgnoringCaseAndPadding() = runBlocking {
        val collections = InMemoryCollectionRepository(listOf(userCollection(1, "Мои белые")))
        val useCase = AssignSpeciesToCollectionUseCase(collections)

        useCase(categoryId = 7, collectionName = "  мои БЕЛЫЕ ")

        assertEquals(1, collections.getAll().size)
        assertEquals(listOf(7L), collections.getMemberCategoryIds(1))
    }

    /** Перенос последнего гриба забирает с собой опустевшую подборку: пустых пользовательских
     * подборок в приложении не бывает. */
    @Test
    fun movingTheLastSpeciesOutDeletesTheEmptiedCollection() = runBlocking {
        val collections = InMemoryCollectionRepository(
            listOf(userCollection(1, "Старая"), userCollection(2, "Новая")),
        )
        collections.addMember(categoryId = 7, collectionId = 1)
        collections.addMember(categoryId = 8, collectionId = 1)
        val useCase = AssignSpeciesToCollectionUseCase(collections)

        useCase(categoryId = 7, collectionName = "Новая")
        assertEquals(2, collections.getAll().size, "в старой ещё остался гриб 8")

        useCase(categoryId = 8, collectionName = "Новая")

        val remaining = collections.getAll().single()
        assertEquals("Новая", remaining.name)
        assertEquals(setOf(7L, 8L), collections.getMemberCategoryIds(remaining.id).toSet())
    }

    /** Пересохранение гриба в ту же подборку ничего не ломает — в частности, не удаляет её по пути
     * как «опустевшую». */
    @Test
    fun reassigningToTheSameCollectionIsANoOp() = runBlocking {
        val collections = InMemoryCollectionRepository(listOf(userCollection(1, "Мои белые")))
        collections.addMember(categoryId = 7, collectionId = 1)
        val useCase = AssignSpeciesToCollectionUseCase(collections)

        useCase(categoryId = 7, collectionName = "Мои белые")

        assertTrue(collections.getAll().size == 1)
        assertEquals(listOf(7L), collections.getMemberCategoryIds(1))
    }
}
