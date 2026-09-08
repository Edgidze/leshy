package leshy.mushrooms.map.presentation

import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.CategoryCollectionMembership
import leshy.mushrooms.map.domain.model.Collection
import leshy.mushrooms.map.i18n.collectionDisplayName

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

/**
 * Joins the raw collections/categories/membership flows into picker-ready items, ordered
 * alphabetically by the name the user actually reads — [collectionDisplayName] in [language] —
 * with members ordered like [Category.order].
 *
 * **Не по [Collection.order].** Тот порядок — порядок сидирования из `countries.json`, то есть
 * алфавит ISO-КОДОВ: в любом языке интерфейса он выглядит случайным («Албания, Армения, Австрия,
 * Австралия, Азербайджан…» — это `AL, AM, AT, AU, AZ`). Пятьдесят с лишним стран в списке, который
 * не сортирован ни по чему видимому, глазами не просматриваются вовсе. Сортировка по имени
 * пересчитывается при смене языка интерфейса вместе с самими именами.
 *
 * Сравнение — по [lowercase] строке, без учёта регистра: имена приходят из CLDR с единообразной
 * прописной буквы, но регистр в сравнении не несёт здесь никакого смысла, и завязываться на него
 * не на что. Полноценной локализованной коллации (ICU) в общем коде нет; посимвольный порядок
 * Unicode совпадает с алфавитным для латиницы и кириллицы и расходится с ним лишь на диакритике
 * (шведские `å/ä/ö` уезжают за `z`, а не встают в конец своего алфавита) — тот же компромисс, на
 * котором уже стоит [sortCategories] для имён грибов.
 */
fun buildCollectionPickerItems(
    collections: List<Collection>,
    categories: List<Category>,
    memberships: List<CategoryCollectionMembership>,
    language: AppLanguage,
): List<CollectionPickerItem> {
    val categoriesById = categories.associateBy { it.id }
    val memberIdsByCollection = memberships.groupBy({ it.collectionId }, { it.categoryId })
    return collections
        .sortedBy { collectionDisplayName(it, language).lowercase() }
        .map { collection ->
            val members = memberIdsByCollection[collection.id].orEmpty()
                .mapNotNull { categoriesById[it] }
                .sortedBy { it.order }
            CollectionPickerItem(collection = collection, members = members)
        }
}
