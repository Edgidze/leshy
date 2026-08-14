package compose.project.leshy.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.model.TrackPoint
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.TrackPointRepository
import compose.project.leshy.domain.repository.WalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private data class RawArchiveData(
    val walks: List<Walk>,
    val trackPoints: List<TrackPoint>,
    val marks: List<FieldMark>,
)

class ArchiveViewModel(
    private val walkRepository: WalkRepository,
    private val trackPointRepository: TrackPointRepository,
    private val fieldMarkRepository: FieldMarkRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArchiveUiState())
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                walkRepository.observeAll(),
                trackPointRepository.observeAll(),
                fieldMarkRepository.observeAll(),
            ) { walks, trackPoints, marks -> RawArchiveData(walks, trackPoints, marks) }
                .collect { raw -> _uiState.value = buildUiState(raw) }
        }
    }

    private fun buildUiState(raw: RawArchiveData): ArchiveUiState {
        val tracksByWalk = raw.trackPoints
            .groupBy(TrackPoint::walkId) { GeoPoint(it.lat, it.lon, it.elevation, it.timestamp) }
        val findsByWalk = raw.marks
            .filter { it.type == MarkType.MUSHROOM }
            .groupBy(FieldMark::walkId) { GeoPoint(it.lat, it.lon, null, it.timestamp) }

        // raw.walks is already ordered newest-first by the DAO query (ORDER BY startTime DESC).
        val items = raw.walks.map { walk ->
            WalkArchiveItem(
                walk = walk,
                track = tracksByWalk[walk.id].orEmpty(),
                findLocations = findsByWalk[walk.id].orEmpty(),
            )
        }
        return ArchiveUiState(items = items)
    }
}
