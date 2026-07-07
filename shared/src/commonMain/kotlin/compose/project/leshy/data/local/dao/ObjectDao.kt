package compose.project.leshy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import compose.project.leshy.data.local.entity.ObjectEntity
import compose.project.leshy.data.local.entity.ObjectType
import kotlinx.coroutines.flow.Flow

@Dao
interface ObjectDao {
    @Query("SELECT * FROM objects ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<ObjectEntity>>

    @Query("SELECT * FROM objects WHERE walkId = :walkId ORDER BY timestamp ASC")
    fun observeByWalkId(walkId: Long): Flow<List<ObjectEntity>>

    @Query(
        "SELECT * FROM objects WHERE walkId = :walkId AND categoryId = :categoryId " +
            "ORDER BY timestamp DESC LIMIT 1",
    )
    suspend fun getLastByWalkAndCategory(walkId: Long, categoryId: Long): ObjectEntity?

    @Query(
        "SELECT COUNT(*) FROM objects WHERE walkId = :walkId AND categoryId = :categoryId " +
            "AND type = :type",
    )
    suspend fun countByWalkAndCategory(walkId: Long, categoryId: Long, type: ObjectType = ObjectType.MUSHROOM): Int

    @Insert
    suspend fun insert(objectEntity: ObjectEntity): Long

    @Delete
    suspend fun delete(objectEntity: ObjectEntity)
}
