package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.TrackPoint
import kotlinx.coroutines.flow.Flow

interface TrackPointRepository {
    fun observeByWalkId(walkId: Long): Flow<List<TrackPoint>>
    suspend fun addPoint(point: TrackPoint): Long
}
