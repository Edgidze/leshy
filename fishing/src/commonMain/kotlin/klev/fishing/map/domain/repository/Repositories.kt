package klev.fishing.map.domain.repository

import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.model.Trip
import klev.fishing.map.domain.model.TripTrackPoint
import klev.fishing.map.domain.model.TripWeather
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeAll(): Flow<List<Trip>>
    fun observeActive(): Flow<Trip?>
    fun observeById(id: Long): Flow<Trip?>
    suspend fun findActive(): Trip?
    suspend fun getById(id: Long): Trip?
    suspend fun start(method: FishingMethod, startLat: Double, startLon: Double, at: Long): Long
    suspend fun finish(id: Long, endLat: Double?, endLon: Double?, distanceMeters: Double, at: Long)
    suspend fun setTitle(id: Long, title: String?)
    suspend fun setWaterBody(id: Long, waterBody: String?)
    suspend fun setMethod(id: Long, method: FishingMethod)
    suspend fun setWeather(id: Long, weather: TripWeather?)
    suspend fun setThumbnail(id: Long, path: String?)
    suspend fun setDistance(id: Long, distanceMeters: Double)
    suspend fun delete(id: Long)
}

interface CatchRepository {
    fun observeByTrip(tripId: Long): Flow<List<Catch>>
    fun observeAll(): Flow<List<Catch>>
    fun observeRecentBaits(): Flow<List<String>>
    suspend fun add(item: Catch): Long
    suspend fun update(item: Catch)
    suspend fun getById(id: Long): Catch?
    suspend fun delete(id: Long)
}

interface SpeciesRepository {
    fun observeAll(): Flow<List<FishSpecies>>
    suspend fun getAll(): List<FishSpecies>
    suspend fun getById(id: Long): FishSpecies?
    suspend fun setActive(id: Long, isActive: Boolean)
}

interface TripTrackPointRepository {
    fun observeByTrip(tripId: Long): Flow<List<TripTrackPoint>>
    suspend fun getByTrip(tripId: Long): List<TripTrackPoint>
    suspend fun lastOf(tripId: Long): TripTrackPoint?
    suspend fun append(tripId: Long, lat: Double, lon: Double, at: Long)
}
