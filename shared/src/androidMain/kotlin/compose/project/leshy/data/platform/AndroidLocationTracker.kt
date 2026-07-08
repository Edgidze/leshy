package compose.project.leshy.data.platform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import compose.project.leshy.domain.model.GeoPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private const val MIN_INTERVAL_MILLIS = 3000L
private const val MIN_DISTANCE_METERS = 5f

class AndroidLocationTracker(private val context: Context) : LocationTracker {
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
        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> null
        }
        if (provider == null) {
            close()
            return@callbackFlow
        }

        // requestLocationUpdates only calls the listener on the *next* fix — without this, the
        // map shows the default (0,0) point until a fresh update arrives (e.g. before a walk is
        // even started), even though the OS already has a recent fix cached.
        locationManager.getLastKnownLocation(provider)?.let { trySend(it.toGeoPoint()) }

        val listener = LocationListener { location -> trySend(location.toGeoPoint()) }
        locationManager.requestLocationUpdates(
            provider,
            MIN_INTERVAL_MILLIS,
            MIN_DISTANCE_METERS,
            listener,
            Looper.getMainLooper(),
        )
        awaitClose { locationManager.removeUpdates(listener) }
    }
}

private fun Location.toGeoPoint() = GeoPoint(
    lat = latitude,
    lon = longitude,
    elevation = if (hasAltitude()) altitude else null,
    timestamp = time,
)
