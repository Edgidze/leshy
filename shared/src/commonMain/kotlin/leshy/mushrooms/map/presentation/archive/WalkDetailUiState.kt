package leshy.mushrooms.map.presentation.archive

import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.Walk

data class CategoryCount(val category: Category, val count: Int)

data class WalkDetailUiState(
    val walk: Walk? = null,
    val mushroomCounts: List<CategoryCount> = emptyList(),
    val marks: List<FieldMark> = emptyList(),
    val track: List<GeoPoint> = emptyList(),
    val categories: List<Category> = emptyList(),
    val showDeleteConfirmation: Boolean = false,
    val showEditDialog: Boolean = false,
    val deleted: Boolean = false,
)
