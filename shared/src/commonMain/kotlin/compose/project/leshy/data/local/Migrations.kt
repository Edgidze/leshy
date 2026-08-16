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
