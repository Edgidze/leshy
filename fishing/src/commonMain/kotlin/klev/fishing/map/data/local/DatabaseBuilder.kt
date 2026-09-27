package klev.fishing.map.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

fun buildFishingDatabase(builder: RoomDatabase.Builder<FishingDatabase>): FishingDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
