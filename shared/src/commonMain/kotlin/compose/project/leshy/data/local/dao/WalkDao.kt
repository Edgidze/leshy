package compose.project.leshy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import compose.project.leshy.data.local.entity.WalkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkDao {
    @Query("SELECT * FROM walks ORDER BY startTime DESC")
    fun observeAll(): Flow<List<WalkEntity>>

    @Query("SELECT * FROM walks WHERE id = :id")
    fun observeById(id: Long): Flow<WalkEntity?>

    @Query("SELECT * FROM walks WHERE id = :id")
    suspend fun getById(id: Long): WalkEntity?

    @Insert
    suspend fun insert(walk: WalkEntity): Long

    @Update
    suspend fun update(walk: WalkEntity)

    @Delete
    suspend fun delete(walk: WalkEntity)
}
