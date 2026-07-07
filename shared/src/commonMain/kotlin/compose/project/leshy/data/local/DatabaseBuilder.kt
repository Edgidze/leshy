package compose.project.leshy.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

const val DATABASE_NAME = "leshy.db"

fun getRoomDatabase(builder: RoomDatabase.Builder<LeshyDatabase>): LeshyDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
