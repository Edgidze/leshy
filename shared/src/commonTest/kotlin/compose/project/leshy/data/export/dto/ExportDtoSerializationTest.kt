package compose.project.leshy.data.export.dto

import kotlinx.serialization.encodeToString
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportDtoSerializationTest {
    @Test
    fun roundTripsManifest() {
        val manifest = ExportManifestDto(
            schemaVersion = EXPORT_SCHEMA_VERSION,
            appVersion = "1.2.3",
            exportedAt = 1_755_000_000_000,
            walkCount = 2,
        )
        val json = ExportJson.encodeToString(manifest)
        assertEquals(manifest, ExportJson.decodeFromString<ExportManifestDto>(json))
    }

    @Test
    fun roundTripsWalkWithNullableFields() {
        val walk = WalkExportDto(
            originalId = 42,
            name = "Утренняя прогулка",
            startTime = 1000,
            endTime = null,
            distanceMeters = 1234.5,
            avgSpeed = 1.1,
            startLat = 55.7,
            startLon = 37.6,
            endLat = null,
            endLon = null,
            mushroomCount = 3,
        )
        val json = ExportJson.encodeToString(walk)
        assertEquals(walk, ExportJson.decodeFromString<WalkExportDto>(json))
    }

    @Test
    fun roundTripsTrackPointList() {
        val points = listOf(
            TrackPointExportDto(lat = 1.0, lon = 2.0, timestamp = 100, elevation = 10.5, sequence = 0),
            TrackPointExportDto(lat = 1.1, lon = 2.1, timestamp = 200, elevation = null, sequence = 1),
        )
        val serializer = ListSerializer(TrackPointExportDto.serializer())
        val json = ExportJson.encodeToString(serializer, points)
        assertEquals(points, ExportJson.decodeFromString(serializer, json))
    }

    @Test
    fun roundTripsObjectWithAndWithoutPhoto() {
        val objects = listOf(
            ObjectExportDto(
                categoryNameKey = "boletus_edulis",
                lat = 1.0,
                lon = 2.0,
                timestamp = 100,
                type = "MUSHROOM",
                photoFile = photoEntryName(originalObjectId = 7, extension = "jpg"),
                name = null,
                description = null,
            ),
            ObjectExportDto(
                categoryNameKey = "category_misc",
                lat = 1.1,
                lon = 2.1,
                timestamp = 200,
                type = "POI",
                photoFile = null,
                name = "Родник",
                description = "Чистая вода",
            ),
        )
        val serializer = ListSerializer(ObjectExportDto.serializer())
        val json = ExportJson.encodeToString(serializer, objects)
        assertEquals(objects, ExportJson.decodeFromString(serializer, json))
    }

    @Test
    fun ignoresUnknownFieldsForForwardCompatibility() {
        val futureManifestJson = """
            {"schemaVersion":1,"appVersion":"9.9.9","exportedAt":1,"walkCount":0,"newField":"x"}
        """.trimIndent()
        val manifest = ExportJson.decodeFromString<ExportManifestDto>(futureManifestJson)
        assertEquals(1, manifest.schemaVersion)
    }
}
