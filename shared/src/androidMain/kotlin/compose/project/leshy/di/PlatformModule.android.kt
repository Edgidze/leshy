package compose.project.leshy.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import compose.project.leshy.data.local.DATABASE_NAME
import compose.project.leshy.data.local.LeshyDatabase
import compose.project.leshy.data.platform.AndroidArchiveFileReader
import compose.project.leshy.data.platform.AndroidBackgroundRecordingController
import compose.project.leshy.data.platform.AndroidHttpTextFetcher
import compose.project.leshy.data.platform.AndroidLocationTracker
import compose.project.leshy.data.platform.AndroidMapStyleStorage
import compose.project.leshy.data.platform.AndroidPhotoStorage
import compose.project.leshy.data.platform.AndroidWalkThumbnailRenderer
import compose.project.leshy.data.platform.AndroidPinnedStyleInterceptor
import compose.project.leshy.data.platform.ArchiveFileReader
import compose.project.leshy.data.platform.BackgroundRecordingController
import compose.project.leshy.data.platform.HttpTextFetcher
import compose.project.leshy.data.platform.LocationTracker
import compose.project.leshy.data.platform.MapStyleStorage
import compose.project.leshy.data.platform.PhotoStorage
import compose.project.leshy.data.platform.PinnedStyleInterceptor
import compose.project.leshy.data.platform.WalkThumbnailRenderer
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import org.maplibre.compose.offline.OfflineManager
import org.maplibre.compose.offline.getOfflineManager

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
    single<BackgroundRecordingController> { AndroidBackgroundRecordingController(androidContext()) }
    single<WalkThumbnailRenderer> { AndroidWalkThumbnailRenderer(androidContext(), get()) }
    single<PhotoStorage> { AndroidPhotoStorage(androidContext()) }
    single<MapStyleStorage> { AndroidMapStyleStorage(androidContext()) }
    single<ArchiveFileReader> { AndroidArchiveFileReader(androidContext()) }
    single<HttpTextFetcher> { AndroidHttpTextFetcher() }
    // createdAtStart: must install itself into MapLibre's native HTTP client before anything below
    // (OfflineManager, any MaplibreMap) makes its first network request — see PinnedStyleInterceptor.
    single<PinnedStyleInterceptor>(createdAtStart = true) { AndroidPinnedStyleInterceptor(androidContext()) }
    // getOfflineManager(context) calls MapLibre.getInstance(context) internally on first use
    // (see AndroidOfflineManager) — no separate native-init step needed here.
    single<OfflineManager> { getOfflineManager(androidContext()) }
    single<DataStore<Preferences>> {
        val appContext = androidContext().applicationContext
        PreferenceDataStoreFactory.createWithPath {
            appContext.filesDir.resolve(SETTINGS_FILE_NAME).absolutePath.toPath()
        }
    }
}
