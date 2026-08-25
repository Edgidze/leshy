package leshy.mushrooms.map.domain.repository

import leshy.mushrooms.map.domain.model.TrackPoint
import kotlinx.coroutines.flow.Flow

interface TrackPointRepository {
    fun observeAll(): Flow<List<TrackPoint>>
    fun observeByWalkId(walkId: Long): Flow<List<TrackPoint>>
    suspend fun addPoint(point: TrackPoint): Long
}
