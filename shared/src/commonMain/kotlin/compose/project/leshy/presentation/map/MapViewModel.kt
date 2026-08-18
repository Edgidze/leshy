package compose.project.leshy.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MapFilter
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.model.TrackPoint
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.MapFilterRepository
import compose.project.leshy.domain.repository.TrackPointRepository
import compose.project.leshy.domain.repository.WalkRepository
import compose.project.leshy.domain.usecase.DeletePlaceMarkUseCase
import compose.project.leshy.domain.usecase.UpdatePlaceMarkUseCase
import compose.project.leshy.domain.util.computeFilterCount
import compose.project.leshy.domain.util.matchesDateAndSeason
import compose.project.leshy.presentation.archive.CategoryCount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

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
    mapFilterRepository: MapFilterRepository,
    private val updatePlaceMark: UpdatePlaceMarkUseCase,
    private val deletePlaceMark: DeletePlaceMarkUseCase,
) : ViewModel() {

    private val _mode = MutableStateFlow(MapMode.MAP)

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

            combine(rawData, _mode, mapFilterRepository.observeFilter()) { raw, mode, filter ->
                buildUiState(raw, mode, filter)
            }.collect { state -> _uiState.value = state }
        }
    }

    fun selectMode(mode: MapMode) {
        _mode.value = mode
    }

    fun updatePlace(mark: FieldMark, name: String, description: String, photoPath: String?) {
        // No manual _uiState splice needed: fieldMarkRepository.observeAll() above is a live Room
        // Flow, so the update re-emits into uiState.placeMarks on its own once it commits.
        viewModelScope.launch { updatePlaceMark(mark, name, description, photoPath) }
    }

    fun deletePlace(mark: FieldMark) {
        viewModelScope.launch { deletePlaceMark(mark) }
    }

    private fun buildUiState(raw: RawMapData, mode: MapMode, filter: MapFilter): MapUiState {
        val filteredWalkIds = raw.walks
            .filter { it.matchesDateAndSeason(filter) }
            .map { it.id }
            .toSet()

        val tracks = raw.trackPoints
            .filter { it.walkId in filteredWalkIds }
            .groupBy(TrackPoint::walkId) { GeoPoint(it.lat, it.lon, it.elevation, it.timestamp) }

        val categoryById = raw.categories.associateBy { it.id }
        val mushroomMarks = raw.marks.filter {
            it.walkId in filteredWalkIds && it.type == MarkType.MUSHROOM && categoryById[it.categoryId]?.isActive == true
        }
        val placeMarks = raw.marks.filter { it.walkId in filteredWalkIds && it.type == MarkType.POI }

        val categoryCounts = mushroomMarks
            .groupingBy { it.categoryId }
            .eachCount()
            .mapNotNull { (categoryId, count) -> categoryById[categoryId]?.let { CategoryCount(it, count) } }
            .sortedBy { it.category.order }

        return MapUiState(
            mode = mode,
            tracks = tracks,
            findMarks = mushroomMarks,
            placeMarks = placeMarks,
            categories = raw.categories,
            stats = MapStats(
                walkCount = filteredWalkIds.size,
                totalDistanceMeters = raw.walks.filter { it.id in filteredWalkIds }.sumOf { it.distanceMeters },
                totalMushroomCount = mushroomMarks.size,
                categoryCounts = categoryCounts,
            ),
            filterCount = computeFilterCount(filter, raw.walks, raw.categories),
        )
    }
}
