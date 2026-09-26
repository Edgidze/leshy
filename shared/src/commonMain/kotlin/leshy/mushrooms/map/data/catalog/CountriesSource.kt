package leshy.mushrooms.map.data.catalog

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import leshy.shared.generated.resources.Res

private const val COUNTRIES_PATH = "files/catalog/countries.json"

private val CountriesJson = Json { ignoreUnknownKeys = true }

/** Mirrors one entry of `composeResources/files/catalog/countries.json` — see
 * `.claude/plans/countries-and-languages.md` §3, Phase 3 for the shape and how it's generated. */
@Serializable
data class CountryEntry(
    val code: String,
    val langs: List<String>,
    val keys: List<String>,
    /**
     * Виды, которые в этой стране реально встречаются часто (роль `common_encounter` в дампе) —
     * отсюда берётся «вперёд частотные» в порядке ленты по умолчанию.
     *
     * **`null`, а не пустой список, у стран без этих данных** — это 22 подборки партий
     * post-soviet и europe-15, собранные исследованиями, у которых ролей в источнике нет.
     * Разница существенная: пустой список означал бы «здесь ничего не часто», а `null` —
     * «данных нет», и порядок для такой страны падает на глобальный `CatalogEntry.importance`
     * вместо того, чтобы уводить всю подборку в один ряд.
     */
    val common: List<String>? = null,
)

/** Prefix all per-country [leshy.mushrooms.map.domain.model.Collection.nameKey]s share — the only
 * place this string is spelled out, everything else (seeding, display-name resolution) derives from
 * it or from [CountryEntry.code]. */
private const val COUNTRY_COLLECTION_PREFIX = "collection_country_"

fun countryCollectionNameKey(countryCode: String): String = COUNTRY_COLLECTION_PREFIX + countryCode

/** `null` unless [nameKey] is a per-country collection — i.e. it isn't one of the removed
 * `collection_demo_*` rows or something else entirely. */
fun countryCodeForCollectionNameKey(nameKey: String): String? =
    nameKey.takeIf { it.startsWith(COUNTRY_COLLECTION_PREFIX) }?.removePrefix(COUNTRY_COLLECTION_PREFIX)

/**
 * Parses `countries.json` (55 entries, ~110 KB) once and caches the result — Koin singleton
 * (`di/DataModule.kt`), same shape and reasoning as [CatalogSource].
 *
 * **Редакции этот класс больше не различает.** До 2026-09-26 у записи `RU` лежало отдельное поле
 * `extendedKeys` (171 вид вместо 54), которое подставлялось вместо [CountryEntry.keys] в редакции,
 * сделанной под эту страну. Теперь ту же роль играют наборы ([SpeciesSetsSource]): подборка «своей»
 * страны у такой редакции не заводится вовсе, а 171 вид разложен по базовому набору и дополнениям.
 * Двух механизмов на одну задачу не нужно, поэтому расширение убрано и из данных.
 */
class CountriesSource {
    private class Parsed(val entries: List<CountryEntry>, val version: Int)

    private val parsed: Parsed by lazy {
        val bytes = runBlocking { Res.readBytes(COUNTRIES_PATH) }
        val entries: List<CountryEntry> = CountriesJson.decodeFromString(bytes.decodeToString())
        Parsed(entries, bytes.contentHashCode())
    }

    val entries: List<CountryEntry> get() = parsed.entries

    /** Fingerprint of the bundled `countries.json`, used to gate `EnsureDefaultCollectionsUseCase`'s
     * reseeding diff — same reasoning as [CatalogSource.version]. */
    val version: Int get() = parsed.version

    /**
     * Объединение списков [CountryEntry.common] по кодам [countryCodes] — виды, частотные хотя бы
     * в одной из выбранных пользователем стран.
     *
     * Объединение, а не пересечение: человек, отметивший Россию и Финляндию, ходит и там, и там, и
     * вид, обычный хотя бы в одной из них, ему нужен под рукой. Страны без страновых данных
     * (`common == null`) в объединение просто ничего не вносят — их виды упорядочатся по
     * глобальному `importance`, см. `sortCategories`.
     */
    fun commonKeysFor(countryCodes: Set<String>): Set<String> =
        entries.asSequence()
            .filter { it.code in countryCodes }
            .flatMap { it.common.orEmpty().asSequence() }
            .toSet()
}
