package leshy.mushrooms.map.domain.repository

import leshy.mushrooms.map.domain.model.TrackPoint
import kotlinx.coroutines.flow.Flow

interface TrackPointRepository {
    fun observeAll(): Flow<List<TrackPoint>>
    fun observeByWalkId(walkId: Long): Flow<List<TrackPoint>>

    /**
     * Точки указанных прогулок одним чтением, без подписки. Почему именно без подписки — см.
     * `TrackPointDao.getByWalkIds`. Порядок: по прогулкам, внутри прогулки — по `sequence`.
     */
    suspend fun getPoints(walkIds: Collection<Long>): List<TrackPoint>

    suspend fun addPoint(point: TrackPoint): Long
}
