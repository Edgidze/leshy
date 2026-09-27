package klev.fishing.map.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import klev.fishing.map.data.local.entity.CatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatchDao {
    @Insert
    suspend fun insert(item: CatchEntity): Long

    @Update
    suspend fun update(item: CatchEntity)

    @Query("DELETE FROM catches WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM catches WHERE tripId = :tripId ORDER BY timestamp")
    fun observeByTrip(tripId: Long): Flow<List<CatchEntity>>

    @Query("SELECT * FROM catches ORDER BY timestamp")
    fun observeAll(): Flow<List<CatchEntity>>

    @Query("SELECT * FROM catches WHERE id = :id")
    suspend fun getById(id: Long): CatchEntity?

    /** Приманки, которые пользователь уже вводил, — источник подсказок в поле ввода. Закрытого
     *  справочника приманок в приложении нет сознательно (см. `Catch.bait`). */
    @Query("SELECT DISTINCT bait FROM catches WHERE bait IS NOT NULL AND bait != '' ORDER BY timestamp DESC LIMIT 30")
    fun observeRecentBaits(): Flow<List<String>>
}
