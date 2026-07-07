package compose.project.leshy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import compose.project.leshy.data.local.entity.TrackPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPointDao {
    @Query("SELECT * FROM track_points WHERE walkId = :walkId ORDER BY sequence ASC")
    fun observeByWalkId(walkId: Long): Flow<List<TrackPointEntity>>

    @Insert
    suspend fun insert(trackPoint: TrackPointEntity): Long
}
