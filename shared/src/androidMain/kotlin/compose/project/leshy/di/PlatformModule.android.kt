package compose.project.leshy.di

import androidx.room.Room
import androidx.room.RoomDatabase
import compose.project.leshy.data.local.DATABASE_NAME
import compose.project.leshy.data.local.LeshyDatabase
import compose.project.leshy.data.platform.AndroidLocationTracker
import compose.project.leshy.data.platform.LocationTracker
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<RoomDatabase.Builder<LeshyDatabase>> {
        val appContext = androidContext().applicationContext
        val dbFile = appContext.getDatabasePath(DATABASE_NAME)
        Room.databaseBuilder<LeshyDatabase>(
            context = appContext,
            name = dbFile.absolutePath,
        )
    }
    single<LocationTracker> { AndroidLocationTracker(androidContext()) }
}
