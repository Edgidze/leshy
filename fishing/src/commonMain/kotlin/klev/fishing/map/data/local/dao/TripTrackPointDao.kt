package klev.fishing.map.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import klev.fishing.map.data.local.entity.TripTrackPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripTrackPointDao {
    @Insert
    suspend fun insert(point: TripTrackPointEntity): Long

    @Query("SELECT * FROM track_points WHERE tripId = :tripId ORDER BY timestamp")
    fun observeByTrip(tripId: Long): Flow<List<TripTrackPointEntity>>

    @Query("SELECT * FROM track_points WHERE tripId = :tripId ORDER BY timestamp")
    suspend fun getByTrip(tripId: Long): List<TripTrackPointEntity>

    @Query("SELECT * FROM track_points WHERE tripId = :tripId ORDER BY timestamp DESC LIMIT 1")
    suspend fun lastOf(tripId: Long): TripTrackPointEntity?
}
