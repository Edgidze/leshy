package leshy.mushrooms.map.domain.usecase

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
import leshy.mushrooms.map.domain.repository.CategoryRepository
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

        val exportUseCase = ExportDataUseCase(walks, trackPoints, fieldMarks, sourceCategories, FakePhotoStorage(), sourceFs)
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
            destWalks, destTrackPoints, destFieldMarks, destCategories, FakePhotoStorage(), destFs,
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

        val exportUseCase = ExportDataUseCase(walks, FakeTrackPointRepository(), fieldMarks, categories, FakePhotoStorage(), sourceFs)
        val sink = Buffer()
        exportUseCase(sink)
        val archiveBytes = sink.readByteArray()

        val destFieldMarks = FakeFieldMarkRepository()
        val importUseCase = ImportDataUseCase(
            FakeWalkRepository(), FakeTrackPointRepository(), destFieldMarks,
            FakeCategoryRepository(listOf(category(1, BOLETUS_NAME_KEY), category(2, MISC_CATEGORY_NAME_KEY))),
            FakePhotoStorage(), FakeFileSystem(),
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
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(),
            categories, FakePhotoStorage(), FakeFileSystem(),
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
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(),
            categories, FakePhotoStorage(), FakeFileSystem(),
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
            walks, FakeTrackPointRepository(), fieldMarks, sourceCategories, FakePhotoStorage(), sourceFs,
        )
        val sink = Buffer()
        exportUseCase(sink)
        val archiveBytes = sink.readByteArray()

        val destFs = FakeFileSystem()
        val destCategories = FakeCategoryRepository(listOf(category(10, MISC_CATEGORY_NAME_KEY)))
        val destFieldMarks = FakeFieldMarkRepository()
        val importUseCase = ImportDataUseCase(
            FakeWalkRepository(), FakeTrackPointRepository(), destFieldMarks, destCategories, FakePhotoStorage(), destFs,
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
            walks, FakeTrackPointRepository(), fieldMarks, sourceCategories, FakePhotoStorage(), sourceFs,
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
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(),
            destCategories, FakePhotoStorage(), destFs,
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
            walks, FakeTrackPointRepository(), fieldMarks, sourceCategories, FakePhotoStorage(), sourceFs,
        )
        val sink = Buffer()
        exportUseCase(sink)
        val archiveBytes = sink.readByteArray()

        val destFs = FakeFileSystem()
        destFs.write("/catimg_user_3_local.png".toPath()) { write(ByteArray(5) { 1 }) }
        val existingLocal = userCategory(20, nameKey, iconFile = "catimg_user_3_local.png")
        val destCategories = FakeCategoryRepository(listOf(category(10, MISC_CATEGORY_NAME_KEY), existingLocal))
        val importUseCase = ImportDataUseCase(
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(),
            destCategories, FakePhotoStorage(), destFs,
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
            walks, FakeTrackPointRepository(), fieldMarks, sourceCategories, FakePhotoStorage(), sourceFs,
        )
        val sink = Buffer()
        exportUseCase(sink)
        val archiveBytes = sink.readByteArray()

        val destCategories = FakeCategoryRepository(listOf(category(10, MISC_CATEGORY_NAME_KEY)))
        val destWalks = FakeWalkRepository()
        val importUseCase = ImportDataUseCase(
            destWalks, FakeTrackPointRepository(), FakeFieldMarkRepository(),
            destCategories, FakePhotoStorage(), FakeFileSystem(),
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
            FakeWalkRepository(), FakeTrackPointRepository(), FakeFieldMarkRepository(),
            categories, FakePhotoStorage(), FakeFileSystem(),
        )
        val result = importUseCase(sink.readByteArray(), "")

        assertEquals(1, result.importedWalkCount)
        assertEquals(0, result.failedWalkCount)
    }
}

private fun ZipWriter.writeEntry(name: String, text: String) = writeEntry(name, text.encodeToByteArray())
