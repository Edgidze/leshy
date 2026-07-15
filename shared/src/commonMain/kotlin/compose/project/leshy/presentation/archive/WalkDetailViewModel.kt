package compose.project.leshy.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.TrackPointRepository
import compose.project.leshy.domain.repository.WalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class WalkDetailViewModel(
    private val walkId: Long,
    private val walkRepository: WalkRepository,
    fieldMarkRepository: FieldMarkRepository,
    trackPointRepository: TrackPointRepository,
    categoryRepository: CategoryRepository,
) : ViewModel() {

    private val showDeleteConfirmation = MutableStateFlow(false)
    private val deleted = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(WalkDetailUiState())
    val uiState: StateFlow<WalkDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val dataFlow = combine(
                walkRepository.observeById(walkId),
                fieldMarkRepository.observeByWalkId(walkId),
                trackPointRepository.observeByWalkId(walkId),
                categoryRepository.observeAll(),
            ) { walk, marks, trackPoints, categories ->
                val categoryById = categories.associateBy { it.id }
                val mushroomCounts = marks
                    .filter { it.type == MarkType.MUSHROOM }
                    .groupingBy { it.categoryId }
                    .eachCount()
                    .mapNotNull { (categoryId, count) -> categoryById[categoryId]?.let { CategoryCount(it, count) } }
                    .sortedBy { it.category.order }
                WalkDetailUiState(
                    walk = walk,
                    mushroomCounts = mushroomCounts,
                    marks = marks,
                    track = trackPoints.map { GeoPoint(it.lat, it.lon, it.elevation, it.timestamp) },
                    categories = categories,
                )
            }
            combine(dataFlow, showDeleteConfirmation, deleted) { data, showConfirm, isDeleted ->
                data.copy(showDeleteConfirmation = showConfirm, deleted = isDeleted)
            }.collect { state -> _uiState.value = state }
        }
    }

    fun onDeleteClick() {
        showDeleteConfirmation.value = true
    }

    fun onDeleteDismiss() {
        showDeleteConfirmation.value = false
    }

    fun onDeleteConfirm() {
        viewModelScope.launch {
            showDeleteConfirmation.value = false
            _uiState.value.walk?.let { walkRepository.delete(it) }
            deleted.value = true
        }
    }
}
