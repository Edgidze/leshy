package klev.fishing.map.data.platform

import android.content.Context
import leshy.mushrooms.map.domain.model.AppLanguage

class AndroidTripRecordingKeepAlive(private val context: Context) : TripRecordingKeepAlive {
    override fun start(language: AppLanguage) = TripRecordingService.start(context, language)

    override fun stop() = TripRecordingService.stop(context)
}
