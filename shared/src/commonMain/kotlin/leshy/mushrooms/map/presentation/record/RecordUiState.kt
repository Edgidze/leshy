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

/**
 * Куда лента плиток должна доехать по очередному [RecordUiState.feedScrollSignal].
 *
 * Отдельный тип, а не пара независимых полей рядом с сигналом: адресат прокрутки и манера
 * движения связаны между собой, и до 2026-09-26 их связь держалась только комментарием «всегда
 * обновлять вместе одним `copy`». Заодно появился адресат, которого в паре выразить было нечем —
 * [Tile].
 */
sealed interface FeedScrollTarget {
    /**
     * К началу ленты — туда, куда плитка только что переставлена.
     *
     * [durationMillis] `null` — обычная скорость прокрутки: осознанный переход к плитке (выбор в
     * поиске, создание своего вида), его незачем растягивать. Non-null (ставит
     * [RecordViewModel.flushPendingFrontBumps]) — растянутая на столько миллисекунд прокрутка:
     * перестановка от «+»/«−» должна читаться как наблюдаемое движение, а не как телепорт.
     */
    data class Front(val durationMillis: Int?) : FeedScrollTarget

    /**
     * К плитке вида [categoryId] — в середину видимой части ленты, не меняя порядок.
     *
     * Единственный возможный ответ поиска при включённом «неподвижном порядке грибов»:
     * переставить плитку вперёд там нельзя (настройка ровно это и запрещает — см.
     * [RecordViewModel.revealCategory]), а прокрутка к НАЧАЛУ ленты в этом режиме не показывала
     * выбранный вид вовсе, а лишь уносила человека от него к алфавитному началу каталога. Лупа
     * при этом не помогала ничем — репорт владельца с устройства, 2026-09-26.
     */
    data class Tile(val categoryId: Long) : FeedScrollTarget
}

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
    /** Bumped each time the feed has somewhere to scroll — see [feedScrollTarget]. */
    val feedScrollSignal: Int = 0,
    /** Where the feed should scroll on the current [feedScrollSignal]. */
    val feedScrollTarget: FeedScrollTarget = FeedScrollTarget.Front(null),
    /**
     * Whether the tile feed still reorders itself around recent finds — i.e. Settings'
     * «неподвижный порядок грибов» (freeze order) is OFF. While it does, the front of the feed is
     * the only position in it that means anything, and the Record screen snaps back there every
     * time it resumes — see the `LifecycleResumeEffect` in `RecordScreenContent`.
     */
    val tileOrderFollowsRecency: Boolean = true,
)
