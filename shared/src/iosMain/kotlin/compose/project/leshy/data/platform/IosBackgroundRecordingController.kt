package compose.project.leshy.data.platform

import compose.project.leshy.domain.model.AppLanguage

/**
 * Unlike Android, iOS doesn't need a separate foreground-service-equivalent component — it just
 * flips [IosLocationTracker]'s background delivery flags on/off around an active walk. Doing this
 * here (start/stop), rather than leaving background updates on for the tracker's whole lifetime,
 * is what keeps the app from using (and showing the system's background-location notification for)
 * GPS while it's simply sitting on a tab with no walk being recorded.
 */
class IosBackgroundRecordingController(
    private val locationTracker: IosLocationTracker,
) : BackgroundRecordingController {
    override fun start(language: AppLanguage) {
        locationTracker.setBackgroundUpdatesEnabled(true)
    }

    override fun stop() {
        locationTracker.setBackgroundUpdatesEnabled(false)
    }
}
