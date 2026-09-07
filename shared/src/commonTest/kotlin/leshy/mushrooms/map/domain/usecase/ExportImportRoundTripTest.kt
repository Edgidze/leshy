package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.export.dto.CATEGORIES_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.EXPORT_SCHEMA_VERSION
import leshy.mushrooms.map.data.export.dto.ExportJson
import leshy.mushrooms.map.data.export.dto.ExportManifestDto
import leshy.mushrooms.map.data.export.dto.MANIFEST_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.WALK_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.walkDirectory
import leshy.mushrooms.map.data.export.zip.ZipWriter
import leshy.mushrooms.map.data.platform.PhotoStorage
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.CategorySource
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.TrackPoint
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.domain.model.CategoryCollectionMembership
// Под своим именем `Collection` затенил бы `kotlin.collections.Collection`, который тут же нужен
// фейку TrackPointRepository (`getPoints(walkIds: Collection<Long>)`).
import leshy.mushrooms.map.domain.model.Collection as MushroomCollection
import leshy.mushrooms.map.domain.model.CollectionSource
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import okio.Buffer
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private class FakeWalkRepository : WalkRepository {
    private val state = MutableStateFlow<List<Walk>>(emptyList())
    private var nextId = 1L
    override fun observeAll(): Flow<List<Walk>> = state
    override fun observeById(id: Long): Flow<Walk?> = state.map { it.find { w -> w.id == id } }
    override suspend fun getById(id: Long): Walk? = state.value.find { it.id == id }
    override suspend fun insert(walk: Walk): Long {
        val id = nextId++
        state.update { it + walk.copy(id = id) }
        return id
    }
    override suspend fun update(walk: Walk) = state.update { list -> list.map { if (it.id == walk.id) walk else it } }
    override suspend fun delete(walk: Walk) = state.update { it.filterNot { w -> w.id == walk.id } }
}

private class FakeTrackPointRepository : TrackPointRepository {
    private val state = MutableStateFlow<List<TrackPoint>>(emptyList())
    private var nextId = 1L
    override fun observeAll(): Flow<List<TrackPoint>> = state
    override fun observeByWalkId(walkId: Long): Flow<List<TrackPoint>> = state.map { it.filter { p -> p.walkId == walkId } }
    override suspend fun addPoint(point: TrackPoint): Long {
        val id = nextId++
        state.update { it + point.copy(id = id) }
        return id
    }
    // Повторяет семантику SQL-запроса, включая порядок сортировки.
    override suspend fun getPoints(walkIds: Collection<Long>): List<TrackPoint> =
        state.value.filter { it.walkId in walkIds }
            .sortedWith(compareBy({ it.walkId }, { it.sequence }))
}

private class FakeFieldMarkRepository : FieldMarkRepository {
    private val state = MutableStateFlow<List<FieldMark>>(emptyList())
    private var nextId = 1L
    override fun observeAll(): Flow<List<FieldMark>> = state
    override fun observeByWalkId(walkId: Long): Flow<List<FieldMark>> = state.map { it.filter { m -> m.walkId == walkId } }
    override suspend fun countMushroomsByWalkAndCategory(walkId: Long, categoryId: Long): Int = 0
    override suspend fun addMark(mark: FieldMark): Long {
        val id = nextId++
        state.update { it + mark.copy(id = id) }
        return id
    }
    override suspend fun updateMark(mark: FieldMark) = state.update { list -> list.map { if (it.id == mark.id) mark else it } }
    override suspend fun deleteMark(mark: FieldMark) = state.update { it.filterNot { m -> m.id == mark.id } }
    override suspend fun removeLastMushroomMark(walkId: Long, categoryId: Long): FieldMark? = null
    override suspend fun reassignCategory(oldCategoryId: Long, newCategoryId: Long) =
        state.update { list -> list.map { if (it.categoryId == oldCategoryId) it.copy(categoryId = newCategoryId) else it } }
}

// Pre-Phase-6, import/export never called upsert (only observeAll/getByNameKey to resolve
// categoryId) — a stub that returned category.id without touching state was indistinguishable
// from the real thing. Phase 6's category merge actually needs persistence, so this mirrors
// CategoryRepositoryImpl.upsert: insert (assign an id) when id == 0, update in place otherwise.
private class FakeCategoryRepository(seed: List<Category>) : CategoryRepository {
    private val state = MutableStateFlow(seed)
    private var nextId = (seed.maxOfOrNull { it.id } ?: 0L) + 1
    override fun observeAll(): Flow<List<Category>> = state
    override fun observeActive(): Flow<List<Category>> = state.map { it.filter { c -> c.isActive } }
    override fun observeFilterEligible(): Flow<List<Category>> = state.map { it.filter { c -> c.isFilterEligible } }
    override fun observeNonCatalog(): Flow<List<Category>> =
        state.map { it.filter { c -> c.source != CategorySource.APP } }
    override suspend fun getById(id: Long): Category? = state.value.find { it.id == id }
    override suspend fun getByNameKey(nameKey: String): Category? = state.value.find { it.nameKey == nameKey }
    override suspend fun count(): Int = state.value.size
    override suspend fun getAll(): List<Category> = state.value
    override suspend fun upsert(category: Category): Long {
        if (category.id != 0L) {
            state.update { list -> list.map { if (it.id == category.id) category else it } }
            return category.id
        }
        val id = nextId++
        state.update { it + category.copy(id = id) }
        return id
    }
    override suspend fun upsertAll(categories: List<Category>) = error("not needed")
    override suspend fun delete(category: Category) = state.update { it.filterNot { c -> c.id == category.id } }
}

/** Подборки живут в памяти теста ровно так же, как категории: связь many-to-many держится списком
 * пар, а не таблицей, — этого хватает и экспорту (он читает членство), и импорту (он его пишет). */
private class FakeCollectionRepository(seed: List<MushroomCollection> = emptyList()) : CollectionRepository {
    private val state = MutableStateFlow(seed)
    private val members = MutableStateFlow(emptyList<CategoryCollectionMembership>())
    private var nextId = (seed.maxOfOrNull { it.id } ?: 0L) + 1
    override fun observeAll(): Flow<List<MushroomCollection>> = state
    override fun observeAllMemberships(): Flow<List<CategoryCollectionMembership>> = members
    override suspend fun getAll(): List<MushroomCollection> = state.value
    override suspend fun getByNameKey(nameKey: String): MushroomCollection? =
        state.value.find { it.nameKey == nameKey }
    override suspend fun getById(id: Long): MushroomCollection? = state.value.find { it.id == id }
    override suspend fun countBySource(source: CollectionSource): Int = state.value.count { it.source == source }
    override suspend fun upsert(collection: MushroomCollection): Long {
        if (collection.id != 0L) {
            state.update { list -> list.map { if (it.id == collection.id) collection else it } }
            return collection.id
        }
        val id = nextId++
        state.update { it + collection.copy(id = id) }
        return id
    }
    override suspend fun upsertAll(collections: List<MushroomCollection>) = error("not needed")
    override suspend fun addMember(categoryId: Long, collectionId: Long) {
        members.update { existing ->
            val membership = CategoryCollectionMembership(categoryId, collectionId)
            if (membership in existing) existing else existing + membership
        }
    }
    override suspend fun addMembers(memberships: List<CategoryCollectionMembership>) {
        memberships.forEach { addMember(it.categoryId, it.collectionId) }
    }
    override suspend fun getMemberCategoryIds(collectionId: Long): List<Long> =
        members.value.filter { it.collectionId == collectionId }.map { it.categoryId }
    override suspend fun getMemberCollectionIds(categoryId: Long): List<Long> =
        members.value.filter { it.categoryId == categoryId }.map { it.collectionId }
    override suspend fun countMembers(collectionId: Long): Int =
        members.value.count { it.collectionId == collectionId }
    override suspend fun removeMember(categoryId: Long, collectionId: Long) {
        members.update { it.filterNot { m -> m.categoryId == categoryId && m.collectionId == collectionId } }
    }
    override suspend fun delete(collection: MushroomCollection) {
        state.update { it.filterNot { c -> c.id == collection.id } }
        members.update { it.filterNot { m -> m.collectionId == collection.id } }
    }
}

// Flat at the filesystem root — FakeFileSystem (like a real one) requires a file's parent
// directory to already exist, and production PhotoStorage implementations own creating theirs
// (AndroidPhotoStorage.mkdirs(), IosPhotoStorage's always-existing Documents dir); a fake has no
// such setup step, so it avoids needing a subdirectory at all.
private class FakePhotoStorage : PhotoStorage {
    override fun resolvePath(fileName: String): String = "/$fileName"
}

private fun category(id: Long, nameKey: String) = Category(
    id = id,
    nameKey = nameKey,
    colorHex = "#000000",
    iconRef = null,
    order = 0,
    isActive = true,
)

private fun userCategory(
    id: Long,
    nameKey: String,
    customNames: Map<AppLanguage, String> = mapOf(AppLanguage.RU to "Мой гриб"),
    scientificName: String? = "Mycena mea",
    source: CategorySource = CategorySource.USER,
    iconFile: String? = null,
) = Category(
    id = id,
    nameKey = nameKey,
    colorHex = "#112233",
    iconRef = null,
    order = 500,
    isActive = true,
    isPicked = true,
    isFilterEligible = true,
    source = source,
    customNames = customNames,
    scientificName = scientificName,
    iconFile = iconFile,
)

private fun userCollection(
    id: Long,
    nameKey: String,
    name: String? = null,
    order: Int = 1000,
) = MushroomCollection(id = id, nameKey = nameKey, order = order, source = CollectionSource.USER, name = name)

private const val BOLETUS_NAME_KEY = "boletus_edulis"

class ExportImportRoundTripTest {
    @Test
    fun exportedArchiveImportsAsNewWalksWithResolvedCategoriesAndCopiedPhotos() = runBlocking {
        val sourceFs = FakeFileSystem()
        val photoBytes = ByteArray(500) { it.toByte() }
        sourceFs.createDirectories("/photos".toPath())
        sourceFs.write("/photos/mark.jpg".toPath()) { write(photoBytes) }

        val sourceCategories = FakeCategoryRepository(
            listOf(category(1, BOLETUS_NAME_KEY), category(2, MISC_CATEGORY_NAME_KEY)),
        )
        val walks = FakeWalkRepository()
        val walkId = walks.insert(
            Walk(
                id = 0,
                name = "Утренняя прогулка",
                startTime = 1000,
                endTime = 2000,
                distanceMeters = 1234.5,
                avgSpeed = 1.1,
                startLat = 55.7,
                startLon = 37.6,
                endLat = 55.71,
                endLon = 37.61,
                mushroomCount = 1,
                thumbnailPath = "/thumbnails/walk_1.png",
                description = "Заметки о прогулке",
            ),
        )
        val trackPoints = FakeTrackPointRepository()
        trackPoints.addPoint(TrackPoint(0, walkId, 55.7, 37.6, 1000, 10.0, 0))
        trackPoints.addPoint(TrackPoint(0, walkId, 55.705, 37.605, 1500, null, 1))
        val fieldMarks = FakeFieldMarkRepository()
        fieldMarks.addMark(
            FieldMark(
                0, walkId, categoryId = 1, lat = 55.701, lon = 37.601, timestamp = 1200,
                type = MarkType.MUSHROOM, photoPath = "/photos/mark.jpg", name = null, description = null,
            ),
        )
        fieldMarks.addMark(
            FieldMark(
                0, walkId, categoryId = 2, lat = 55.702, lon = 37.602, timestamp = 1300,
                type = MarkType.POI, photoPath = null, name = "Родник", description = "Чистая вода",
            ),
        )

        val exportUseCase = ExportDataUseCase(
            walks, trackPoints, fieldMarks, sourceCategories, FakeCollectionRepository(),
            FakePhotoStorage(), sourceFs,
        )
        val sink = Buffer()
        exportUseCase(sink)
        val archiveBytes = sink.readByteArray()

        // Import into a fresh set of repositories/filesystem — same category catalog but different
        // ids, simulating a second device.
        val destFs = FakeFileSystem()
        val destCategories = FakeCategoryRepository(
            listOf(category(10, MISC_CATEGORY_NAME_KEY), category(20, BOLETUS_NAME_KEY)),
        )
        val destWalks = FakeWalkRepository()
        val destTrackPoints = FakeTrackPointRepository()
        val destFieldMarks = FakeFieldMarkRepository()
        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            destWalks, destTrackPoints, destFieldMarks, destCategories, FakeCollectionRepository(),
            FakePhotoStorage(), destFs,
        )

        val result = importUseCase(archiveBytes, "(импорт)")
        assertEquals(ImportDataUseCase.Result(importedWalkCount = 1, failedWalkCount = 0), result)

        val walk = destWalks.observeAll().first().single()
        assertEquals("Утренняя прогулка (импорт)", walk.name)
        assertEquals(1000L, walk.startTime)
        assertEquals(1234.5, walk.distanceMeters)
        assertNull(walk.thumbnailPath)
        assertEquals("Заметки о прогулке", walk.description)

        val importedTrack = destTrackPoints.observeByWalkId(walk.id).first().sortedBy { it.sequence }
        assertEquals(2, importedTrack.size)
        assertEquals(listOf(55.7, 55.705), importedTrack.map { it.lat })
        assertEquals(listOf(10.0, null), importedTrack.map { it.elevation })

        val marks = destFieldMarks.observeByWalkId(walk.id).first()
        assertEquals(2, marks.size)

        val mushroom = marks.single { it.type == MarkType.MUSHROOM }
        assertEquals(20L, mushroom.categoryId)
        val mushroomPhotoPath = assertNotNull(mushroom.photoPath)
        assertNotEquals("/photos/mark.jpg", mushroomPhotoPath)
        assertContentEquals(photoBytes, destFs.read(mushroomPhotoPath.toPath()) { readByteArray() })

        val poi = marks.single { it.type == MarkType.POI }
        assertEquals(10L, poi.categoryId)
        assertNull(poi.photoPath)
        assertEquals("Родник", poi.name)
        assertEquals("Чистая вода", poi.description)
    }

    @Test
    fun exportSkipsDanglingPhotoPathWithoutFailing() = runBlocking {
        // Reproduces the "No such file or directory" export failure: a FieldMark whose photoPath
        // no longer exists on disk (e.g. iOS sandbox container UUID changed after a reinstall)
        // must not abort the whole archive — the mark should just export without a photo.
        val sourceFs = FakeFileSystem()
        val categories = FakeCategoryRepository(listOf(category(1, BOLETUS_NAME_KEY)))
        val walks = FakeWalkRepository()
        val walkId = walks.insert(
            Walk(
                id = 0, name = "Прогулка", startTime = 1000, endTime = 2000, distanceMeters = 0.0,
                avgSpeed = 0.0, startLat = 55.7, startLon = 37.6, endLat = null, endLon = null,
                mushroomCount = 1, thumbnailPath = null, description = null,
            ),
        )
        val fieldMarks = FakeFieldMarkRepository()
        fieldMarks.addMark(
            FieldMark(
                0, walkId, categoryId = 1, lat = 55.701, lon = 37.601, timestamp = 1200,
                type = MarkType.MUSHROOM, photoPath = "/photos/missing.jpg", name = null, description = null,
            ),
        )

        val exportUseCase = ExportDataUseCase(
            walks, FakeTrackPointRepository(), fieldMarks, categories, FakeCollectionRepository(),
            FakePhotoStorage(), sourceFs,
        )
        val sink = Buffer()
        exportUseCase(sink)
        val archiveBytes = sink.readByteArray()

        val destFieldMarks = FakeFieldMarkRepository()
        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            FakeWalkRepository(), FakeTrackPointRepository(), destFieldMarks,
            FakeCategoryRepository(listOf(category(1, BOLETUS_NAME_KEY), category(2, MISC_CATEGORY_NAME_KEY))),
            FakeCollectionRepository(), FakePhotoStorage(), FakeFileSystem(),
        )
        importUseCase(archiveBytes, "")

        val mark = destFieldMarks.observeAll().first().single()
        assertNull(mark.photoPath)
    }

    @Test
    fun rejectsArchiveWithNewerSchemaVersion() = runBlocking {
        val sink = Buffer()
        val writer = ZipWriter(sink)
        val futureManifest = ExportManifestDto(schemaVersion = EXPORT_SCHEMA_VERSION + 1, exportedAt = 0, walkCount = 0)
        writer.writeEntry(MANIFEST_ENTRY_NAME, ExportJson.encodeToString(futureManifest).encodeToByteArray())
        writer.finish()

        val categories = FakeCategoryRepository(listOf(category(1, MISC_CATEGORY_NAME_KEY)))
        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(),
            categories, FakeCollectionRepository(), FakePhotoStorage(), FakeFileSystem(),
        )

        assertFailsWith<IllegalArgumentException> { importUseCase(sink.readByteArray(), "") }
        Unit
    }

    @Test
    fun skipsMalformedWalkButImportsTheRest() = runBlocking {
        val categories = FakeCategoryRepository(listOf(category(1, MISC_CATEGORY_NAME_KEY)))

        val sink = Buffer()
        val writer = ZipWriter(sink)
        writer.writeEntry(
            MANIFEST_ENTRY_NAME,
            ExportJson.encodeToString(ExportManifestDto(EXPORT_SCHEMA_VERSION, 0, 2)).encodeToByteArray(),
        )
        writer.writeEntry(
            "${walkDirectory(1)}/$WALK_ENTRY_NAME",
            """{"originalId":1,"name":"Good","startTime":1,"endTime":null,"distanceMeters":0.0,""" +
                """"avgSpeed":0.0,"startLat":0.0,"startLon":0.0,"endLat":null,"endLon":null,"mushroomCount":0}""",
        )
        writer.writeEntry("${walkDirectory(2)}/$WALK_ENTRY_NAME", "not json")
        writer.finish()

        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(),
            categories, FakeCollectionRepository(), FakePhotoStorage(), FakeFileSystem(),
        )
        val result = importUseCase(sink.readByteArray(), "")

        assertEquals(1, result.importedWalkCount)
        assertEquals(1, result.failedWalkCount)
    }

    @Test
    fun newUserSpeciesImportsAsImportedWithIconAndFindsResolveToIt() = runBlocking {
        val sourceFs = FakeFileSystem()
        val iconBytes = ByteArray(64) { it.toByte() }
        sourceFs.write("/catimg_user_1.png".toPath()) { write(iconBytes) }
        val nameKey = "user_1"
        val sourceCategories = FakeCategoryRepository(
            listOf(category(1, MISC_CATEGORY_NAME_KEY), userCategory(2, nameKey, iconFile = "catimg_user_1.png")),
        )
        val walks = FakeWalkRepository()
        val walkId = walks.insert(
            Walk(
                id = 0, name = "Прогулка", startTime = 1000, endTime = 2000, distanceMeters = 0.0,
                avgSpeed = 0.0, startLat = 55.7, startLon = 37.6, endLat = null, endLon = null,
                mushroomCount = 1, thumbnailPath = null, description = null,
            ),
        )
        val fieldMarks = FakeFieldMarkRepository()
        fieldMarks.addMark(
            FieldMark(
                0, walkId, categoryId = 2, lat = 55.701, lon = 37.601, timestamp = 1200,
                type = MarkType.MUSHROOM, photoPath = null, name = null, description = null,
            ),
        )

        val exportUseCase = ExportDataUseCase(
            walks, FakeTrackPointRepository(), fieldMarks, sourceCategories, FakeCollectionRepository(),
            FakePhotoStorage(), sourceFs,
        )
        val sink = Buffer()
        exportUseCase(sink)
        val archiveBytes = sink.readByteArray()

        val destFs = FakeFileSystem()
        val destCategories = FakeCategoryRepository(listOf(category(10, MISC_CATEGORY_NAME_KEY)))
        val destFieldMarks = FakeFieldMarkRepository()
        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            FakeWalkRepository(), FakeTrackPointRepository(), destFieldMarks, destCategories,
            FakeCollectionRepository(), FakePhotoStorage(), destFs,
        )
        importUseCase(archiveBytes, "")

        val imported = destCategories.observeAll().first().single { it.nameKey == nameKey }
        assertEquals(CategorySource.IMPORTED, imported.source)
        assertEquals(true, imported.isActive)
        assertEquals(true, imported.isPicked)
        assertEquals("Мой гриб", imported.customNames[AppLanguage.RU])
        val iconFile = assertNotNull(imported.iconFile)
        assertContentEquals(iconBytes, destFs.read(FakePhotoStorage().resolvePath(iconFile).toPath()) { readByteArray() })

        val mark = destFieldMarks.observeAll().first().single()
        assertEquals(imported.id, mark.categoryId)
    }

    @Test
    fun importAttachesIconToExistingSpeciesWithoutOverwritingItsFields() = runBlocking {
        val sourceFs = FakeFileSystem()
        val iconBytes = ByteArray(32) { it.toByte() }
        sourceFs.write("/catimg_user_2.png".toPath()) { write(iconBytes) }
        val nameKey = "user_2"
        val sourceCategories = FakeCategoryRepository(
            listOf(
                category(1, MISC_CATEGORY_NAME_KEY),
                userCategory(
                    2, nameKey,
                    customNames = mapOf(AppLanguage.RU to "Приезжее имя"),
                    iconFile = "catimg_user_2.png",
                ),
            ),
        )
        val walks = FakeWalkRepository()
        val walkId = walks.insert(
            Walk(
                id = 0, name = "П", startTime = 1, endTime = null, distanceMeters = 0.0, avgSpeed = 0.0,
                startLat = 0.0, startLon = 0.0, endLat = null, endLon = null, mushroomCount = 1, thumbnailPath = null, description = null,
            ),
        )
        val fieldMarks = FakeFieldMarkRepository()
        fieldMarks.addMark(
            FieldMark(
                0, walkId, categoryId = 2, lat = 0.0, lon = 0.0, timestamp = 1,
                type = MarkType.MUSHROOM, photoPath = null, name = null, description = null,
            ),
        )

        val exportUseCase = ExportDataUseCase(
            walks, FakeTrackPointRepository(), fieldMarks, sourceCategories, FakeCollectionRepository(),
            FakePhotoStorage(), sourceFs,
        )
        val sink = Buffer()
        exportUseCase(sink)
        val archiveBytes = sink.readByteArray()

        val destFs = FakeFileSystem()
        val existingLocal = userCategory(
            20, nameKey,
            customNames = mapOf(AppLanguage.RU to "Местное имя"),
            source = CategorySource.USER,
            iconFile = null,
        )
        val destCategories = FakeCategoryRepository(listOf(category(10, MISC_CATEGORY_NAME_KEY), existingLocal))
        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(),
            destCategories, FakeCollectionRepository(), FakePhotoStorage(), destFs,
        )
        importUseCase(archiveBytes, "")

        val merged = destCategories.observeAll().first().single { it.nameKey == nameKey }
        assertEquals(20L, merged.id)
        assertEquals(CategorySource.USER, merged.source)
        assertEquals("Местное имя", merged.customNames[AppLanguage.RU])
        val iconFile = assertNotNull(merged.iconFile)
        assertContentEquals(iconBytes, destFs.read(FakePhotoStorage().resolvePath(iconFile).toPath()) { readByteArray() })
    }

    @Test
    fun importLeavesExistingSpeciesWithIconCompletelyUntouched() = runBlocking {
        val sourceFs = FakeFileSystem()
        sourceFs.write("/catimg_user_3.png".toPath()) { write(ByteArray(10) { 9 }) }
        val nameKey = "user_3"
        val sourceCategories = FakeCategoryRepository(
            listOf(category(1, MISC_CATEGORY_NAME_KEY), userCategory(2, nameKey, iconFile = "catimg_user_3.png")),
        )
        val walks = FakeWalkRepository()
        val walkId = walks.insert(
            Walk(
                id = 0, name = "П", startTime = 1, endTime = null, distanceMeters = 0.0, avgSpeed = 0.0,
                startLat = 0.0, startLon = 0.0, endLat = null, endLon = null, mushroomCount = 1, thumbnailPath = null, description = null,
            ),
        )
        val fieldMarks = FakeFieldMarkRepository()
        fieldMarks.addMark(
            FieldMark(
                0, walkId, categoryId = 2, lat = 0.0, lon = 0.0, timestamp = 1,
                type = MarkType.MUSHROOM, photoPath = null, name = null, description = null,
            ),
        )

        val exportUseCase = ExportDataUseCase(
            walks, FakeTrackPointRepository(), fieldMarks, sourceCategories, FakeCollectionRepository(),
            FakePhotoStorage(), sourceFs,
        )
        val sink = Buffer()
        exportUseCase(sink)
        val archiveBytes = sink.readByteArray()

        val destFs = FakeFileSystem()
        destFs.write("/catimg_user_3_local.png".toPath()) { write(ByteArray(5) { 1 }) }
        val existingLocal = userCategory(20, nameKey, iconFile = "catimg_user_3_local.png")
        val destCategories = FakeCategoryRepository(listOf(category(10, MISC_CATEGORY_NAME_KEY), existingLocal))
        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(),
            destCategories, FakeCollectionRepository(), FakePhotoStorage(), destFs,
        )
        importUseCase(archiveBytes, "")

        val merged = destCategories.observeAll().first().single { it.nameKey == nameKey }
        assertEquals("catimg_user_3_local.png", merged.iconFile)
    }

    @Test
    fun repeatedImportOfSameArchiveDuplicatesWalksButNotSpecies() = runBlocking {
        val sourceFs = FakeFileSystem()
        sourceFs.write("/catimg_user_4.png".toPath()) { write(ByteArray(4) { 2 }) }
        val nameKey = "user_4"
        val sourceCategories = FakeCategoryRepository(
            listOf(category(1, MISC_CATEGORY_NAME_KEY), userCategory(2, nameKey, iconFile = "catimg_user_4.png")),
        )
        val walks = FakeWalkRepository()
        val walkId = walks.insert(
            Walk(
                id = 0, name = "П", startTime = 1, endTime = null, distanceMeters = 0.0, avgSpeed = 0.0,
                startLat = 0.0, startLon = 0.0, endLat = null, endLon = null, mushroomCount = 1, thumbnailPath = null, description = null,
            ),
        )
        val fieldMarks = FakeFieldMarkRepository()
        fieldMarks.addMark(
            FieldMark(
                0, walkId, categoryId = 2, lat = 0.0, lon = 0.0, timestamp = 1,
                type = MarkType.MUSHROOM, photoPath = null, name = null, description = null,
            ),
        )

        val exportUseCase = ExportDataUseCase(
            walks, FakeTrackPointRepository(), fieldMarks, sourceCategories, FakeCollectionRepository(),
            FakePhotoStorage(), sourceFs,
        )
        val sink = Buffer()
        exportUseCase(sink)
        val archiveBytes = sink.readByteArray()

        val destCategories = FakeCategoryRepository(listOf(category(10, MISC_CATEGORY_NAME_KEY)))
        val destWalks = FakeWalkRepository()
        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            destWalks, FakeTrackPointRepository(), FakeFieldMarkRepository(),
            destCategories, FakeCollectionRepository(), FakePhotoStorage(), FakeFileSystem(),
        )
        importUseCase(archiveBytes, "")
        importUseCase(archiveBytes, "")

        assertEquals(2, destWalks.observeAll().first().size)
        assertEquals(1, destCategories.observeAll().first().count { it.nameKey == nameKey })
    }

    @Test
    fun importsOldArchiveWithoutCategoriesFolder() = runBlocking {
        val categories = FakeCategoryRepository(listOf(category(1, MISC_CATEGORY_NAME_KEY)))

        val sink = Buffer()
        val writer = ZipWriter(sink)
        writer.writeEntry(
            MANIFEST_ENTRY_NAME,
            ExportJson.encodeToString(ExportManifestDto(schemaVersion = 1, exportedAt = 0, walkCount = 1)).encodeToByteArray(),
        )
        writer.writeEntry(
            "${walkDirectory(1)}/$WALK_ENTRY_NAME",
            """{"originalId":1,"name":"Old","startTime":1,"endTime":null,"distanceMeters":0.0,""" +
                """"avgSpeed":0.0,"startLat":0.0,"startLon":0.0,"endLat":null,"endLon":null,"mushroomCount":0}""",
        )
        writer.finish()

        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(),
            categories, FakeCollectionRepository(), FakePhotoStorage(), FakeFileSystem(),
        )
        val result = importUseCase(sink.readByteArray(), "")

        assertEquals(1, result.importedWalkCount)
        assertEquals(0, result.failedWalkCount)
    }
    // --- Проверка архива перед импортом (задача 10) -------------------------------------------

    private fun archiveOf(vararg entries: Pair<String, String>): ByteArray {
        val sink = Buffer()
        val writer = ZipWriter(sink)
        entries.forEach { (name, content) -> writer.writeEntry(name, content) }
        writer.finish()
        return sink.readByteArray()
    }

    private fun manifestEntry(schemaVersion: Int = EXPORT_SCHEMA_VERSION, walkCount: Int = 1) =
        MANIFEST_ENTRY_NAME to ExportJson.encodeToString(ExportManifestDto(schemaVersion, 0, walkCount))

    private fun goodWalkEntry(id: Long) = "${walkDirectory(id)}/$WALK_ENTRY_NAME" to
        """{"originalId":$id,"name":"W$id","startTime":1,"endTime":null,"distanceMeters":0.0,""" +
        """"avgSpeed":0.0,"startLat":0.0,"startLon":0.0,"endLat":null,"endLon":null,"mushroomCount":0}"""

    @Test
    fun rejectsFilesThatArentArchivesAtAll() {
        val validate = ValidateImportArchiveUseCase()
        assertEquals(ImportArchiveProblem.NOT_AN_ARCHIVE, validate(ByteArray(0)))
        assertEquals(ImportArchiveProblem.NOT_AN_ARCHIVE, validate("совсем не архив".encodeToByteArray()))
        // Правдоподобный «почти zip»: сигнатура на месте, дальше мусор.
        assertEquals(
            ImportArchiveProblem.NOT_AN_ARCHIVE,
            validate(byteArrayOf(0x50, 0x4B, 0x03, 0x04) + ByteArray(200) { 0x7F }),
        )
    }

    @Test
    fun rejectsAZipThatIsntALeshyArchive() {
        val validate = ValidateImportArchiveUseCase()
        assertEquals(
            ImportArchiveProblem.NOT_A_LESHY_ARCHIVE,
            validate(archiveOf("readme.txt" to "чужой архив")),
        )
        assertEquals(
            ImportArchiveProblem.NOT_A_LESHY_ARCHIVE,
            validate(archiveOf(MANIFEST_ENTRY_NAME to "{ это не манифест")),
        )
    }

    @Test
    fun rejectsAnArchiveFromANewerAppVersion() {
        assertEquals(
            ImportArchiveProblem.NEWER_FORMAT,
            ValidateImportArchiveUseCase()(
                archiveOf(manifestEntry(schemaVersion = EXPORT_SCHEMA_VERSION + 1), goodWalkEntry(1)),
            ),
        )
    }

    @Test
    fun rejectsAnArchiveWithNothingToImport() {
        assertEquals(
            ImportArchiveProblem.NO_WALKS,
            ValidateImportArchiveUseCase()(archiveOf(manifestEntry(walkCount = 0))),
        )
    }

    @Test
    fun rejectsAnArchiveWhoseEveryWalkIsBrokenButAcceptsOneGoodWalkAmongBad() {
        val validate = ValidateImportArchiveUseCase()
        assertEquals(
            ImportArchiveProblem.DAMAGED_CONTENT,
            validate(
                archiveOf(
                    manifestEntry(walkCount = 2),
                    "${walkDirectory(1)}/$WALK_ENTRY_NAME" to "not json",
                    "${walkDirectory(2)}/$WALK_ENTRY_NAME" to "also not json",
                ),
            ),
        )
        // Одна битая прогулка среди целых — не повод выбрасывать целые: их импорт пропускает
        // поштучно и отчитывается failedWalkCount, см. doc ValidateImportArchiveUseCase.
        assertNull(
            validate(
                archiveOf(
                    manifestEntry(walkCount = 2),
                    goodWalkEntry(1),
                    "${walkDirectory(2)}/$WALK_ENTRY_NAME" to "not json",
                ),
            ),
        )
    }

    @Test
    fun rejectsAnArchiveWithAnUnreadableCategoriesSection() {
        assertEquals(
            ImportArchiveProblem.DAMAGED_CONTENT,
            ValidateImportArchiveUseCase()(
                archiveOf(manifestEntry(), CATEGORIES_ENTRY_NAME to "{{{", goodWalkEntry(1)),
            ),
        )
    }

    @Test
    fun aRejectedArchiveWritesNothingAtAll() = runBlocking {
        val walks = FakeWalkRepository()
        val categories = FakeCategoryRepository(listOf(category(1, MISC_CATEGORY_NAME_KEY)))
        val marks = FakeFieldMarkRepository()
        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            walks, FakeTrackPointRepository(), marks, categories, FakeCollectionRepository(),
            FakePhotoStorage(), FakeFileSystem(),
        )

        // Валидный манифест и валидная секция видов, но НИ ОДНОЙ читаемой прогулки: без
        // предварительной проверки импорт успел бы слить виды в каталог до первой прогулки.
        val archive = archiveOf(
            manifestEntry(walkCount = 1),
            CATEGORIES_ENTRY_NAME to
                """[{"nameKey":"user_9","colorHex":"#ABCDEF","customNames":{"ru":"Чужой"},""" +
                """"scientificName":null,"hasIcon":false}]""",
            "${walkDirectory(1)}/$WALK_ENTRY_NAME" to "not json",
        )

        val failure = assertFailsWith<ImportDataUseCase.RejectedException> { importUseCase(archive, "") }
        assertEquals(ImportArchiveProblem.DAMAGED_CONTENT, failure.problem)
        assertEquals(emptyList(), walks.observeAll().first())
        assertEquals(emptyList(), marks.observeAll().first())
        assertEquals(listOf(MISC_CATEGORY_NAME_KEY), categories.getAll().map { it.nameKey })
    }

    // --- Конфликты с уже имеющимися данными (задача 10) --------------------------------------

    @Test
    fun importingTheSameArchiveTwiceNeverMergesWalks() = runBlocking {
        val categories = FakeCategoryRepository(listOf(category(1, MISC_CATEGORY_NAME_KEY)))
        val walks = FakeWalkRepository()
        // Прогулка, уже лежащая в базе, с тем же originalId и именем, что в архиве.
        walks.insert(
            Walk(
                id = 0, name = "W1", startTime = 1, endTime = null, distanceMeters = 0.0, avgSpeed = 0.0,
                startLat = 0.0, startLon = 0.0, endLat = null, endLon = null, mushroomCount = 0,
                thumbnailPath = null, description = null,
            ),
        )
        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            walks, FakeTrackPointRepository(), FakeFieldMarkRepository(), categories,
            FakeCollectionRepository(), FakePhotoStorage(), FakeFileSystem(),
        )
        val archive = archiveOf(manifestEntry(), goodWalkEntry(1))

        importUseCase(archive, "")
        importUseCase(archive, "")

        // Ни слияния, ни перезаписи: три отдельные строки с разными id.
        val all = walks.observeAll().first()
        assertEquals(3, all.size)
        assertEquals(3, all.map { it.id }.distinct().size)
    }

    @Test
    fun importNeverTouchesCatalogSpecies() = runBlocking {
        val catalogRow = category(1, BOLETUS_NAME_KEY).copy(source = CategorySource.APP, colorHex = "#AAAAAA")
        val categories = FakeCategoryRepository(listOf(category(2, MISC_CATEGORY_NAME_KEY), catalogRow))
        val importUseCase = ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(), categories,
            FakeCollectionRepository(), FakePhotoStorage(), FakeFileSystem(),
        )
        // Архив, выгруженный этим приложением, каталожных видов не содержит — но nameKey в JSON
        // всего лишь строка, и правленый/битый архив может назвать каталожный ключ своим.
        val archive = archiveOf(
            manifestEntry(),
            CATEGORIES_ENTRY_NAME to
                """[{"nameKey":"$BOLETUS_NAME_KEY","colorHex":"#FF0000","customNames":{"ru":"Подделка"},""" +
                """"scientificName":"Fake fake","hasIcon":false}]""",
            goodWalkEntry(1),
        )

        importUseCase(archive, "")

        assertEquals(catalogRow, categories.getByNameKey(BOLETUS_NAME_KEY))
    }

    /** Ради этого подборки и попали в архив: на новом устройстве грибы обязаны разложиться так же,
     * как их разложили руками на старом (`.claude/plans/user-collections.md`). */
    @Test
    fun userCollectionTravelsWithItsSpecies() = runBlocking {
        val nameKey = "user_c1"
        val sourceCategories = FakeCategoryRepository(
            listOf(category(1, MISC_CATEGORY_NAME_KEY), userCategory(2, nameKey)),
        )
        val sourceCollections = FakeCollectionRepository(
            listOf(userCollection(id = 1, nameKey = "usercoll_1", name = "Мои белые")),
        )
        sourceCollections.addMember(categoryId = 2, collectionId = 1)

        val archiveBytes = exportSingleMarkArchive(sourceCategories, sourceCollections, categoryId = 2)

        val destCategories = FakeCategoryRepository(listOf(category(10, MISC_CATEGORY_NAME_KEY)))
        val destCollections = FakeCollectionRepository()
        importArchive(archiveBytes, destCategories, destCollections)

        val imported = destCategories.observeAll().first().single { it.nameKey == nameKey }
        val collection = destCollections.getAll().single()
        assertEquals("Мои белые", collection.name)
        assertEquals(CollectionSource.IMPORTED, collection.source)
        assertEquals(listOf(imported.id), destCollections.getMemberCategoryIds(collection.id))
    }

    /** У «Других» ключ фиксированный, поэтому приезжие «Другие» обязаны влиться в местные, а не
     * встать рядом второй подборкой с тем же названием. */
    @Test
    fun otherCollectionsFromTwoDevicesMergeIntoOne() = runBlocking {
        val sourceCategories = FakeCategoryRepository(
            listOf(category(1, MISC_CATEGORY_NAME_KEY), userCategory(2, "user_c2")),
        )
        val sourceCollections = FakeCollectionRepository(
            listOf(userCollection(id = 1, nameKey = OTHER_COLLECTION_NAME_KEY, order = 2000)),
        )
        sourceCollections.addMember(categoryId = 2, collectionId = 1)

        val archiveBytes = exportSingleMarkArchive(sourceCategories, sourceCollections, categoryId = 2)

        val local = userCategory(
            11, "user_local",
            customNames = mapOf(AppLanguage.RU to "Местный"),
            scientificName = "Mycena localis",
        )
        val destCategories = FakeCategoryRepository(listOf(category(10, MISC_CATEGORY_NAME_KEY), local))
        val destCollections = FakeCollectionRepository(
            listOf(userCollection(id = 5, nameKey = OTHER_COLLECTION_NAME_KEY, order = 2000)),
        )
        destCollections.addMember(categoryId = 11, collectionId = 5)

        importArchive(archiveBytes, destCategories, destCollections)

        val collection = destCollections.getAll().single()
        assertEquals(5L, collection.id)
        val imported = destCategories.observeAll().first().single { it.nameKey == "user_c2" }
        assertEquals(setOf(11L, imported.id), destCollections.getMemberCategoryIds(collection.id).toSet())
    }

    /** Вид, слившийся с местным по названию и латыни, остаётся в СВОЕЙ подборке: местная раскладка
     * старше приезжей. Приехавшая подборка при этом не остаётся пустой строкой в базе. */
    @Test
    fun importKeepsALocalSpeciesInItsOwnCollection() = runBlocking {
        val names = mapOf(AppLanguage.RU to "Одинаковый")
        val sourceCategories = FakeCategoryRepository(
            listOf(
                category(1, MISC_CATEGORY_NAME_KEY),
                userCategory(2, "user_from_archive", customNames = names, scientificName = "Mycena gemina"),
            ),
        )
        val sourceCollections = FakeCollectionRepository(
            listOf(userCollection(id = 1, nameKey = "usercoll_2", name = "Приезжие")),
        )
        sourceCollections.addMember(categoryId = 2, collectionId = 1)

        val archiveBytes = exportSingleMarkArchive(sourceCategories, sourceCollections, categoryId = 2)

        val local = userCategory(11, "user_local_same", customNames = names, scientificName = "Mycena gemina")
        val destCategories = FakeCategoryRepository(listOf(category(10, MISC_CATEGORY_NAME_KEY), local))
        val destCollections = FakeCollectionRepository(
            listOf(userCollection(id = 5, nameKey = "usercoll_local", name = "Свои")),
        )
        destCollections.addMember(categoryId = 11, collectionId = 5)

        importArchive(archiveBytes, destCategories, destCollections)

        // Вид один: слились по названию + латыни, а не завелись вторым «Одинаковым».
        assertEquals(1, destCategories.observeAll().first().count { it.source != CategorySource.APP })
        val collection = destCollections.getAll().single()
        assertEquals("Свои", collection.name)
        assertEquals(listOf(11L), destCollections.getMemberCategoryIds(collection.id))
    }

    /** Общая обвязка трёх тестов выше: одна прогулка с одной находкой указанного вида. */
    private suspend fun exportSingleMarkArchive(
        categories: FakeCategoryRepository,
        collections: FakeCollectionRepository,
        categoryId: Long,
    ): ByteArray {
        val walks = FakeWalkRepository()
        val walkId = walks.insert(
            Walk(
                id = 0, name = "Прогулка", startTime = 1000, endTime = 2000, distanceMeters = 0.0,
                avgSpeed = 0.0, startLat = 55.7, startLon = 37.6, endLat = null, endLon = null,
                mushroomCount = 1, thumbnailPath = null, description = null,
            ),
        )
        val fieldMarks = FakeFieldMarkRepository()
        fieldMarks.addMark(
            FieldMark(
                0, walkId, categoryId = categoryId, lat = 55.701, lon = 37.601, timestamp = 1200,
                type = MarkType.MUSHROOM, photoPath = null, name = null, description = null,
            ),
        )
        val exportUseCase = ExportDataUseCase(
            walks, FakeTrackPointRepository(), fieldMarks, categories, collections,
            FakePhotoStorage(), FakeFileSystem(),
        )
        val sink = Buffer()
        exportUseCase(sink)
        return sink.readByteArray()
    }

    private suspend fun importArchive(
        archiveBytes: ByteArray,
        categories: FakeCategoryRepository,
        collections: FakeCollectionRepository,
    ) {
        ImportDataUseCase(
            ValidateImportArchiveUseCase(),
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(), categories, collections,
            FakePhotoStorage(), FakeFileSystem(),
        )(archiveBytes, "")
    }
}

private fun ZipWriter.writeEntry(name: String, text: String) = writeEntry(name, text.encodeToByteArray())
