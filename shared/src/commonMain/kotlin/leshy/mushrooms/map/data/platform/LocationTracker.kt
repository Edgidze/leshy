package leshy.mushrooms.map.data.platform

import leshy.mushrooms.map.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    fun track(): Flow<GeoPoint>

    /**
     * Whether the platform can deliver fixes at all right now — the app holds a location
     * permission and the system's location services are on. `false` is the difference between
     * "no fix yet" (normal, resolves itself in seconds) and "no fix ever, until the user changes
     * something in system settings", which is what the Record screen warns about.
     *
     * A snapshot, not a flow: nothing on either platform notifies about a permission change
     * without also restarting the process or resuming the screen, and both of those already
     * re-read this.
     */
    fun isAvailable(): Boolean
}
