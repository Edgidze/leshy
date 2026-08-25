package leshy.mushrooms.map.domain.repository

import leshy.mushrooms.map.domain.model.MapFilter
import kotlinx.coroutines.flow.Flow

interface MapFilterRepository {
    fun observeFilter(): Flow<MapFilter>
    suspend fun setDateRange(startMillis: Long?, endMillis: Long?)
    suspend fun setMonthRange(from: Int?, to: Int?)
}
