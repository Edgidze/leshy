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
    @OptIn(ExperimentalForeignApi::class)
    override fun track(): Flow<GeoPoint> = callbackFlow {
        val manager = CLLocationManager()
        manager.desiredAccuracy = kCLLocationAccuracyBest
        // Requires the `location` UIBackgroundModes entry in Info.plist, or CoreLocation throws.
        // Together they let updates keep reaching the delegate while the app is backgrounded
        // (screen off, another app on top) without needing "Always" authorization — the same
        // approach fitness-tracking apps use for a "When In Use" background session.
        manager.allowsBackgroundLocationUpdates = true
        manager.pausesLocationUpdatesAutomatically = false

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
        }
    }
}
