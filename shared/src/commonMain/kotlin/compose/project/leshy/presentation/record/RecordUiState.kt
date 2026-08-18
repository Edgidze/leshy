package compose.project.leshy.presentation.record

import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint

data class RecordUiState(
    val walkName: String = "",
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val elapsedMillis: Long = 0L,
    val distanceMeters: Double = 0.0,
    val categories: List<Category> = emptyList(),
    val mushroomCounts: Map<Long, Int> = emptyMap(),
    val currentLocation: GeoPoint? = null,
    val trackPoints: List<GeoPoint> = emptyList(),
    val marks: List<FieldMark> = emptyList(),
    val historicalFinds: List<FieldMark> = emptyList(),
    val historicalPlaces: List<FieldMark> = emptyList(),
    val filterCount: Int = 0,
    val justFinished: Boolean = false,
    /** Bumped each time a tile is moved to the front of the feed — see [RecordViewModel.bringCategoryToFront]. */
    val scrollToStartSignal: Int = 0,
)
