package leshy.mushrooms.map.presentation.mapfilter

import leshy.mushrooms.map.domain.model.Category

data class MapFilterUiState(
    val minWalkStart: Long? = null,
    val maxWalkStart: Long? = null,
    val startMillis: Long? = null,
    val endMillis: Long? = null,
    val monthFrom: Int = 1,
    val monthTo: Int = 12,
    val showPastRoutes: Boolean = true,
    val categories: List<Category> = emptyList(),
)
