package compose.project.leshy.presentation.mapfilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.MapFilterRepository
import compose.project.leshy.domain.repository.SettingsRepository
import compose.project.leshy.domain.repository.WalkRepository
import compose.project.leshy.domain.usecase.MISC_CATEGORY_NAME_KEY
import compose.project.leshy.domain.util.MILLIS_PER_DAY
import compose.project.leshy.presentation.sortCategories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MapFilterViewModel(
    private val walkRepository: WalkRepository,
    private val categoryRepository: CategoryRepository,
    private val mapFilterRepository: MapFilterRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapFilterUiState())
    val uiState: StateFlow<MapFilterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val sortSettings = combine(
                settingsRepository.observeMushroomSortOrder(),
                settingsRepository.observeLanguage(),
            ) { sortOrder, language -> sortOrder to language }
            combine(
                walkRepository.observeAll(),
                categoryRepository.observeAll(),
                mapFilterRepository.observeFilter(),
                sortSettings,
            ) { walks, categories, filter, (sortOrder, language) ->
                val starts = walks.map { it.startTime }
                MapFilterUiState(
                    minWalkStart = starts.minOrNull(),
                    maxWalkStart = starts.maxOrNull(),
                    startMillis = filter.startMillis ?: starts.minOrNull(),
                    endMillis = filter.endMillis ?: starts.maxOrNull(),
                    monthFrom = filter.monthFrom ?: 1,
                    monthTo = filter.monthTo ?: 12,
                    categories = sortCategories(
                        categories.filter { it.nameKey != MISC_CATEGORY_NAME_KEY },
                        sortOrder,
                        language,
                    ),
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun setDateRange(startMillis: Long, endMillis: Long) {
        val state = _uiState.value
        // Compared by calendar day, not exact millis — the slider itself only ever commits
        // day-floor values, so "full range" must be judged on the same granularity or dragging
        // to the visual extremes would never match a walk's actual (later-in-the-day) timestamp.
        val startDay = startMillis / MILLIS_PER_DAY
        val endDay = endMillis / MILLIS_PER_DAY
        val minDay = (state.minWalkStart ?: startMillis) / MILLIS_PER_DAY
        val maxDay = (state.maxWalkStart ?: endMillis) / MILLIS_PER_DAY
        val isFullRange = startDay <= minDay && endDay >= maxDay
        viewModelScope.launch {
            mapFilterRepository.setDateRange(
                if (isFullRange) null else startMillis,
                if (isFullRange) null else endMillis,
            )
        }
    }

    fun setMonthRange(from: Int, to: Int) {
        viewModelScope.launch {
            mapFilterRepository.setMonthRange(if (from <= 1) null else from, if (to >= 12) null else to)
        }
    }

    fun setCategoryIncluded(category: Category, included: Boolean) {
        viewModelScope.launch { categoryRepository.upsert(category.copy(isActive = included)) }
    }
}
