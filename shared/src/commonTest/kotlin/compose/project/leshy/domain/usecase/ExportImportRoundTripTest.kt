package compose.project.leshy.domain.usecase

import compose.project.leshy.data.export.dto.EXPORT_SCHEMA_VERSION
import compose.project.leshy.data.export.dto.ExportJson
import compose.project.leshy.data.export.dto.ExportManifestDto
import compose.project.leshy.data.export.dto.MANIFEST_ENTRY_NAME
import compose.project.leshy.data.export.dto.WALK_ENTRY_NAME
import compose.project.leshy.data.export.dto.walkDirectory
import compose.project.leshy.data.export.zip.ZipWriter
import compose.project.leshy.data.platform.PhotoStorage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.EdibilityStatus
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.model.TrackPoint
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.TrackPointRepository
import compose.project.leshy.domain.repository.WalkRepository
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
    override suspend fun removeLastMushroomMark(walkId: Long, categoryId: Long) = false
}

private class FakeCategoryRepository(seed: List<Category>) : CategoryRepository {
    private val state = MutableStateFlow(seed)
    override fun observeAll(): Flow<List<Category>> = state
    override fun observeActive(): Flow<List<Category>> = state.map { it.filter { c -> c.isActive } }
    override fun observeFilterEligible(): Flow<List<Category>> = state.map { it.filter { c -> c.isFilterEligible } }
    override suspend fun getById(id: Long): Category? = state.value.find { it.id == id }
    override suspend fun getByNameKey(nameKey: String): Category? = state.value.find { it.nameKey == nameKey }
    override suspend fun count(): Int = state.value.size
    override suspend fun upsert(category: Category): Long = category.id
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
    edibilityStatus = EdibilityStatus.EDIBLE,
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

        val exportUseCase = ExportDataUseCase(walks, trackPoints, fieldMarks, sourceCategories, sourceFs)
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
}

private fun ZipWriter.writeEntry(name: String, text: String) = writeEntry(name, text.encodeToByteArray())
