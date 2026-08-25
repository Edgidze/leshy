package leshy.mushrooms.map

import android.app.Application
import leshy.mushrooms.map.di.initKoin
import org.koin.android.ext.koin.androidContext

class LeshyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@LeshyApplication)
        }
    }
}
