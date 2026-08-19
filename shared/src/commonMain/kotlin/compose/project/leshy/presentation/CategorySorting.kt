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
                { categoryDisplayName(it, language) },
            ),
        )
        MushroomSortOrder.ALPHABETICAL -> categories.sortedBy { categoryDisplayName(it, language) }
    }

/**
 * Reorders [base] so that ids in [recencyOrder] (most-recently-bumped first) lead the list,
 * followed by the rest of [base] in its original order. Used by the Record screen to move a
 * mushroom's tile to the leftmost position each time it's tapped, for the duration of one walk —
 * see [compose.project.leshy.presentation.record.RecordViewModel].
 */
fun applyRecencyOrder(base: List<Category>, recencyOrder: List<Long>): List<Category> {
    if (recencyOrder.isEmpty()) return base
    val byId = base.associateBy { it.id }
    val bumped = recencyOrder.mapNotNull { byId[it] }
    val bumpedIds = bumped.map { it.id }.toSet()
    return bumped + base.filter { it.id !in bumpedIds }
}

/**
 * Reorders [categories] for the mushroom-search dialog: names starting with [query] first, then
 * names merely containing it, then the rest — all three buckets keep [categories]' relative order,
 * so nothing jumps around beyond what the query explains and the whole catalog stays reachable.
 */
fun searchOrderedCategories(categories: List<Category>, query: String, language: AppLanguage): List<Category> {
    val q = query.trim().lowercase()
    if (q.isEmpty()) return categories
    val (prefixMatches, rest) = categories.partition { categoryDisplayName(it, language).lowercase().startsWith(q) }
    val (containsMatches, others) = rest.partition { categoryDisplayName(it, language).lowercase().contains(q) }
    return prefixMatches + containsMatches + others
}
