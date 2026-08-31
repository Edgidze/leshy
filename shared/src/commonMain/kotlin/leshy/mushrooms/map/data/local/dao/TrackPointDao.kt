package leshy.mushrooms.map.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import leshy.mushrooms.map.data.local.entity.TrackPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPointDao {
    @Query("SELECT * FROM track_points ORDER BY walkId ASC, sequence ASC")
    fun observeAll(): Flow<List<TrackPointEntity>>

    @Query("SELECT * FROM track_points WHERE walkId = :walkId ORDER BY sequence ASC")
    fun observeByWalkId(walkId: Long): Flow<List<TrackPointEntity>>

    /**
     * Одноразовое (не `Flow`) чтение точек указанных прогулок.
     *
     * Именно НЕ `Flow` — в этом весь смысл. Room инвалидирует запросы по таблице целиком, так
     * что любой `Flow` над `track_points` переотдаёт весь результат на каждую дописанную точку
     * текущей прогулки, то есть на каждый GPS-фикс. Треки уже ЗАВЕРШЁННЫХ прогулок при этом не
     * меняются — перечитывать их нужно только когда меняется сам набор прогулок, а за этим
     * следят вызывающие (см. `RecordViewModel`/`MapViewModel`).
     *
     * Прореживание для фоновых линий сюда НЕ вынесено, хотя `sequence % :stride = 0` напрашивался:
     * такой отбор роняет хвост трека (последняя точка выживает только если её номер кратен шагу) и
     * на коротких прогулках оставляет меньше двух точек, а от одной точки линия не рисуется вовсе
     * — воспроизведено на прогулке из 4 точек при шаге 4. Прореживает `decimateTrack`
     * (`domain/util/TrackDecimation.kt`), уже после чтения.
     */
    @Query("SELECT * FROM track_points WHERE walkId IN (:walkIds) ORDER BY walkId ASC, sequence ASC")
    suspend fun getByWalkIds(walkIds: Collection<Long>): List<TrackPointEntity>

    @Insert
    suspend fun insert(trackPoint: TrackPointEntity): Long
}
