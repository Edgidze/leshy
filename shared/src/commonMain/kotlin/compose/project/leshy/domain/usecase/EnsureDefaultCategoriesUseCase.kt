package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.repository.CategoryRepository

const val MISC_CATEGORY_NAME_KEY = "category_misc"

private val DEFAULT_CATEGORIES = listOf(
    Category(id = 0, nameKey = "category_porcini", colorHex = "#6B4423", iconRef = null, order = 0, isActive = true),
    Category(id = 0, nameKey = "category_chanterelle", colorHex = "#FFA500", iconRef = null, order = 1, isActive = true),
    Category(id = 0, nameKey = "category_ryzhik", colorHex = "#FF6347", iconRef = null, order = 2, isActive = true),
    Category(id = 0, nameKey = "category_boletus", colorHex = "#8B4513", iconRef = null, order = 3, isActive = true),
    Category(id = 0, nameKey = MISC_CATEGORY_NAME_KEY, colorHex = "#808080", iconRef = null, order = 999, isActive = false),
)

class EnsureDefaultCategoriesUseCase(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke() {
        if (categoryRepository.count() > 0) return
        DEFAULT_CATEGORIES.forEach { categoryRepository.upsert(it) }
    }
}
