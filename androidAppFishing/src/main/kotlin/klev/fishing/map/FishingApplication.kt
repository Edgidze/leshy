package klev.fishing.map

import android.app.Application
import klev.fishing.map.di.initFishingKoin
import org.koin.android.ext.koin.androidContext

class FishingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initFishingKoin {
            androidContext(this@FishingApplication)
        }
    }
}
