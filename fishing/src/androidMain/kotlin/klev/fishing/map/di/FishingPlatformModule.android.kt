package klev.fishing.map.di

import androidx.room.Room
import androidx.room.RoomDatabase
import klev.fishing.map.data.local.FISHING_DATABASE_NAME
import klev.fishing.map.data.local.FishingDatabase
import klev.fishing.map.data.platform.AndroidTripRecordingKeepAlive
import klev.fishing.map.data.platform.TripRecordingKeepAlive
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val fishingPlatformModule: Module = module {
    single<RoomDatabase.Builder<FishingDatabase>> {
        val appContext = androidContext().applicationContext
        Room.databaseBuilder<FishingDatabase>(
            context = appContext,
            name = appContext.getDatabasePath(FISHING_DATABASE_NAME).absolutePath,
        )
    }
    single<TripRecordingKeepAlive> { AndroidTripRecordingKeepAlive(androidContext()) }
}
