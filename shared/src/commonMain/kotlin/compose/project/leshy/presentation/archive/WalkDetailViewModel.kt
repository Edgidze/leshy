package compose.project.leshy.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.TrackPointRepository
import compose.project.leshy.domain.repository.WalkRepository
import compose.project.leshy.domain.usecase.DeletePlaceMarkUseCase
import compose.project.leshy.domain.usecase.UpdatePlaceMarkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.Path.Companion.toPath

class WalkDetailViewModel(
    private val walkId: Long,
    private val walkRepository: WalkRepository,
    fieldMarkRepository: FieldMarkRepository,
    trackPointRepository: TrackPointRepository,
    categoryRepository: CategoryRepository,
    private val updatePlaceMark: UpdatePlaceMarkUseCase,
    private val deletePlaceMark: DeletePlaceMarkUseCase,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) : ViewModel() {

    private val showDeleteConfirmation = MutableStateFlow(false)
    private val showEditDialog = MutableStateFlow(false)
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
                    .sortedWith(compareByDescending<CategoryCount> { it.count }.thenBy { it.category.order })
                WalkDetailUiState(
                    walk = walk,
                    mushroomCounts = mushroomCounts,
                    marks = marks,
                    track = trackPoints.map { GeoPoint(it.lat, it.lon, it.elevation, it.timestamp) },
                    categories = categories,
                )
            }
            combine(dataFlow, showDeleteConfirmation, showEditDialog, deleted) { data, showConfirm, showEdit, isDeleted ->
                data.copy(showDeleteConfirmation = showConfirm, showEditDialog = showEdit, deleted = isDeleted)
            }.collect { state -> _uiState.value = state }
        }
    }

    fun onEditClick() {
        showEditDialog.value = true
    }

    fun onEditDismiss() {
        showEditDialog.value = false
    }

    fun onEditConfirm(name: String) {
        viewModelScope.launch {
            showEditDialog.value = false
            _uiState.value.walk?.let { walkRepository.update(it.copy(name = name)) }
        }
    }

    fun onDescriptionConfirm(description: String) {
        viewModelScope.launch {
            _uiState.value.walk?.let { walkRepository.update(it.copy(description = description.ifBlank { null })) }
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
            val walk = _uiState.value.walk
            // Room's ON DELETE CASCADE on walkId only removes the `objects`/`track_points` rows,
            // not the photo/thumbnail files they point at — collect those paths before the delete,
            // a cascading DELETE doesn't return the rows it removes.
            val orphanedPhotoPaths = _uiState.value.marks.mapNotNull { it.photoPath } +
                listOfNotNull(walk?.thumbnailPath)
            if (walk != null) {
                walkRepository.delete(walk)
                for (photoPath in orphanedPhotoPaths) {
                    // Best-effort: a leftover file is harmless, a failed cleanup shouldn't surface as an error.
                    runCatching { fileSystem.delete(photoPath.toPath()) }
                }
            }
            deleted.value = true
        }
    }

    fun updatePlace(mark: FieldMark, name: String, description: String, photoPath: String?) {
        // No manual _uiState splice needed: fieldMarkRepository.observeByWalkId(walkId) above is a
        // live Room Flow, so the update re-emits into uiState.marks on its own once it commits.
        viewModelScope.launch { updatePlaceMark(mark, name, description, photoPath) }
    }

    fun deletePlace(mark: FieldMark) {
        viewModelScope.launch { deletePlaceMark(mark) }
    }
}
