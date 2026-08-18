package compose.project.leshy.presentation.map

import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.presentation.archive.CategoryCount

enum class MapMode {
    MAP,
    STATS,
}

data class MapStats(
    val walkCount: Int = 0,
    val totalDistanceMeters: Double = 0.0,
    val totalMushroomCount: Int = 0,
    val categoryCounts: List<CategoryCount> = emptyList(),
)

data class MapUiState(
    val mode: MapMode = MapMode.MAP,
    val tracks: Map<Long, List<GeoPoint>> = emptyMap(),
    val findMarks: List<FieldMark> = emptyList(),
    val placeMarks: List<FieldMark> = emptyList(),
    val categories: List<Category> = emptyList(),
    val stats: MapStats = MapStats(),
    val filterCount: Int = 0,
)
