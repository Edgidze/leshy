package leshy.mushrooms.map.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * MIGRATION_7_8 rebuilds `objects` via create-copy-drop-rename (to add `ON DELETE CASCADE` on
 * `categoryId`) — [LeshyDatabaseMigrationTest] only checks the resulting schema matches, which
 * would stay green even if the `INSERT ... SELECT` copy step dropped a column or a row. This
 * inserts real rows at v7 and reads them back after migrating to v8.
 */
@RunWith(AndroidJUnit4::class)
class Migration7to8DataTest {

    @get:Rule
    val helper: MigrationTestHelper =
        MigrationTestHelper(
            instrumentation = InstrumentationRegistry.getInstrumentation(),
            file = InstrumentationRegistry.getInstrumentation().targetContext.getDatabasePath(TEST_DB),
            driver = BundledSQLiteDriver(),
            databaseClass = LeshyDatabase::class,
        )

    @Test
    fun objectsRowsSurviveTableRebuild() {
        helper.createDatabase(7).apply {
            execSQL(
                "INSERT INTO categories (id, nameKey, colorHex, iconRef, `order`, isActive, " +
                    "edibilityStatus, isPicked, isFilterEligible, source, customNames, scientificName, iconFile) " +
                    "VALUES (1, 'category_test', '#123456', NULL, 0, 1, 'EDIBLE', 1, 1, 'APP', '{}', NULL, NULL)",
            )
            execSQL(
                "INSERT INTO walks (id, name, startTime, endTime, distanceMeters, avgSpeed, " +
                    "startLat, startLon, endLat, endLon, mushroomCount, thumbnailPath) " +
                    "VALUES (1, 'Test walk', 1000, 2000, 500.5, 1.5, 55.5, 37.5, 55.75, 37.75, 2, NULL)",
            )
            // One MUSHROOM row with a photo and no name/description, one POI row with the opposite
            // nullability, so both directions of every nullable column get a round trip.
            execSQL(
                "INSERT INTO objects (id, walkId, categoryId, lat, lon, timestamp, type, " +
                    "photoPath, name, description) VALUES " +
                    "(1, 1, 1, 55.25, 37.25, 1500, 'MUSHROOM', '/photo/1.jpg', NULL, NULL)",
            )
            execSQL(
                "INSERT INTO objects (id, walkId, categoryId, lat, lon, timestamp, type, " +
                    "photoPath, name, description) VALUES " +
                    "(2, 1, 1, 55.125, 37.125, 1600, 'POI', NULL, 'Landmark', 'A tall pine')",
            )
            close()
        }

        val migrated = helper.runMigrationsAndValidate(version = 8, migrations = listOf(MIGRATION_7_8))
        migrated
            .prepare(
                "SELECT id, walkId, categoryId, lat, lon, timestamp, type, photoPath, name, description " +
                    "FROM objects ORDER BY id",
            )
            .use { statement ->
                assertTrue("row 1 (MUSHROOM) missing after migration", statement.step())
                assertEquals(1L, statement.getLong(0))
                assertEquals(1L, statement.getLong(1))
                assertEquals(1L, statement.getLong(2))
                assertEquals(55.25, statement.getDouble(3), 0.0)
                assertEquals(37.25, statement.getDouble(4), 0.0)
                assertEquals(1500L, statement.getLong(5))
                assertEquals("MUSHROOM", statement.getText(6))
                assertEquals("/photo/1.jpg", statement.getText(7))
                assertTrue("name should still be NULL", statement.isNull(8))
                assertTrue("description should still be NULL", statement.isNull(9))

                assertTrue("row 2 (POI) missing after migration", statement.step())
                assertEquals(2L, statement.getLong(0))
                assertEquals("POI", statement.getText(6))
                assertTrue("photoPath should still be NULL", statement.isNull(7))
                assertEquals("Landmark", statement.getText(8))
                assertEquals("A tall pine", statement.getText(9))

                assertFalse("unexpected extra row after migration", statement.step())
            }
        migrated.close()
    }

    private companion object {
        const val TEST_DB = "leshy-migration-7-8-data-test.db"
    }
}
