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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: CollectionEntity): Long

    @Update
    suspend fun update(collection: CollectionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMember(crossRef: CategoryCollectionCrossRef)

    @Query("SELECT categoryId FROM category_collections WHERE collectionId = :collectionId")
    suspend fun getMemberCategoryIds(collectionId: Long): List<Long>

    @Query("SELECT * FROM category_collections")
    fun observeAllMemberships(): Flow<List<CategoryCollectionCrossRef>>
}
