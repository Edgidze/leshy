package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.catalog.CountriesSource
import leshy.mushrooms.map.data.catalog.SpeciesSetsSource
import leshy.mushrooms.map.data.catalog.countryCodeForCollectionNameKey
import leshy.mushrooms.map.data.catalog.speciesSetIdForCollectionNameKey
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Ключи видов, частотных хотя бы в одной из выбранных пользователем стран, — вход для «вперёд
 * частотные» в [leshy.mushrooms.map.presentation.sortCategories].
 *
 * **Объединение по выбранным подборкам, а не по региону устройства** — решение владельца
 * 2026-09-17. Регион расходится с выбором: человек мог выбрать страну, в которую ездит, а телефон
 * при этом настроен на другую; а тот, кто выбрал три страны, ходит по всем трём.
 *
 * Подборка считается выбранной, если выбран хоть один её вид. Это то же определение, по которому
 * рисует себя чекбокс пикера ([leshy.mushrooms.map.presentation.CollectionPickerItem.pickState] —
 * `NONE` только когда не выбрано ничего), и другого определения быть не должно: список, который
 * человек видит отмеченным, и список, по которому считается частотность, обязаны совпадать.
 *
 * Читается из трёх потоков, потому что «выбранность» подборки в модели не хранится: она живёт на
 * [leshy.mushrooms.map.domain.model.Category.isPicked], а связь вида со страной — в membership.
 * Вывести её из одних лишь ключей `countries.json` нельзя: подборки соседних стран пересекаются на
 * 80% и больше, и выбор одной России пометил бы «выбранными» почти все европейские страны.
 *
 * [distinctUntilChanged] на выходе — не украшение: `categories` переизлучается на каждое изменение
 * любого вида (включая отметку находки), а множество частотных ключей меняется только когда
 * человек трогает пикер подборок. Без него каждая находка заново пересортировывала бы ленту.
 */
class ObserveFrequentSpeciesKeysUseCase(
    private val collectionRepository: CollectionRepository,
    private val categoryRepository: CategoryRepository,
    private val countriesSource: CountriesSource,
    private val speciesSetsSource: SpeciesSetsSource,
) {
    operator fun invoke(): Flow<Set<String>> = combine(
        collectionRepository.observeAll(),
        collectionRepository.observeAllMemberships(),
        categoryRepository.observeAll(),
    ) { collections, memberships, categories ->
        val pickedCategoryIds = categories.asSequence().filter { it.isPicked }.map { it.id }.toSet()
        val pickedCollectionIds = memberships.asSequence()
            .filter { it.categoryId in pickedCategoryIds }
            .map { it.collectionId }
            .toSet()
        // Набор редакции считается за подборку СВОЕЙ страны: наборы — это её подборка,
        // разрезанная на части, и частотность видов у них та же самая. Без этого редакция, где
        // страновой подборки больше нет, потеряла бы «вперёд частотные» в ленте целиком.
        val pickedCountryCodes = collections.asSequence()
            .filter { it.id in pickedCollectionIds }
            .mapNotNull { collection ->
                countryCodeForCollectionNameKey(collection.nameKey)
                    ?: speciesSetsSource.countryCode.takeIf {
                        speciesSetIdForCollectionNameKey(collection.nameKey) != null
                    }
            }
            .toSet()
        countriesSource.commonKeysFor(pickedCountryCodes)
    }.distinctUntilChanged()
}
