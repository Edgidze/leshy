package leshy.mushrooms.map.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Chains all 11 recorded migrations (v1 -> v12) in one [MigrationTestHelper.runMigrationsAndValidate]
 * call and lets Room's own migration path resolution walk them in order — this only validates the
 * resulting schema against `schemas/leshy.mushrooms.map.data.local.LeshyDatabase/12.json`, it does
 * not check that data survives any individual step (see the per-migration data tests next to this
 * one for that).
 */
@RunWith(AndroidJUnit4::class)
class LeshyDatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper =
        MigrationTestHelper(
            instrumentation = InstrumentationRegistry.getInstrumentation(),
            file = InstrumentationRegistry.getInstrumentation().targetContext.getDatabasePath(TEST_DB),
            driver = BundledSQLiteDriver(),
            databaseClass = LeshyDatabase::class,
        )

    @Test
    fun migrateFromV1ToLatest() {
        helper.createDatabase(1).close()

        helper
            .runMigrationsAndValidate(
                version = 12,
                migrations =
                    listOf(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10,
                        MIGRATION_10_11,
                        MIGRATION_11_12,
                    ),
            )
            .close()
    }

    private companion object {
        const val TEST_DB = "leshy-migration-test.db"
    }
}
