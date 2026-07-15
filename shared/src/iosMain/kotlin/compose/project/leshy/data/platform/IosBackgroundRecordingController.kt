package compose.project.leshy.data.platform

import compose.project.leshy.domain.model.AppLanguage

/**
 * No-op: unlike Android, iOS doesn't need a separate foreground-service-equivalent component.
 * [IosLocationTracker] sets `allowsBackgroundLocationUpdates`/`pausesLocationUpdatesAutomatically`
 * on its `CLLocationManager` directly, which — combined with the `location` UIBackgroundModes
 * entry in Info.plist — is enough for CoreLocation to keep delivering updates to the already
 * running delegate while the app is backgrounded.
 */
class IosBackgroundRecordingController : BackgroundRecordingController {
    override fun start(language: AppLanguage) = Unit
    override fun stop() = Unit
}
