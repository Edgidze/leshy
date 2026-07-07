package compose.project.leshy.presentation.archive

import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.Walk

data class CategoryCount(val category: Category, val count: Int)

data class WalkDetailUiState(
    val walk: Walk? = null,
    val mushroomCounts: List<CategoryCount> = emptyList(),
    val marks: List<FieldMark> = emptyList(),
    val track: List<GeoPoint> = emptyList(),
    val categories: List<Category> = emptyList(),
)
