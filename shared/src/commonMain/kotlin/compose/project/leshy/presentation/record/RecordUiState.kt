package compose.project.leshy.presentation.record

import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.util.TurnDirection

data class NavigationOverlayState(
    val targetId: Long,
    val targetName: String,
    val targetLat: Double,
    val targetLon: Double,
    val distanceMeters: Double,
    val hasArrived: Boolean,
    /** Null until enough GPS movement has occurred to derive a course-over-ground. */
    val turnDirection: TurnDirection?,
    /** Null for [TurnDirection.AHEAD] and while [turnDirection] itself is null. */
    val turnDegrees: Double?,
)

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
    val navigationTarget: NavigationOverlayState? = null,
    val justFinished: Boolean = false,
    /** Bumped each time a tile is moved to the front of the feed — see [RecordViewModel.bringCategoryToFront]. */
    val scrollToStartSignal: Int = 0,
)
