package compose.project.leshy.data.local

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import compose.project.leshy.data.catalog.LEGACY_CATEGORY_KEY_REMAP

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

// Adds ON DELETE CASCADE to objects.categoryId -> categories.id, backing real species deletion
// (.claude/plans/user-mushrooms.md, "Только скрытие..." section, superseded by Phase 9). Until now
// that FK had no onDelete (NO ACTION), which is exactly why deletion was ruled out originally: with
// PRAGMA foreign_keys ON (Room), deleting a category with existing finds threw a constraint
// violation instead of taking them with it.
//
// SQLite cannot ALTER a FOREIGN KEY on an existing table, so this is the standard rebuild: create a
// new table with the desired FK, copy the data, drop the old table, rename. No other table
// references `objects` as a parent, so there's nothing else to re-point mid-migration. Column list,
// types and index names below are copied verbatim from the exported v7 schema
// (shared/schemas/.../7.json) other than the one changed FK clause.
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `objects_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`walkId` INTEGER NOT NULL, `categoryId` INTEGER NOT NULL, `lat` REAL NOT NULL, " +
                "`lon` REAL NOT NULL, `timestamp` INTEGER NOT NULL, `type` TEXT NOT NULL, " +
                "`photoPath` TEXT, `name` TEXT, `description` TEXT, " +
                "FOREIGN KEY(`walkId`) REFERENCES `walks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
        )
        connection.execSQL(
            "INSERT INTO `objects_new` (`id`, `walkId`, `categoryId`, `lat`, `lon`, `timestamp`, " +
                "`type`, `photoPath`, `name`, `description`) " +
                "SELECT `id`, `walkId`, `categoryId`, `lat`, `lon`, `timestamp`, `type`, `photoPath`, " +
                "`name`, `description` FROM `objects`",
        )
        connection.execSQL("DROP TABLE `objects`")
        connection.execSQL("ALTER TABLE `objects_new` RENAME TO `objects`")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_objects_walkId` ON `objects` (`walkId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_objects_categoryId` ON `objects` (`categoryId`)")
    }
}

// Additive-only: backs the free-text walk description field (WalkDetailScreen, edited on its own
// screen rather than a dialog — see WalkDescriptionEditScreen doc comment for why). Existing rows
// get description = null, rendered as an empty/placeholder description.
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE walks ADD COLUMN description TEXT")
    }
}

// Drops `categories.edibilityStatus` — the app no longer classifies species as poisonous/not (out
// of scope for the app per product decision), so the column, its sort-order tie-in, and every
// derived UI (species form field, tile badge, "poisonous last" sort option) are gone together.
// `ALTER TABLE ... DROP COLUMN` needs no table rebuild here (no FK/index touches this column,
// unlike the v7->v8 migration) — supported by the bundled SQLite (androidx.sqlite:sqlite-bundled,
// >= 3.35).
val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE categories DROP COLUMN edibilityStatus")
    }
}

// Re-keys the 30 bundled catalog rows onto the 408-entry catalog's keys (`category_boletus_edulis`
// -> `boletus_edulis`), so `EnsureDefaultCategoriesUseCase` can reconcile them against catalog.json
// instead of the hard-coded list it used to carry (.claude/plans/countries-and-languages.md, Phase
// 2). No schema change at all — v11 is structurally identical to v10, the version bump exists only
// to give this data rewrite a migration to live in.
//
// UPDATE, never DELETE + reseed: `objects.categoryId` has cascaded from `categories` since v7->v8,
// so any delete here would take the user's recorded finds with it. Renaming in place keeps every
// find bound to the same row id, and leaves isActive/isPicked/isFilterEligible (all user-owned)
// untouched.
//
// Seven of the 30 aren't a plain prefix strip — the old catalog's broad "group" entries, whose
// targets the project owner picked by GC code (see LEGACY_CATEGORY_KEY_REMAP). They run first: once
// renamed they no longer carry the `category_` prefix, so the generic strip below can't touch them
// a second time. Both statements are scoped to `source = 'APP'` (user-created/imported species'
// keys are generated `user_…` and could never legitimately match, but an imported archive is
// outside data this app generated) and skip the two service keys, which aren't catalog entries and
// keep their StringKey-backed names.
val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(connection: SQLiteConnection) {
        LEGACY_CATEGORY_KEY_REMAP.forEach { (legacyKey, catalogKey) ->
            connection.execSQL(
                "UPDATE categories SET nameKey = '$catalogKey' WHERE nameKey = '$legacyKey' AND source = 'APP'",
            )
        }
        connection.execSQL(
            "UPDATE categories SET nameKey = substr(nameKey, 10) " +
                "WHERE source = 'APP' AND nameKey LIKE 'category_%' " +
                "AND nameKey NOT IN ('category_misc', 'category_unknown_mushroom')",
        )
    }
}
