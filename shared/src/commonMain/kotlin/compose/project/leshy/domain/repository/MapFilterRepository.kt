package compose.project.leshy.domain.repository

import compose.project.leshy.domain.model.MapFilter
import kotlinx.coroutines.flow.Flow

interface MapFilterRepository {
    fun observeFilter(): Flow<MapFilter>
    suspend fun setDateRange(startMillis: Long?, endMillis: Long?)
    suspend fun setMonthRange(from: Int?, to: Int?)
}
