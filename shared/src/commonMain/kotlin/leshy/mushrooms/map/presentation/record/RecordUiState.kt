package leshy.mushrooms.map.presentation.record

import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.util.TurnDirection

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
    // Elapsed walk time is deliberately NOT here — it ticks once a second, and this whole object
    // travels into RecordScreenContent as one parameter. See RecordViewModel.elapsedMillis.
    val distanceMeters: Double = 0.0,
    val categories: List<Category> = emptyList(),
    val mushroomCounts: Map<Long, Int> = emptyMap(),
    val currentLocation: GeoPoint? = null,
    /** The platform cannot deliver fixes at all — permission denied, or location services off.
     * Distinct from "no fix yet": this one needs the user to change something in system settings,
     * so the Record screen says so instead of leaving them waiting. */
    val locationUnavailable: Boolean = false,
    val trackPoints: List<GeoPoint> = emptyList(),
    val marks: List<FieldMark> = emptyList(),
    val historicalFinds: List<FieldMark> = emptyList(),
    /** Tracks of past (finished) walks that pass the date/season filter, keyed by walk id — drawn
     * as a muted background layer under the current walk's own track. Empty while the filter's
     * "show past routes" toggle is off. */
    val historicalTracks: Map<Long, List<GeoPoint>> = emptyMap(),
    val historicalPlaces: List<FieldMark> = emptyList(),
    val filterCount: Int = 0,
    val navigationTarget: NavigationOverlayState? = null,
    val justFinished: Boolean = false,
    /** Bumped each time a tile is moved to the front of the feed — see [RecordViewModel.bringCategoryToFront]. */
    val scrollToStartSignal: Int = 0,
    /**
     * Null for an immediate, deliberate jump-to-tile ([RecordViewModel.bringCategoryToFront] —
     * search-dialog selection, new-species creation): the feed snaps to the front at its usual
     * scroll speed. Non-null (set by [RecordViewModel.flushPendingFrontBumps]) means this reorder
     * came from a quiet +/- tap settling into place — the feed should instead scroll to the front
     * over this many milliseconds, so the reorder reads as a deliberate, observable motion rather
     * than a teleport. Always set together with [scrollToStartSignal] in the same state update, so
     * a reader of the signal always sees the value meant for that specific event.
     */
    val scrollToStartDurationMillis: Int? = null,
)
