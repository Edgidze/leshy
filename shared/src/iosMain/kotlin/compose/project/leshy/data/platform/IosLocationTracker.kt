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

        awaitClose {
            manager.stopUpdatingLocation()
            manager.delegate = null
        }
    }
}
