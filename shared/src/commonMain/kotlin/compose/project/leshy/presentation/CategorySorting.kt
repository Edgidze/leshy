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
        MushroomSortOrder.ALPHABETICAL -> categories.sortedBy { categoryDisplayName(it, language) }
        // EdibilityStatus.NOT_SPECIFIED sorts before POISONOUS by ordinal, so this already puts
        // poisonous species last — alphabetical within each of the two groups.
        MushroomSortOrder.POISONOUS_LAST -> categories.sortedWith(
            compareBy(
                { it.edibilityStatus.ordinal },
                { categoryDisplayName(it, language) },
            ),
        )
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

/** Below this, a matched leading chunk of [query] is too short to mean anything — pure noise. */
private const val MIN_FUZZY_PREFIX_LENGTH = 2

/**
 * Reorders [categories] for the mushroom-search dialog: names starting with [query] first, then
 * names merely containing it, then names containing a long leading chunk of it (tolerates the
 * trailing character(s) not matching yet — e.g. mid-typo, or a not-yet-finished grammatical
 * ending), then the rest — all buckets keep [categories]' relative order (the third one breaks
 * ties by matched-chunk length, longest first), so nothing jumps around beyond what the query
 * explains and the whole catalog stays reachable.
 */
fun searchOrderedCategories(categories: List<Category>, query: String, language: AppLanguage): List<Category> {
    val q = query.trim().lowercase()
    if (q.isEmpty()) return categories
    val (prefixMatches, rest) = categories.partition { categoryDisplayName(it, language).lowercase().startsWith(q) }
    val (containsMatches, rest2) = rest.partition { categoryDisplayName(it, language).lowercase().contains(q) }
    val fuzzyMatchLengths = rest2.mapNotNull { category ->
        val name = categoryDisplayName(category, language).lowercase()
        val matchedLength = (q.length - 1 downTo MIN_FUZZY_PREFIX_LENGTH)
            .firstOrNull { len -> name.contains(q.substring(0, len)) }
        matchedLength?.let { category to it }
    }
    val fuzzyMatches = fuzzyMatchLengths.sortedByDescending { it.second }.map { it.first }
    val fuzzyIds = fuzzyMatches.map { it.id }.toSet()
    val others = rest2.filter { it.id !in fuzzyIds }
    return prefixMatches + containsMatches + fuzzyMatches + others
}
