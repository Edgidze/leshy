package klev.fishing.map.data.local

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * Глубина в месте поимки (`catches.depthCm`).
 *
 * Только добавление колонки: у всех записей, сделанных до этой версии, глубина `null` — и это не
 * потеря, а правда. Их писали, когда поля не было, и подставлять им ноль значило бы утверждать, что
 * рыбу взяли с поверхности.
 *
 * `fallbackToDestructiveMigration` в проекте запрещён (корневой `CLAUDE.md`), поэтому каждая версия
 * базы приезжает со своим `Migration`-объектом и экспортированной схемой в `fishing/schemas/`.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE catches ADD COLUMN depthCm INTEGER")
    }
}
