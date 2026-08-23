package compose.project.leshy.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import compose.project.leshy.data.local.DATABASE_NAME
import compose.project.leshy.data.local.LeshyDatabase
import compose.project.leshy.data.platform.ArchiveFileReader
import compose.project.leshy.data.platform.BackgroundRecordingController
import compose.project.leshy.data.platform.HttpTextFetcher
import compose.project.leshy.data.platform.IosArchiveFileReader
import compose.project.leshy.data.platform.IosBackgroundRecordingController
import compose.project.leshy.data.platform.IosHttpTextFetcher
import compose.project.leshy.data.platform.IosLocationTracker
import compose.project.leshy.data.platform.IosMapStyleStorage
import compose.project.leshy.data.platform.IosPhotoStorage
import compose.project.leshy.data.platform.IosPinnedStyleInterceptor
import compose.project.leshy.data.platform.IosWalkThumbnailRenderer
import compose.project.leshy.data.platform.LocationTracker
import compose.project.leshy.data.platform.MapStyleStorage
import compose.project.leshy.data.platform.PhotoStorage
import compose.project.leshy.data.platform.PinnedStyleInterceptor
import compose.project.leshy.data.platform.WalkThumbnailRenderer
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
