package klev.fishing.map.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import klev.fishing.map.data.local.dao.CatchDao
import klev.fishing.map.data.local.dao.SpeciesDao
import klev.fishing.map.data.local.dao.TripDao
import klev.fishing.map.data.local.dao.TripTrackPointDao
import klev.fishing.map.data.local.entity.CatchEntity
import klev.fishing.map.data.local.entity.SpeciesEntity
import klev.fishing.map.data.local.entity.TripEntity
import klev.fishing.map.data.local.entity.TripTrackPointEntity

const val FISHING_DATABASE_NAME = "fishing.db"

/**
 * База рыбацкого продукта — СВОЯ, с версии 1, и грибную историю миграций она не наследует. Причина
 * не в чистоте: подмешай её сюда, и первая же рыбацкая миграция стала бы обязательной для грибного
 * приложения, где ей делать нечего. Разбор — `.claude/plans/product-family.md`, раздел 6.
 *
 * `exportSchema = true` и `schemas/` в модуле — как у грибов: `fallbackToDestructiveMigration`
 * в проекте запрещён, и единственный способ это соблюсти — иметь схему каждой версии под рукой.
 */
@Database(
    entities = [
        TripEntity::class,
        TripTrackPointEntity::class,
        SpeciesEntity::class,
        CatchEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
@ConstructedBy(FishingDatabaseConstructor::class)
abstract class FishingDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun trackPointDao(): TripTrackPointDao
    abstract fun speciesDao(): SpeciesDao
    abstract fun catchDao(): CatchDao
}

// Платформенные `actual` генерирует KSP-компилятор Room.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object FishingDatabaseConstructor : RoomDatabaseConstructor<FishingDatabase> {
    override fun initialize(): FishingDatabase
}
