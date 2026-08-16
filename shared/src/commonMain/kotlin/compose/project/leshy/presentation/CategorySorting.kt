package compose.project.leshy.presentation

import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.MushroomSortOrder
import compose.project.leshy.i18n.categoryDisplayName

/**
 * Orders mushroom categories per the user's [MushroomSortOrder] setting — shared by the Record
 * screen's tile feed ([compose.project.leshy.presentation.record.RecordViewModel]) and the map
 * filter dialog's species list ([compose.project.leshy.presentation.mapfilter.MapFilterViewModel])
 * so both stay in sync with the same choice made in Settings.
 */
fun sortCategories(categories: List<Category>, sortOrder: MushroomSortOrder, language: AppLanguage): List<Category> =
    when (sortOrder) {
        MushroomSortOrder.EDIBILITY_THEN_ALPHABETICAL -> categories.sortedWith(
            compareBy(
                { it.edibilityStatus.ordinal },
                { categoryDisplayName(it.nameKey, language) },
            ),
        )
        MushroomSortOrder.ALPHABETICAL -> categories.sortedBy { categoryDisplayName(it.nameKey, language) }
    }
