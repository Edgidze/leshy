package leshy.mushrooms.map.presentation

import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.CategoryCollectionMembership
import leshy.mushrooms.map.domain.model.Collection

enum class CollectionPickState { ALL, SOME, NONE }

/** One collection plus its member species (each carrying its current [Category.isPicked]), for the
 * collection-picker UI shared by Settings and the first-run onboarding screen (see
 * `.claude/plans/mushroom-collections.md`, Phases 1/3). */
data class CollectionPickerItem(val collection: Collection, val members: List<Category>) {
    val pickState: CollectionPickState = when {
        members.isEmpty() || members.all { it.isPicked } -> CollectionPickState.ALL
        members.none { it.isPicked } -> CollectionPickState.NONE
        else -> CollectionPickState.SOME
    }
}

/** Joins the raw collections/categories/membership flows into picker-ready items, ordered like
 * [Collection.order] with members ordered like [Category.order]. */
fun buildCollectionPickerItems(
    collections: List<Collection>,
    categories: List<Category>,
    memberships: List<CategoryCollectionMembership>,
): List<CollectionPickerItem> {
    val categoriesById = categories.associateBy { it.id }
    val memberIdsByCollection = memberships.groupBy({ it.collectionId }, { it.categoryId })
    return collections.sortedBy { it.order }.map { collection ->
        val members = memberIdsByCollection[collection.id].orEmpty()
            .mapNotNull { categoriesById[it] }
            .sortedBy { it.order }
        CollectionPickerItem(collection = collection, members = members)
    }
}
