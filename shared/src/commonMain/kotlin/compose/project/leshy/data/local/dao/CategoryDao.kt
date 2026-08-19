package compose.project.leshy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import compose.project.leshy.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY `order` ASC")
    fun observeActive(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isFilterEligible = 1 ORDER BY `order` ASC")
    fun observeFilterEligible(): Flow<List<CategoryEntity>>

    /** Species outside the bundled catalog — user-created plus imported ones (i.e. everything the
     * "my mushrooms" section owns). The literal matches `CategorySource.APP.name`, which is what
     * `Converters` stores. Unlike the observers above this one ignores isActive/isPicked: a hidden
     * species must stay listed there, or it could never be brought back. */
    @Query("SELECT * FROM categories WHERE source != 'APP' ORDER BY `order` ASC")
    fun observeNonCatalog(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Query("SELECT * FROM categories WHERE nameKey = :nameKey LIMIT 1")
    suspend fun getByNameKey(nameKey: String): CategoryEntity?

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)
}
