package compose.project.leshy.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import compose.project.leshy.domain.model.MapFilter
import compose.project.leshy.domain.repository.MapFilterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val FILTER_DATE_START = longPreferencesKey("filter_date_start")
private val FILTER_DATE_END = longPreferencesKey("filter_date_end")
private val FILTER_MONTH_FROM = intPreferencesKey("filter_month_from")
private val FILTER_MONTH_TO = intPreferencesKey("filter_month_to")

class MapFilterRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : MapFilterRepository {
    override fun observeFilter(): Flow<MapFilter> = dataStore.data.map { prefs ->
        MapFilter(
            startMillis = prefs[FILTER_DATE_START],
            endMillis = prefs[FILTER_DATE_END],
            monthFrom = prefs[FILTER_MONTH_FROM],
            monthTo = prefs[FILTER_MONTH_TO],
        )
    }

    override suspend fun setDateRange(startMillis: Long?, endMillis: Long?) {
        dataStore.edit { prefs ->
            if (startMillis == null) prefs.remove(FILTER_DATE_START) else prefs[FILTER_DATE_START] = startMillis
            if (endMillis == null) prefs.remove(FILTER_DATE_END) else prefs[FILTER_DATE_END] = endMillis
        }
    }

    override suspend fun setMonthRange(from: Int?, to: Int?) {
        dataStore.edit { prefs ->
            if (from == null) prefs.remove(FILTER_MONTH_FROM) else prefs[FILTER_MONTH_FROM] = from
            if (to == null) prefs.remove(FILTER_MONTH_TO) else prefs[FILTER_MONTH_TO] = to
        }
    }
}
