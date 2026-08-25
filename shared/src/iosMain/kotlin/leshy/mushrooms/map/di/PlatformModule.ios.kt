package leshy.mushrooms.map.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import leshy.mushrooms.map.data.local.DATABASE_NAME
import leshy.mushrooms.map.data.local.LeshyDatabase
import leshy.mushrooms.map.data.platform.ArchiveFileReader
import leshy.mushrooms.map.data.platform.BackgroundRecordingController
import leshy.mushrooms.map.data.platform.HttpTextFetcher
import leshy.mushrooms.map.data.platform.IosArchiveFileReader
import leshy.mushrooms.map.data.platform.IosBackgroundRecordingController
import leshy.mushrooms.map.data.platform.IosHttpTextFetcher
import leshy.mushrooms.map.data.platform.IosLocationTracker
import leshy.mushrooms.map.data.platform.IosMapStyleStorage
import leshy.mushrooms.map.data.platform.IosPhotoStorage
import leshy.mushrooms.map.data.platform.IosPinnedStyleInterceptor
import leshy.mushrooms.map.data.platform.IosWalkThumbnailRenderer
import leshy.mushrooms.map.data.platform.LocationTracker
import leshy.mushrooms.map.data.platform.MapStyleStorage
import leshy.mushrooms.map.data.platform.PhotoStorage
import leshy.mushrooms.map.data.platform.PinnedStyleInterceptor
import leshy.mushrooms.map.data.platform.WalkThumbnailRenderer
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import org.maplibre.compose.offline.OfflineManager
import org.maplibre.compose.offline.getOfflineManager
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
    single { IosLocationTracker() }
    single<LocationTracker> { get<IosLocationTracker>() }
    single<BackgroundRecordingController> { IosBackgroundRecordingController(get()) }
    single<WalkThumbnailRenderer> { IosWalkThumbnailRenderer(get()) }
    single<PhotoStorage> { IosPhotoStorage() }
    single<MapStyleStorage> { IosMapStyleStorage() }
    single<ArchiveFileReader> { IosArchiveFileReader() }
    single<HttpTextFetcher> { IosHttpTextFetcher() }
    // createdAtStart: must install itself into MapLibre's native HTTP client before anything below
    // (OfflineManager, any MLNMapView) makes its first network request — see PinnedStyleInterceptor.
    single<PinnedStyleInterceptor>(createdAtStart = true) { IosPinnedStyleInterceptor() }
    single<OfflineManager> { getOfflineManager() }
    single<DataStore<Preferences>> {
        val settingsFilePath = documentsDirectoryPath() + "/" + SETTINGS_FILE_NAME
        PreferenceDataStoreFactory.createWithPath { settingsFilePath.toPath() }
    }
}
