package compose.project.leshy.data.platform

import compose.project.leshy.domain.model.AppLanguage

/**
 * Keeps GPS updates flowing while a walk is being recorded and the app is not in the foreground
 * (screen off, another app on top). Android needs an active foreground service of type `location`
 * for this — without it the OS throttles/stops location callbacks once the app leaves the
 * foreground. iOS achieves the same via the `location` background mode + `CLLocationManager`
 * flags set directly on the tracker, so its implementation is a no-op.
 */
interface BackgroundRecordingController {
    /** [language] is used for the Android persistent-notification text; unused on iOS. */
    fun start(language: AppLanguage)
    fun stop()
}
