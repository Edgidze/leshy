package compose.project.leshy.domain.usecase

import compose.project.leshy.data.export.dto.EXPORT_SCHEMA_VERSION
import compose.project.leshy.data.export.dto.ExportJson
import compose.project.leshy.data.export.dto.ExportManifestDto
import compose.project.leshy.data.export.dto.MANIFEST_ENTRY_NAME
import compose.project.leshy.data.export.dto.OBJECTS_ENTRY_NAME
import compose.project.leshy.data.export.dto.ObjectExportDto
import compose.project.leshy.data.export.dto.TRACK_ENTRY_NAME
import compose.project.leshy.data.export.dto.TrackPointExportDto
import compose.project.leshy.data.export.dto.WALK_ENTRY_NAME
import compose.project.leshy.data.export.dto.WalkExportDto
import compose.project.leshy.data.export.dto.photoEntryName
import compose.project.leshy.data.export.dto.walkDirectory
import compose.project.leshy.data.export.zip.ZipWriter
import compose.project.leshy.data.platform.currentTimeMillis
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.TrackPoint
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.TrackPointRepository
import compose.project.leshy.domain.repository.WalkRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import okio.BufferedSink
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Writes every walk (track + finds + photos) to [sink] as a zip archive — see
 * `.claude/plans/export-import.md` for the archive layout. Categories/collections aren't
 * exported: they're a fixed catalog reseeded on every install ([EnsureDefaultCategoriesUseCase]),
 * finds only need [Category.nameKey] to be re-resolved against whatever catalog exists on import.
 */
class ExportDataUseCase(
    private val walkRepository: WalkRepository,
    private val trackPointRepository: TrackPointRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val categoryRepository: CategoryRepository,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    /** [walkIds], if non-null, restricts the archive to those walks — see the export walks picker
     * (`WalksPickerDialog`/`DataViewModel`). `null` exports every walk (also what every existing
     * caller/test relied on before the picker was added). */
    suspend operator fun invoke(sink: BufferedSink, walkIds: Set<Long>? = null) {
        val allWalks = walkRepository.observeAll().first()
        val walks = if (walkIds == null) allWalks else allWalks.filter { it.id in walkIds }
        val nameKeyByCategoryId = categoryRepository.observeAll().first().associate { it.id to it.nameKey }

        val writer = ZipWriter(sink)
        val manifest = ExportManifestDto(
            schemaVersion = EXPORT_SCHEMA_VERSION,
            exportedAt = currentTimeMillis(),
            walkCount = walks.size,
        )
        writer.writeEntry(MANIFEST_ENTRY_NAME, ExportJson.encodeToString(manifest).encodeToByteArray())

        for (walk in walks) writeWalk(writer, walk, nameKeyByCategoryId)
        writer.finish()
    }

    private suspend fun writeWalk(writer: ZipWriter, walk: Walk, nameKeyByCategoryId: Map<Long, String>) {
        val dir = walkDirectory(walk.id)
        writer.writeEntry("$dir/$WALK_ENTRY_NAME", ExportJson.encodeToString(walk.toExportDto()).encodeToByteArray())

        val track = trackPointRepository.observeByWalkId(walk.id).first().sortedBy { it.sequence }
        writer.writeEntry(
            "$dir/$TRACK_ENTRY_NAME",
            ExportJson.encodeToString(ListSerializer(TrackPointExportDto.serializer()), track.map { it.toExportDto() })
                .encodeToByteArray(),
        )

        val marks = fieldMarkRepository.observeByWalkId(walk.id).first()
        val objectDtos = marks.map { mark ->
            val categoryNameKey = nameKeyByCategoryId[mark.categoryId] ?: MISC_CATEGORY_NAME_KEY
            val photoFile = mark.photoPath?.let { path ->
                val extension = path.substringAfterLast('.', "jpg")
                val entryName = photoEntryName(mark.id, extension)
                writer.writeEntry("$dir/$entryName", fileSystem.read(path.toPath()) { readByteArray() })
                entryName
            }
            mark.toExportDto(categoryNameKey, photoFile)
        }
        writer.writeEntry(
            "$dir/$OBJECTS_ENTRY_NAME",
            ExportJson.encodeToString(ListSerializer(ObjectExportDto.serializer()), objectDtos).encodeToByteArray(),
        )
    }
}

private fun Walk.toExportDto() = WalkExportDto(
    originalId = id,
    name = name,
    startTime = startTime,
    endTime = endTime,
    distanceMeters = distanceMeters,
    avgSpeed = avgSpeed,
    startLat = startLat,
    startLon = startLon,
    endLat = endLat,
    endLon = endLon,
    mushroomCount = mushroomCount,
)

private fun TrackPoint.toExportDto() = TrackPointExportDto(
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    elevation = elevation,
    sequence = sequence,
)

private fun FieldMark.toExportDto(categoryNameKey: String, photoFile: String?) = ObjectExportDto(
    categoryNameKey = categoryNameKey,
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    type = type.name,
    photoFile = photoFile,
    name = name,
    description = description,
)
