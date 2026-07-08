package compose.project.leshy.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import compose.project.leshy.data.local.DATABASE_NAME
import compose.project.leshy.data.local.LeshyDatabase
import compose.project.leshy.data.platform.IosLocationTracker
import compose.project.leshy.data.platform.LocationTracker
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

private const val SETTINGS_FILE_NAME = "leshy_settings.preferences_pb"

@OptIn(ExperimentalForeignApi::class)
private fun documentsDirectoryPath(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

@OptIn(ExperimentalForeignApi::class)
actual val platformModule: Module = module {
    single<RoomDatabase.Builder<LeshyDatabase>> {
        val dbFilePath = documentsDirectoryPath() + "/" + DATABASE_NAME
        Room.databaseBuilder<LeshyDatabase>(name = dbFilePath)
    }
    single<LocationTracker> { IosLocationTracker() }
    single<DataStore<Preferences>> {
        val settingsFilePath = documentsDirectoryPath() + "/" + SETTINGS_FILE_NAME
        PreferenceDataStoreFactory.createWithPath { settingsFilePath.toPath() }
    }
}
