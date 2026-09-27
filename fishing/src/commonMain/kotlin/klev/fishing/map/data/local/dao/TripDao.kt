package klev.fishing.map.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import klev.fishing.map.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert
    suspend fun insert(trip: TripEntity): Long

    @Update
    suspend fun update(trip: TripEntity)

    @Query("SELECT * FROM trips ORDER BY startTime DESC")
    fun observeAll(): Flow<List<TripEntity>>

    /** Незакрытая рыбалка. Их не может быть больше одной, но `LIMIT 1` стоит защитно: процесс
     *  может быть убит в любой момент, и восстановление обязано работать, а не спорить с данными. */
    @Query("SELECT * FROM trips WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    fun observeActive(): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    suspend fun findActive(): TripEntity?

    @Query("SELECT * FROM trips WHERE id = :id")
    fun observeById(id: Long): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getById(id: Long): TripEntity?

    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun deleteById(id: Long)
}
