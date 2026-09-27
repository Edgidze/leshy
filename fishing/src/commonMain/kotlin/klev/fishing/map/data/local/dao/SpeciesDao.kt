package klev.fishing.map.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import klev.fishing.map.data.local.entity.SpeciesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeciesDao {
    @Insert
    suspend fun insert(species: SpeciesEntity): Long

    @Update
    suspend fun update(species: SpeciesEntity)

    @Query("SELECT * FROM species ORDER BY sortOrder, key")
    fun observeAll(): Flow<List<SpeciesEntity>>

    @Query("SELECT * FROM species ORDER BY sortOrder, key")
    suspend fun getAll(): List<SpeciesEntity>

    @Query("SELECT * FROM species WHERE key = :key LIMIT 1")
    suspend fun findByKey(key: String): SpeciesEntity?

    @Query("SELECT * FROM species WHERE id = :id")
    suspend fun getById(id: Long): SpeciesEntity?

    @Query("DELETE FROM species WHERE id = :id")
    suspend fun deleteById(id: Long)
}
