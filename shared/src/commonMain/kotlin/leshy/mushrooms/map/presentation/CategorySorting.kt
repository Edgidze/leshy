package leshy.mushrooms.map.presentation

import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.i18n.categoryDisplayName
import leshy.mushrooms.map.i18n.hasLocalizedName

/**
 * Orders mushroom categories alphabetically by display name — shared by the Record screen's tile
 * feed ([leshy.mushrooms.map.presentation.record.RecordViewModel]) and the map filter dialog's
 * species list ([leshy.mushrooms.map.presentation.mapfilter.MapFilterViewModel]) so both stay in
 * sync. Species with no real name in [language] (Latin-name/`nameKey` fallback — see
 * [hasLocalizedName]) sort after every species that does have one, each group alphabetical on its
 * own — otherwise Latin names would interleave alphabetically among translated ones instead of
 * reading as a distinct trailing group.
 */
fun sortCategories(categories: List<Category>, language: AppLanguage): List<Category> {
    val (named, fallback) = categories.partition { hasLocalizedName(it, language) }
    return named.sortedBy { categoryDisplayName(it, language) } +
        fallback.sortedBy { categoryDisplayName(it, language) }
}

/**
 * Reorders [base] so that ids in [recencyOrder] (most-recently-bumped first) lead the list,
 * followed by the rest of [base] in its original order. Used by the Record screen to move a
 * mushroom's tile to the leftmost position each time it's tapped, for the duration of one walk —
 * see [leshy.mushrooms.map.presentation.record.RecordViewModel].
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
 * Reorders [categories] for the mushroom-search dialog — thin wrapper around [searchOrdered]
 * keyed by [categoryDisplayName]. See [searchOrdered]'s doc for the ranking itself.
 */
fun searchOrderedCategories(categories: List<Category>, query: String, language: AppLanguage): List<Category> =
    searchOrdered(categories, query) { categoryDisplayName(it, language) }

/**
 * Reorders [items] by relevance to [query], ranked by [label]: entries whose label starts with
 * [query] first, then labels merely containing it, then labels containing a long leading chunk of
 * it (tolerates the trailing character(s) not matching yet — e.g. mid-typo, or a not-yet-finished
 * grammatical ending), then the rest — all buckets keep [items]' relative order (the third one
 * breaks ties by matched-chunk length, longest first), so nothing jumps around beyond what the
 * query explains and the whole list stays reachable. Originally specific to
 * [searchOrderedCategories] (mushroom search in the Record screen's search dialog); generalized in
 * Phase 4 of `.claude/plans/countries-and-languages.md` so `LanguagePickerScreen`'s language search
 * ranks the same way, on `AppLanguage.endonym`/`englishName` instead of a category name. Indexes
 * into [items] rather than comparing elements directly, so it works for types without a stable
 * identity field to dedupe by (unlike [Category.id], which the pre-generalization version used).
 */
fun <T> searchOrdered(items: List<T>, query: String, label: (T) -> String): List<T> {
    val q = query.trim().lowercase()
    if (q.isEmpty()) return items
    val labels = items.map { label(it).lowercase() }
    val (prefixIndices, restIndices) = items.indices.partition { labels[it].startsWith(q) }
    val (containsIndices, rest2Indices) = restIndices.partition { labels[it].contains(q) }
    val fuzzyMatchLengths = rest2Indices.mapNotNull { index ->
        val matchedLength = (q.length - 1 downTo MIN_FUZZY_PREFIX_LENGTH)
            .firstOrNull { len -> labels[index].contains(q.substring(0, len)) }
        matchedLength?.let { index to it }
    }
    val fuzzyIndices = fuzzyMatchLengths.sortedByDescending { it.second }.map { it.first }
    val fuzzySet = fuzzyIndices.toSet()
    val otherIndices = rest2Indices.filter { it !in fuzzySet }
    return (prefixIndices + containsIndices + fuzzyIndices + otherIndices).map { items[it] }
}
