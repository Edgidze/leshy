package compose.project.leshy.di

import androidx.room.Room
import androidx.room.RoomDatabase
import compose.project.leshy.data.local.DATABASE_NAME
import compose.project.leshy.data.local.LeshyDatabase
import compose.project.leshy.data.platform.IosLocationTracker
import compose.project.leshy.data.platform.LocationTracker
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val platformModule: Module = module {
    single<RoomDatabase.Builder<LeshyDatabase>> {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val dbFilePath = requireNotNull(documentDirectory?.path) + "/" + DATABASE_NAME
        Room.databaseBuilder<LeshyDatabase>(name = dbFilePath)
    }
    single<LocationTracker> { IosLocationTracker() }
}
