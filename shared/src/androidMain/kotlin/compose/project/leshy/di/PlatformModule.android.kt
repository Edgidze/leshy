package compose.project.leshy.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import compose.project.leshy.data.local.DATABASE_NAME
import compose.project.leshy.data.local.LeshyDatabase
import compose.project.leshy.data.platform.AndroidLocationTracker
import compose.project.leshy.data.platform.LocationTracker
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private const val SETTINGS_FILE_NAME = "leshy_settings.preferences_pb"

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
    single<DataStore<Preferences>> {
        val appContext = androidContext().applicationContext
        PreferenceDataStoreFactory.createWithPath {
            appContext.filesDir.resolve(SETTINGS_FILE_NAME).absolutePath.toPath()
        }
    }
}
