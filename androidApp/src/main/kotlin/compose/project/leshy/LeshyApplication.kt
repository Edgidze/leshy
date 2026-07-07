package compose.project.leshy

import android.app.Application
import compose.project.leshy.di.initKoin
import org.koin.android.ext.koin.androidContext

class LeshyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@LeshyApplication)
        }
    }
}
