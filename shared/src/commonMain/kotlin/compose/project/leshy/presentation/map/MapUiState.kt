package compose.project.leshy.presentation.map

import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.presentation.archive.CategoryCount

enum class MapMode {
    MAP,
    STATS,
}

data class MapPeriod(val year: Int, val month: Int)

data class MapStats(
    val walkCount: Int = 0,
    val totalDistanceMeters: Double = 0.0,
    val totalMushroomCount: Int = 0,
    val categoryCounts: List<CategoryCount> = emptyList(),
)

data class MapUiState(
    val mode: MapMode = MapMode.MAP,
    val availablePeriods: List<MapPeriod> = emptyList(),
    val selectedPeriod: MapPeriod? = null,
    val tracks: Map<Long, List<GeoPoint>> = emptyMap(),
    val findLocations: List<GeoPoint> = emptyList(),
    val stats: MapStats = MapStats(),
)
