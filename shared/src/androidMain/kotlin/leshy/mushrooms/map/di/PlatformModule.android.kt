package leshy.mushrooms.map.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import leshy.mushrooms.map.data.local.DATABASE_NAME
import leshy.mushrooms.map.data.local.LeshyDatabase
import leshy.mushrooms.map.data.platform.AndroidArchiveFileReader
import leshy.mushrooms.map.data.platform.AndroidBackgroundRecordingController
import leshy.mushrooms.map.data.platform.AndroidHttpTextFetcher
import leshy.mushrooms.map.data.platform.AndroidLocationTracker
import leshy.mushrooms.map.data.platform.AndroidMapStyleStorage
import leshy.mushrooms.map.data.platform.AndroidPhotoStorage
import leshy.mushrooms.map.data.platform.AndroidWalkThumbnailRenderer
import leshy.mushrooms.map.data.platform.AndroidPinnedStyleInterceptor
import leshy.mushrooms.map.data.platform.ArchiveFileReader
import leshy.mushrooms.map.data.platform.BackgroundRecordingController
import leshy.mushrooms.map.data.platform.HttpTextFetcher
import leshy.mushrooms.map.data.platform.LocationTracker
import leshy.mushrooms.map.data.platform.MapStyleStorage
import leshy.mushrooms.map.data.platform.PhotoStorage
import leshy.mushrooms.map.data.platform.PinnedStyleInterceptor
import leshy.mushrooms.map.data.platform.WalkThumbnailRenderer
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
