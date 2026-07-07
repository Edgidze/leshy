package compose.project.leshy.data.platform

import compose.project.leshy.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    fun track(): Flow<GeoPoint>
}
