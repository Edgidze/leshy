package compose.project.leshy.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import compose.project.leshy.data.local.dao.CategoryDao
import compose.project.leshy.data.local.dao.ObjectDao
import compose.project.leshy.data.local.dao.TrackPointDao
import compose.project.leshy.data.local.dao.WalkDao
import compose.project.leshy.data.local.entity.CategoryEntity
import compose.project.leshy.data.local.entity.ObjectEntity
import compose.project.leshy.data.local.entity.TrackPointEntity
import compose.project.leshy.data.local.entity.WalkEntity

@Database(
    entities = [
        CategoryEntity::class,
        WalkEntity::class,
        ObjectEntity::class,
        TrackPointEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
@ConstructedBy(LeshyDatabaseConstructor::class)
abstract class LeshyDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun walkDao(): WalkDao
    abstract fun objectDao(): ObjectDao
    abstract fun trackPointDao(): TrackPointDao
}

// Room's KSP compiler generates the platform `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object LeshyDatabaseConstructor : RoomDatabaseConstructor<LeshyDatabase> {
    override fun initialize(): LeshyDatabase
}
