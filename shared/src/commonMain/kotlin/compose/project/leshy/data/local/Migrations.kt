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
