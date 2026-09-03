package leshy.mushrooms.map.data.platform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import leshy.mushrooms.map.domain.model.GeoPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private const val MIN_INTERVAL_MILLIS = 3000L
private val MIN_DISTANCE_METERS = LOCATION_MIN_DISTANCE_METERS.toFloat()

class AndroidLocationTracker(private val context: Context) : LocationTracker {

    override fun isAvailable(): Boolean {
        if (!hasLocationPermission(context)) return false
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return runCatching {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }.getOrDefault(false)
    }

    override fun track(): Flow<GeoPoint> = callbackFlow {
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasFinePermission) {
            close()
            return@callbackFlow
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Picked from allProviders, not from isProviderEnabled: requestLocationUpdates accepts a
        // currently-disabled provider and simply starts delivering once it is switched on, which
        // is what makes "the user turned location services on mid-walk" recover on its own.
        // Closing the flow instead (the previous behaviour) meant no fix ever arrived again for
        // the rest of that walk, since nothing re-subscribes while recording is in progress.
        val providers = locationManager.allProviders
        val provider = when {
            LocationManager.GPS_PROVIDER in providers -> LocationManager.GPS_PROVIDER
            LocationManager.NETWORK_PROVIDER in providers -> LocationManager.NETWORK_PROVIDER
            else -> null
        }
        if (provider == null) {
            close()
            return@callbackFlow
        }

        // requestLocationUpdates only calls the listener on the *next* fix — without this, the
        // map shows the default (0,0) point until a fresh update arrives (e.g. before a walk is
        // even started), even though the OS already has a recent fix cached.
        runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
            ?.let { trySend(it.toGeoPoint()) }

        val listener = LocationListener { location -> trySend(location.toGeoPoint()) }
        val registered = runCatching {
            locationManager.requestLocationUpdates(
                provider,
                MIN_INTERVAL_MILLIS,
                MIN_DISTANCE_METERS,
                listener,
                Looper.getMainLooper(),
            )
        }.isSuccess
        if (!registered) {
            close()
            return@callbackFlow
        }
        awaitClose { runCatching { locationManager.removeUpdates(listener) } }
    }
}

private fun Location.toGeoPoint() = GeoPoint(
    lat = latitude,
    lon = longitude,
    elevation = if (hasAltitude()) altitude else null,
    timestamp = time,
)
