package compose.project.leshy.data.local

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

// Remaps the 4 pre-existing default categories onto their equivalents in the
// expanded 30-species catalog in place, rather than delete+reseed, so finds
// already recorded against those category ids stay intact.
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE categories ADD COLUMN edibilityStatus TEXT NOT NULL DEFAULT 'EDIBLE'")

        connection.execSQL(
            "UPDATE categories SET nameKey='category_boletus_edulis', colorHex='#A95620', " +
                "iconRef='boletus_edulis', edibilityStatus='EDIBLE' WHERE nameKey='category_porcini'",
        )
        connection.execSQL(
            "UPDATE categories SET nameKey='category_cantharellus_cibarius', colorHex='#E69A1F', " +
                "iconRef='cantharellus_cibarius', edibilityStatus='EDIBLE' WHERE nameKey='category_chanterelle'",
        )
        connection.execSQL(
            "UPDATE categories SET nameKey='category_lactarius_deliciosus', colorHex='#C96517', " +
                "iconRef='lactarius_deliciosus', edibilityStatus='EDIBLE' WHERE nameKey='category_ryzhik'",
        )
        connection.execSQL(
            "UPDATE categories SET nameKey='category_leccinum_scabrum', colorHex='#6D5A44', " +
                "iconRef='leccinum_scabrum', edibilityStatus='EDIBLE' WHERE nameKey='category_boletus'",
        )
    }
}

// Additive-only: existing rows get thumbnailPath = null and the archive card falls back to the
// plain Canvas polyline (WalkRouteThumbnail) until a render is generated for them.
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE walks ADD COLUMN thumbnailPath TEXT")
    }
}

// Additive-only: backs the "add place" feature (POI-typed objects rows) — existing rows get
// name/description = null, which is also their steady-state value for MUSHROOM/PHOTO rows.
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE objects ADD COLUMN name TEXT")
        connection.execSQL("ALTER TABLE objects ADD COLUMN description TEXT")
    }
}

// Backs per-country mushroom collections (.claude/plans/mushroom-collections.md, Phase 0).
// `isPicked`/`isFilterEligible` default to 1 so existing installs keep seeing every already-seeded
// category on the Filter screen exactly as before, until the user touches the collection picker.
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE categories ADD COLUMN isPicked INTEGER NOT NULL DEFAULT 1")
        connection.execSQL("ALTER TABLE categories ADD COLUMN isFilterEligible INTEGER NOT NULL DEFAULT 1")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `collections` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`nameKey` TEXT NOT NULL, `order` INTEGER NOT NULL)",
        )
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `category_collections` (`categoryId` INTEGER NOT NULL, " +
                "`collectionId` INTEGER NOT NULL, PRIMARY KEY(`categoryId`, `collectionId`), " +
                "FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                "FOREIGN KEY(`collectionId`) REFERENCES `collections`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)",
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_category_collections_collectionId` " +
                "ON `category_collections` (`collectionId`)",
        )
    }
}

// Backs user-created / imported mushroom species (.claude/plans/user-mushrooms.md, Phase 0).
// Additive-only: `source` defaults to 'APP', so every already-seeded row keeps reading as part of
// the bundled catalog, and the three columns that only apply to non-catalog species stay
// empty/null there (a catalog species takes its name from a StringKey and its illustration from
// composeResources, not from these).
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE categories ADD COLUMN source TEXT NOT NULL DEFAULT 'APP'")
        connection.execSQL("ALTER TABLE categories ADD COLUMN customNames TEXT NOT NULL DEFAULT '{}'")
        connection.execSQL("ALTER TABLE categories ADD COLUMN scientificName TEXT")
        connection.execSQL("ALTER TABLE categories ADD COLUMN iconFile TEXT")
    }
}

// Collapses the edible/conditionally-edible/inedible spectrum onto a single poisonous-warning
// flag: those categories are relative (depends on preparation, region, individual tolerance) and
// the app no longer wants to assert them. `EdibilityStatus` itself shrinks to
// NOT_SPECIFIED/POISONOUS, so every existing row's stored enum name ('EDIBLE'/
// 'CONDITIONALLY_EDIBLE'/'INEDIBLE') stops being a valid constant and must be rewritten here, not
// just left for `EnsureDefaultCategoriesUseCase` to fix up later — that use case only reconciles
// the 30 bundled catalog rows (by nameKey), and a stale value would throw on
// `EdibilityStatus.valueOf` the moment any row is read, before that reconciliation ever runs.
// Blanket-resetting every row to NOT_SPECIFIED first (rather than guessing per old value) is the
// safe default: catalog rows get their real classification back on the very next
// `EnsureDefaultCategoriesUseCase` sync (it diffs and upserts unconditionally), while user-created/
// imported species — which that use case never touches — land on the conservative "no claim made"
// state instead of a guessed one (in particular, never auto-promoted to POISONOUS, which would be
// asserting a safety claim about someone's own species with no basis).
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("UPDATE categories SET edibilityStatus = 'NOT_SPECIFIED'")
    }
}
