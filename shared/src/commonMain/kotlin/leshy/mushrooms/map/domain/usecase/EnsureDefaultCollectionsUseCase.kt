package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.catalog.CountriesSource
import leshy.mushrooms.map.data.catalog.SpeciesSetsSource
import leshy.mushrooms.map.data.catalog.countryCodeForCollectionNameKey
import leshy.mushrooms.map.data.catalog.countryCollectionNameKey
import leshy.mushrooms.map.data.catalog.speciesSetCollectionNameKey
import leshy.mushrooms.map.data.catalog.speciesSetIdForCollectionNameKey
import leshy.mushrooms.map.domain.model.CategoryCollectionMembership
import leshy.mushrooms.map.domain.model.Collection
import leshy.mushrooms.map.domain.model.CollectionSource
import leshy.mushrooms.map.domain.repository.CatalogStateRepository
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository

/**
 * Reconciles the `collections` table with the bundled per-country presets (`countries.json`, 45
 * countries — `.claude/plans/countries-and-languages.md`, Phase 3). Replaces the old hardcoded
 * 3-bucket demo seeding; same batch/gate shape as `EnsureDefaultCategoriesUseCase`.
 *
 * **У редакции со своими наборами ([SpeciesSetsSource]) страновых подборок не заводится ВОВСЕ —
 * ни своей страны, ни чужих.** Своей — потому что две сущности с одним и тем же содержимым (одна
 * «Россия» на 171 вид рядом с базовым набором и дополнениями, разбирающими те же 171) означали бы
 * два способа отметить одно и то же и вопрос «а чем они различаются» на экране, где различаться
 * нечему. Чужих — решение владельца от 2026-09-26: «карта России» не обещает ни карты, ни данных
 * за пределами страны (тайлы лежат на своём сервере и других стран на нём не ожидается), а
 * пятьдесят четыре страновые галочки под одиннадцатью наборами читались как обещание, которого
 * продукт не даёт. Мировой редакции это не касается: там наборов нет, и страны заводятся все.
 *
 * Строки страновых подборок, оставшиеся от прежней сборки, удаляются — вместе со своими
 * членствами; `Category.isPicked` при этом не трогается, то есть уже отмеченные виды остаются
 * отмеченными (человек, отметивший что-то через страну, своих грибов не лишается).
 *
 * Membership is always re-inserted in full rather than diffed against what's already there —
 * `CollectionDao.insertMembers` uses `OnConflictStrategy.IGNORE`, so handing it the complete desired
 * list every time this runs is idempotent by construction and cheap (~2240 rows, one transaction),
 * without needing to compute a per-row diff. A country losing a species between catalog
 * regenerations does *not* remove the stale membership row — same additive-only philosophy as
 * `EnsureDefaultCategoriesUseCase`, which never deletes rows either.
 */
class EnsureDefaultCollectionsUseCase(
    private val collectionRepository: CollectionRepository,
    private val categoryRepository: CategoryRepository,
    private val countriesSource: CountriesSource,
    private val speciesSetsSource: SpeciesSetsSource,
    private val catalogStateRepository: CatalogStateRepository,
) {
    suspend operator fun invoke() {
        val sets = speciesSetsSource.sets
        // Редакция со своими наборами: страны не заводятся ни одна — ни из посева, ни из членств
        // ниже (см. KDoc класса). Сам `countries.json` при этом продолжает читаться — из него
        // берётся частотность видов для ленты (`ObserveSpeciesPriorityUseCase`), это независимая
        // от подборок работа.
        val countries = if (sets.isEmpty()) countriesSource.entries else emptyList()

        // Fast path — see EnsureDefaultCategoriesUseCase for why the row-count check matters too.
        // Считаются именно страновые строки: с появлением пользовательских подборок
        // (`.claude/plans/user-collections.md`) общий COUNT(*) перестал означать «все пресеты на
        // месте» — десяток своих подборок перекрыл бы недостачу стран, и вторая половина гейта
        // молча перестала бы работать.
        // Версия — отпечаток обоих файлов разом: наборы живут в своём, и правка только его
        // обязана вызывать пересев так же, как правка `countries.json`.
        val seededVersion = countriesSource.version * 31 + speciesSetsSource.version
        // Сверка числа строк у редакции с наборами — на РАВЕНСТВО, а не «не меньше»: лишние
        // страновые строки от прежней сборки обязаны провалить гейт, иначе удалять их будет некому
        // (отпечаток файлов от этого решения не меняется, и быстрый путь замкнул бы их навсегда).
        // У мировой редакции остаётся «не меньше»: выпавшая из `countries.json` страна оставляет
        // свою строку жить — тот же аддитивный принцип, что у членств ниже, — и равенство
        // заставляло бы пересевать всё на каждом запуске.
        val countryRows = collectionRepository.countBySource(CollectionSource.COUNTRY)
        val rowsSettled =
            if (sets.isEmpty()) countryRows >= countries.size else countryRows == sets.size
        if (catalogStateRepository.getSeededCountriesVersion() == seededVersion && rowsSettled) {
            return
        }

        // Наборы идут ПЕРЕД странами: это подборки продукта, а страны рядом с ними — справочный
        // хвост. Порядок строк — порядок галочек на экране.
        val desired = sets.mapIndexed { index, set ->
            Collection(
                id = 0,
                nameKey = speciesSetCollectionNameKey(set.id),
                order = index,
                source = CollectionSource.COUNTRY,
            )
        } + countries.mapIndexed { index, country ->
            Collection(
                id = 0,
                nameKey = countryCollectionNameKey(country.code),
                order = sets.size + index,
                source = CollectionSource.COUNTRY,
            )
        }
        val existingByNameKey = collectionRepository.getAll().associateBy { it.nameKey }
        val pending = desired.mapNotNull { canonical ->
            val existing = existingByNameKey[canonical.nameKey]
            when {
                existing == null -> canonical
                existing.order == canonical.order -> null
                else -> canonical.copy(id = existing.id)
            }
        }
        if (pending.isNotEmpty()) collectionRepository.upsertAll(pending)

        val collectionIdByCode = collectionRepository.getAll()
            .mapNotNull { collection -> countryCodeForCollectionNameKey(collection.nameKey)?.let { it to collection.id } }
            .toMap()
        val categoryIdByKey = categoryRepository.getAll().associateBy { it.nameKey }.mapValues { it.value.id }
        val collectionIdBySetId = collectionRepository.getAll()
            .mapNotNull { collection -> speciesSetIdForCollectionNameKey(collection.nameKey)?.let { it to collection.id } }
            .toMap()
        val memberships = countries.flatMap { country ->
            val collectionId = collectionIdByCode[country.code] ?: return@flatMap emptyList()
            country.keys.mapNotNull { key ->
                categoryIdByKey[key]?.let { categoryId ->
                    CategoryCollectionMembership(categoryId = categoryId, collectionId = collectionId)
                }
            }
        } + sets.flatMap { set ->
            val collectionId = collectionIdBySetId[set.id] ?: return@flatMap emptyList()
            set.keys.mapNotNull { key ->
                categoryIdByKey[key]?.let { categoryId ->
                    CategoryCollectionMembership(categoryId = categoryId, collectionId = collectionId)
                }
            }
        }
        if (memberships.isNotEmpty()) collectionRepository.addMembers(memberships)

        // Страновые подборки могли остаться от прежней сборки — тогда на экране стояли бы и они, и
        // наборы, разбирающие ту же самую страну. Удаляются по разбору ключа, а не по источнику:
        // `CollectionSource.COUNTRY` носят и наборы (они тоже подборки продукта, а не
        // пользователя), а вот `country_`-ключ есть только у страны.
        if (sets.isNotEmpty()) {
            collectionRepository.getAll()
                .filter { countryCodeForCollectionNameKey(it.nameKey) != null }
                .forEach { collectionRepository.delete(it) }
        }

        catalogStateRepository.setSeededCountriesVersion(seededVersion)
    }
}
