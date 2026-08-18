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
import compose.project.leshy.domain.usecase.BackfillWalkThumbnailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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
    private val backfillWalkThumbnails: BackfillWalkThumbnailsUseCase,
) : ViewModel() {

    // UI-only flags, combined with the Room-backed item list in a second combine() below — kept
    // separate so a new DB emission (e.g. after a delete) can't silently reset them. See
    // presentation/CLAUDE.md.
    private val selectedWalkIds = MutableStateFlow<Set<Long>>(emptySet())
    private val showDeleteConfirmation = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(ArchiveUiState())
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()

    init {
        // Independent of the UI-state flow below: repairs walks whose thumbnail is missing a map
        // background (see BackfillWalkThumbnailsUseCase) without delaying the Archive list itself.
        viewModelScope.launch { backfillWalkThumbnails() }
        viewModelScope.launch {
            val itemsFlow = combine(
                walkRepository.observeAll(),
                trackPointRepository.observeAll(),
                fieldMarkRepository.observeAll(),
            ) { walks, trackPoints, marks -> RawArchiveData(walks, trackPoints, marks) }
                .map(::buildItems)

            combine(itemsFlow, selectedWalkIds, showDeleteConfirmation) { items, selected, showConfirm ->
                // Drops IDs for walks no longer present (e.g. deleted from another screen) so a
                // stale selection can't linger or reopen the delete button with nothing to delete.
                val validSelected = selected.intersect(items.map { it.walk.id }.toSet())
                ArchiveUiState(
                    items = items,
                    selectedWalkIds = validSelected,
                    showDeleteConfirmation = showConfirm,
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    private fun buildItems(raw: RawArchiveData): List<WalkArchiveItem> {
        val tracksByWalk = raw.trackPoints
            .groupBy(TrackPoint::walkId) { GeoPoint(it.lat, it.lon, it.elevation, it.timestamp) }
        val findsByWalk = raw.marks
            .filter { it.type == MarkType.MUSHROOM }
            .groupBy(FieldMark::walkId) { GeoPoint(it.lat, it.lon, null, it.timestamp) }

        // raw.walks is already ordered newest-first by the DAO query (ORDER BY startTime DESC).
        return raw.walks.map { walk ->
            WalkArchiveItem(
                walk = walk,
                track = tracksByWalk[walk.id].orEmpty(),
                findLocations = findsByWalk[walk.id].orEmpty(),
            )
        }
    }

    /** Long-press entry point: opens selection mode (if not already open) and selects this walk. */
    fun selectWalk(walkId: Long) {
        selectedWalkIds.update { it + walkId }
    }

    /** Plain-tap entry point while already in selection mode. */
    fun toggleSelection(walkId: Long) {
        selectedWalkIds.update { current -> if (walkId in current) current - walkId else current + walkId }
    }

    /** Back press, navigating away, or dismissing the delete dialog — all leave selection mode. */
    fun clearSelection() {
        selectedWalkIds.value = emptySet()
    }

    fun onDeleteClick() {
        showDeleteConfirmation.value = true
    }

    fun onDeleteDismiss() {
        showDeleteConfirmation.value = false
        selectedWalkIds.value = emptySet()
    }

    fun onDeleteConfirm() {
        viewModelScope.launch {
            showDeleteConfirmation.value = false
            val idsToDelete = selectedWalkIds.value
            _uiState.value.items
                .filter { it.walk.id in idsToDelete }
                .forEach { walkRepository.delete(it.walk) }
            selectedWalkIds.value = emptySet()
        }
    }
}
