package leshy.mushrooms.map.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import leshy.mushrooms.map.data.local.dao.CategoryDao
import leshy.mushrooms.map.data.local.dao.CollectionDao
import leshy.mushrooms.map.data.local.dao.ObjectDao
import leshy.mushrooms.map.data.local.dao.TrackPointDao
import leshy.mushrooms.map.data.local.dao.WalkDao
import leshy.mushrooms.map.data.local.entity.CategoryCollectionCrossRef
import leshy.mushrooms.map.data.local.entity.CategoryEntity
import leshy.mushrooms.map.data.local.entity.CollectionEntity
import leshy.mushrooms.map.data.local.entity.ObjectEntity
import leshy.mushrooms.map.data.local.entity.TrackPointEntity
import leshy.mushrooms.map.data.local.entity.WalkEntity

/**
 * Version 12 is the first published version — databases at versions 1-11 exist only on
 * development machines, never outside of them.
 */
@Database(
    entities = [
        CategoryEntity::class,
        WalkEntity::class,
        ObjectEntity::class,
        TrackPointEntity::class,
        CollectionEntity::class,
        CategoryCollectionCrossRef::class,
    ],
    version = 12,
    exportSchema = true,
)
@TypeConverters(Converters::class)
@ConstructedBy(LeshyDatabaseConstructor::class)
abstract class LeshyDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun walkDao(): WalkDao
    abstract fun objectDao(): ObjectDao
    abstract fun trackPointDao(): TrackPointDao
    abstract fun collectionDao(): CollectionDao
}

// Room's KSP compiler generates the platform `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object LeshyDatabaseConstructor : RoomDatabaseConstructor<LeshyDatabase> {
    override fun initialize(): LeshyDatabase
}
