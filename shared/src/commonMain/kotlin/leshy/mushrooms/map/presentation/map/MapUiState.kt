package leshy.mushrooms.map.presentation.map

import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.presentation.archive.CategoryCount

/**
 * Свод по всем прогулкам, попавшим в фильтр — ровно те же величины, что экран детализации
 * показывает по одной прогулке, только просуммированные.
 *
 * [totalDurationMillis] считается по прогулкам с проставленным «Финишем»: у незавершённой
 * прогулки продолжительности ещё нет, и подставлять ей «сейчас минус старт» нельзя — свод от
 * этого рос бы сам по себе, пока экран открыт.
 */
data class MapStats(
    val walkCount: Int = 0,
    val totalDistanceMeters: Double = 0.0,
    val totalDurationMillis: Long = 0L,
    val totalMushroomCount: Int = 0,
    val categoryCounts: List<CategoryCount> = emptyList(),
)

data class MapUiState(
    val tracks: Map<Long, List<GeoPoint>> = emptyMap(),
    val findMarks: List<FieldMark> = emptyList(),
    val placeMarks: List<FieldMark> = emptyList(),
    val categories: List<Category> = emptyList(),
    val stats: MapStats = MapStats(),
    /**
     * Есть ли хоть одна записанная прогулка — БЕЗ учёта фильтра, в отличие от
     * [MapStats.walkCount]. По нему экран решает, показывать ли приглашение записать первую
     * прогулку вместо всей страницы: под фильтром свод пустеет и у того, у кого прогулки есть,
     * а приглашение унесло бы с собой и ползунок, которым это исправляется.
     */
    val hasAnyWalks: Boolean = false,
    val filterCount: Int = 0,
    /** `true` до первой выдачи из базы — см. `ArchiveUiState.isLoading`, там же и зачем. */
    val isLoading: Boolean = true,
    /**
     * `true`, пока после сдвига ползунка фильтра пересчитывается свод или перечитываются треки.
     * Отдельно от [isLoading]: там ещё нечего показывать, а здесь на экране лежат прежние числа,
     * и сказать надо только то, что они сейчас сменятся.
     */
    val isRecalculating: Boolean = false,
)
