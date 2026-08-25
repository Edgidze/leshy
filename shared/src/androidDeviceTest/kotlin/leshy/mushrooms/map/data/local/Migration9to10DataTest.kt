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
 * MIGRATION_9_10 drops `categories.edibilityStatus` — [LeshyDatabaseMigrationTest] only checks the
 * resulting schema matches (which it would even if `ALTER TABLE ... DROP COLUMN` had also silently
 * dropped rows). This inserts a real row at v9 and confirms every *other* column survives the drop.
 */
@RunWith(AndroidJUnit4::class)
class Migration9to10DataTest {

    @get:Rule
    val helper: MigrationTestHelper =
        MigrationTestHelper(
            instrumentation = InstrumentationRegistry.getInstrumentation(),
            file = InstrumentationRegistry.getInstrumentation().targetContext.getDatabasePath(TEST_DB),
            driver = BundledSQLiteDriver(),
            databaseClass = LeshyDatabase::class,
        )

    @Test
    fun categoriesRowSurvivesColumnDrop() {
        helper.createDatabase(9).apply {
            execSQL(
                "INSERT INTO categories (id, nameKey, colorHex, iconRef, `order`, isActive, " +
                    "edibilityStatus, isPicked, isFilterEligible, source, customNames, scientificName, iconFile) " +
                    "VALUES (1, 'category_test', '#654321', 'test_icon', 3, 1, 'POISONOUS', 0, 1, " +
                    "'APP', '{}', 'Testus scientificus', NULL)",
            )
            close()
        }

        val migrated = helper.runMigrationsAndValidate(version = 10, migrations = listOf(MIGRATION_9_10))
        migrated
            .prepare(
                "SELECT id, nameKey, colorHex, iconRef, `order`, isActive, isPicked, isFilterEligible, " +
                    "source, customNames, scientificName, iconFile FROM categories WHERE id = 1",
            )
            .use { statement ->
                assertTrue("row missing after column drop", statement.step())
                assertEquals(1L, statement.getLong(0))
                assertEquals("category_test", statement.getText(1))
                assertEquals("#654321", statement.getText(2))
                assertEquals("test_icon", statement.getText(3))
                assertEquals(3L, statement.getLong(4))
                assertEquals(1L, statement.getLong(5))
                assertEquals(0L, statement.getLong(6))
                assertEquals(1L, statement.getLong(7))
                assertEquals("APP", statement.getText(8))
                assertEquals("{}", statement.getText(9))
                assertEquals("Testus scientificus", statement.getText(10))
                assertTrue("iconFile should still be NULL", statement.isNull(11))
                assertFalse("unexpected extra row after migration", statement.step())
            }
        migrated.close()
    }

    private companion object {
        const val TEST_DB = "leshy-migration-9-10-data-test.db"
    }
}
