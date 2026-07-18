package compose.project.leshy.data.platform

import compose.project.leshy.domain.model.GeoPoint
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject

class IosLocationTracker : LocationTracker {

    // Held as instance fields, not locals inside the callbackFlow builder: CLLocationManager's
    // `delegate` property is `weak`, and `delegate` here is never referenced again after being
    // assigned (`awaitClose` below only touches `manager`) — so with purely local vals, the
    // Kotlin/Native coroutine state machine has nothing forcing it to keep a strong reference to
    // the delegate object past that point, and ARC is free to deallocate it right after the first
    // callback. Reproduced on a real iPhone SE: only the initial GPS fix was ever delivered, and
    // every mushroom marked for the rest of the walk landed on that same stale point. Keeping both
    // as fields of this singleton guarantees a strong reference for as long as tracking is active.
    private var manager: CLLocationManager? = null
    private var delegate: CLLocationManagerDelegateProtocol? = null
    private var backgroundUpdatesEnabled = false

    @OptIn(ExperimentalForeignApi::class)
    override fun track(): Flow<GeoPoint> = callbackFlow {
        val manager = CLLocationManager()
        manager.desiredAccuracy = kCLLocationAccuracyBest
        applyBackgroundUpdates(manager, backgroundUpdatesEnabled)

        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
                val point = GeoPoint(
                    lat = location.coordinate.useContents { latitude },
                    lon = location.coordinate.useContents { longitude },
                    elevation = location.altitude,
                    timestamp = currentTimeMillis(),
                )
                trySend(point)
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                // Transient failures are ignored; the flow simply pauses until the next fix.
            }
        }

        manager.delegate = delegate
        this@IosLocationTracker.manager = manager
        this@IosLocationTracker.delegate = delegate
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()

        // startUpdatingLocation() usually redelivers a recent fix quickly via the delegate, but
        // emit the already-cached location right away too, so the map doesn't sit on the default
        // (0,0) point in the meantime (e.g. before a walk is even started).
        manager.location?.let { location ->
            trySend(
                GeoPoint(
                    lat = location.coordinate.useContents { latitude },
                    lon = location.coordinate.useContents { longitude },
                    elevation = location.altitude,
                    timestamp = currentTimeMillis(),
                ),
            )
        }

        awaitClose {
            manager.stopUpdatingLocation()
            manager.delegate = null
            this@IosLocationTracker.manager = null
            this@IosLocationTracker.delegate = null
        }
    }

    /**
     * Toggles CoreLocation background delivery — called from [IosBackgroundRecordingController]
     * around an actual walk recording, not left on for this tracker's whole lifetime. Otherwise
     * the tracker only needs "when in use" access (e.g. to show the current position on the
     * Record screen before a walk starts), and the OS pauses updates once the app backgrounds —
     * without this gating, `allowsBackgroundLocationUpdates` stayed on permanently and the app
     * kept using (and showing the system's background-location notification for) GPS even while
     * just idling on a tab with no walk in progress.
     */
    @OptIn(ExperimentalForeignApi::class)
    fun setBackgroundUpdatesEnabled(enabled: Boolean) {
        backgroundUpdatesEnabled = enabled
        manager?.let { applyBackgroundUpdates(it, enabled) }
    }

    // Requires the `location` UIBackgroundModes entry in Info.plist, or CoreLocation throws.
    // Together they let updates keep reaching the delegate while the app is backgrounded (screen
    // off, another app on top) without needing "Always" authorization — the same approach
    // fitness-tracking apps use for a "When In Use" background session.
    @OptIn(ExperimentalForeignApi::class)
    private fun applyBackgroundUpdates(manager: CLLocationManager, enabled: Boolean) {
        manager.allowsBackgroundLocationUpdates = enabled
        manager.pausesLocationUpdatesAutomatically = !enabled
    }
}
