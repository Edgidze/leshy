package compose.project.leshy.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.model.TrackPoint
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.TrackPointRepository
import compose.project.leshy.domain.repository.WalkRepository
import compose.project.leshy.presentation.archive.CategoryCount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

private data class RawMapData(
    val walks: List<Walk>,
    val marks: List<FieldMark>,
    val trackPoints: List<TrackPoint>,
    val categories: List<Category>,
)

class MapViewModel(
    walkRepository: WalkRepository,
    fieldMarkRepository: FieldMarkRepository,
    trackPointRepository: TrackPointRepository,
    categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _mode = MutableStateFlow(MapMode.MAP)
    private val _selectedPeriod = MutableStateFlow<MapPeriod?>(null)

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val rawData = combine(
                walkRepository.observeAll(),
                fieldMarkRepository.observeAll(),
                trackPointRepository.observeAll(),
                categoryRepository.observeAll(),
            ) { walks, marks, trackPoints, categories -> RawMapData(walks, marks, trackPoints, categories) }

            combine(rawData, _mode, _selectedPeriod) { raw, mode, period ->
                buildUiState(raw, mode, period)
            }.collect { state -> _uiState.value = state }
        }
    }

    fun selectMode(mode: MapMode) {
        _mode.value = mode
    }

    fun selectPeriod(period: MapPeriod?) {
        _selectedPeriod.value = period
    }

    private fun buildUiState(raw: RawMapData, mode: MapMode, period: MapPeriod?): MapUiState {
        val availablePeriods = raw.walks
            .map { it.startTime.toMapPeriod() }
            .distinct()
            .sortedWith(compareByDescending<MapPeriod> { it.year }.thenByDescending { it.month })

        val filteredWalkIds = raw.walks
            .filter { period == null || it.startTime.toMapPeriod() == period }
            .map { it.id }
            .toSet()

        val tracks = raw.trackPoints
            .filter { it.walkId in filteredWalkIds }
            .groupBy(TrackPoint::walkId) { GeoPoint(it.lat, it.lon, it.elevation, it.timestamp) }

        val mushroomMarks = raw.marks.filter { it.walkId in filteredWalkIds && it.type == MarkType.MUSHROOM }

        val categoryById = raw.categories.associateBy { it.id }
        val categoryCounts = mushroomMarks
            .groupingBy { it.categoryId }
            .eachCount()
            .mapNotNull { (categoryId, count) -> categoryById[categoryId]?.let { CategoryCount(it, count) } }
            .sortedBy { it.category.order }

        return MapUiState(
            mode = mode,
            availablePeriods = availablePeriods,
            selectedPeriod = period,
            tracks = tracks,
            findLocations = mushroomMarks.map { GeoPoint(it.lat, it.lon, null, it.timestamp) },
            stats = MapStats(
                walkCount = filteredWalkIds.size,
                totalDistanceMeters = raw.walks.filter { it.id in filteredWalkIds }.sumOf { it.distanceMeters },
                totalMushroomCount = mushroomMarks.size,
                categoryCounts = categoryCounts,
            ),
        )
    }
}

private fun Long.toMapPeriod(): MapPeriod {
    val dateTime = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())
    return MapPeriod(dateTime.year, dateTime.month.number)
}
