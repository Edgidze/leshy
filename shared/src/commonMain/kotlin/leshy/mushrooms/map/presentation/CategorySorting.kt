package leshy.mushrooms.map.presentation

import leshy.mushrooms.map.data.catalog.CatalogEntry
import leshy.mushrooms.map.data.catalog.CatalogSource
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.CategorySource
import leshy.mushrooms.map.domain.usecase.SpeciesPriority
import leshy.mushrooms.map.i18n.categoryDisplayName
import leshy.mushrooms.map.i18n.categorySearchLabel
import leshy.mushrooms.map.i18n.hasLocalizedName
import org.koin.mp.KoinPlatform.getKoin

/**
 * Порядок ленты видов по умолчанию — общий для ленты плиток «Записи»
 * ([leshy.mushrooms.map.presentation.record.RecordViewModel]) и списка видов в диалоге фильтра
 * ([leshy.mushrooms.map.presentation.mapfilter.MapFilterViewModel]): оба обязаны показывать одно и
 * то же, иначе человек ищет вид в двух разных списках по двум разным правилам.
 *
 * Был чистый алфавит, и тестировщики попросили другого (2026-09-17): впереди оказывались поганки,
 * которые большинству не интересны, а частотные съедобные приходилось выискивать. Три уровня, в
 * порядке убывания силы:
 *
 * 1. **Виды без настоящего имени в [language] — в самый конец**, отдельной группой (внешний
 *    `partition`). Это те, у кого показывается латынь (см. [hasLocalizedName]); требование
 *    владельца, и оно старше остальных двух. Внутри группы — свой алфавит, остальные правила к
 *    ней не применяются вовсе: сравнивать латинские имена по частотности бессмысленно, человек
 *    всё равно читает их как один нерасшифрованный хвост.
 * 2. **[CatalogEntry.sortLast] — в конец своей группы.** Поле намеренно называется нейтрально и в
 *    данных тоже: приложение не определяет съедобность (см. его KDoc), флаг означает ровно
 *    «по умолчанию ниже».
 * 3. **Самые узнаваемые — впереди частотных**, в своём порядке
 *    ([SpeciesPriority.flagship], три-пять видов на подборку). Заведено потому, что одной
 *    частотности мало: у России частотных двадцать, и первыми по алфавиту в ленте оказывались
 *    дубовик и ежовик — частые, но незнакомые (замечание владельца 2026-09-26). Порядок здесь
 *    НЕ алфавитный: список короткий и выбран руками, и в нём есть смысл.
 * 4. **Частотные — вперёд** остальных, внутри обеих групп. Частотность страновая
 *    ([SpeciesPriority.frequent] — объединение по выбранным подборкам, см.
 *    [leshy.mushrooms.map.domain.usecase.ObserveSpeciesPriorityUseCase]); где страновых
 *    данных нет, работает глобальный `importance` каталога, и вид просто не получает
 *    странового приоритета.
 * 5. Внутри всего равного — по-прежнему алфавит.
 */
fun sortCategories(
    categories: List<Category>,
    language: AppLanguage,
    priority: SpeciesPriority = SpeciesPriority.None,
): List<Category> {
    val (named, fallback) = categories.partition { hasLocalizedName(it, language) }
    return named
        .map { it to defaultOrderKeyOf(it, language, priority) }
        .sortedWith(compareBy(DEFAULT_ORDER) { it.second })
        .map { it.first } +
        fallback.sortedBy { categoryDisplayName(it, language) }
}

/**
 * То, по чему вид занимает своё место в ленте, — вынесено отдельным типом, чтобы порядок можно
 * было проверить тестом. Сама выемка значений ([defaultOrderKeyOf]) ходит в Koin за каталогом и на
 * хосте не выполняется (`Res.readBytes` требует нативной цели, см. корневой `CLAUDE.md`), а вот
 * правило сравнения — чистое, и ошибиться в нём куда легче, чем в трёх однострочных lookup'ах.
 */
internal data class SpeciesOrderKey(
    val sortsLast: Boolean,
    /** Место в списке самых узнаваемых; [Int.MAX_VALUE] — вида в списке нет. */
    val flagshipRank: Int,
    val frequencyScore: Double,
    val displayName: String,
)

/** Правило из KDoc [sortCategories], пункты 2–4. */
internal val DEFAULT_ORDER: Comparator<SpeciesOrderKey> =
    compareBy<SpeciesOrderKey> { if (it.sortsLast) 1 else 0 }
        .thenBy { it.flagshipRank }
        .thenByDescending { it.frequencyScore }
        .thenBy { it.displayName }

private fun defaultOrderKeyOf(
    category: Category,
    language: AppLanguage,
    priority: SpeciesPriority,
): SpeciesOrderKey = SpeciesOrderKey(
    sortsLast = catalogSortsLast(category),
    // Только каталожные виды: свой вид пользователь завёл сам, и в списке «узнаваемых»,
    // собранном для страны, его быть не может по построению.
    flagshipRank = priority.flagship.indexOf(category.nameKey).takeIf { it >= 0 } ?: Int.MAX_VALUE,
    frequencyScore = frequencyScore(category, priority.frequent),
    displayName = categoryDisplayName(category, language),
)

/** Пользовательские виды в каталоге не значатся и в хвост не уезжают — человек завёл их сам. */
private fun catalogSortsLast(category: Category): Boolean =
    category.source == CategorySource.APP && getKoin().get<CatalogSource>().sortsLast(category.nameKey)

/**
 * Насколько вид «важен» для этого пользователя, больше — выше.
 *
 * Страновая частотность весит больше любой глобальной значимости ([COUNTRY_FREQUENCY_BONUS]
 * заведомо выше максимального `importance` = 5), потому что отвечает на более узкий и более
 * близкий человеку вопрос: не «важный ли это гриб вообще», а «встретится ли он мне здесь».
 * Внутри частотных порядок всё равно задаёт `importance` — бонус общий, он группы не
 * перемешивает.
 *
 * **Пользовательские виды идут наравне с частотными.** Их завели руками, ради них специально
 * открывали форму — это сильнее любого признака из каталога. Каталожный вид без данных получает
 * ноль и оседает ниже частотных, но выше хвоста с латынью.
 */
private fun frequencyScore(category: Category, frequentKeys: Set<String>): Double {
    if (category.source != CategorySource.APP) return COUNTRY_FREQUENCY_BONUS
    val importance = getKoin().get<CatalogSource>().importance(category.nameKey) ?: 0.0
    return if (category.nameKey in frequentKeys) COUNTRY_FREQUENCY_BONUS + importance else importance
}

/** Заведомо больше максимального `importance` (5.0) — см. [frequencyScore]. */
private const val COUNTRY_FREQUENCY_BONUS = 100.0

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
 * keyed by [categorySearchLabel]. See [searchOrdered]'s doc for the ranking itself.
 *
 * Ключ — НЕ `categoryDisplayName`, как было до 2026-09-17, а имя вместе с народными синонимами:
 * подосиновик обязан находиться по «красному», подберёзовик по «обабку». Почему при этом ничего не
 * пришлось менять в самой ранжировке — см. KDoc [categorySearchLabel].
 */
fun searchOrderedCategories(categories: List<Category>, query: String, language: AppLanguage): List<Category> =
    searchOrdered(categories, query) { categorySearchLabel(it, language) }

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
