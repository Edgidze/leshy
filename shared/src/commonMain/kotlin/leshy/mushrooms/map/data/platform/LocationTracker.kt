package leshy.mushrooms.map.data.platform

import leshy.mushrooms.map.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    fun track(): Flow<GeoPoint>
}
