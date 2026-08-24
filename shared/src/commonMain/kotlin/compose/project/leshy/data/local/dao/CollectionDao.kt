package compose.project.leshy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import compose.project.leshy.data.local.entity.CategoryCollectionCrossRef
import compose.project.leshy.data.local.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY `order` ASC")
    fun observeAll(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE nameKey = :nameKey LIMIT 1")
    suspend fun getByNameKey(nameKey: String): CollectionEntity?

    /** One-shot counterpart of [observeAll] — for the country-collections seeding diff (33 rows,
     * one read instead of 33 [getByNameKey] lookups), same reasoning as `CategoryDao.getAll`. */
    @Query("SELECT * FROM collections ORDER BY `order` ASC")
    suspend fun getAll(): List<CollectionEntity>

    @Query("SELECT COUNT(*) FROM collections")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: CollectionEntity): Long

    /** Plain INSERT, not REPLACE — same reasoning as `CategoryDao.insertAll`: these rows are new
     * (`id = 0`), so REPLACE's delete+insert would have nothing to conflict with but would still risk
     * firing `category_collections`' cascade if it ever did. Room runs the whole list in one
     * transaction. */
    @Insert
    suspend fun insertAll(collections: List<CollectionEntity>)

    @Update
    suspend fun update(collection: CollectionEntity)

    @Update
    suspend fun updateAll(collections: List<CollectionEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMember(crossRef: CategoryCollectionCrossRef)

    /** Batch counterpart of [insertMember] — IGNORE makes this idempotent by construction, so the
     * seeding use case can hand it the full desired membership list every time instead of diffing. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMembers(crossRefs: List<CategoryCollectionCrossRef>)

    @Query("SELECT categoryId FROM category_collections WHERE collectionId = :collectionId")
    suspend fun getMemberCategoryIds(collectionId: Long): List<Long>

    @Query("SELECT * FROM category_collections")
    fun observeAllMemberships(): Flow<List<CategoryCollectionCrossRef>>
}
