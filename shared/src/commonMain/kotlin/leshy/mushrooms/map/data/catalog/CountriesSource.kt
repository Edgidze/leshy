package leshy.mushrooms.map.data.catalog

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import leshy.mushrooms.map.domain.model.Edition
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

    /**
     * Расширенный набор — то, чем подборка страны заменяется в редакции, сделанной под эту
     * страну. Есть ровно у одной записи, `RU`: 171 вид против 54, и все 117 добавленных уже
     * имеют русские названия (в этом и критерий отбора — вид без имени на языке продукта
     * показался бы латынью).
     *
     * **Лежит отдельным полем, а не заменяет [keys], потому что файл общий.**
     * `composeResources` не знает о флейворах: `countries.json` попадает в обе сборки целиком.
     * Перепиши мы [keys] — подборка «Россия» выросла бы втрое и у мирового «Лешего», то есть у
     * людей, которые об этом не просили. Разрешает поле [CountriesSource], и только для своей
     * редакции.
     *
     * `null` у остальных 54 стран — и это не пустота, а «расширять нечего»: у них набор один.
     */
    val extendedKeys: List<String>? = null,
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
 * **[edition] решает здесь ровно одно: какой набор видов у подборки страны.** Редакция, сделанная
 * под конкретную страну, получает у неё [CountryEntry.extendedKeys], если те есть; все остальные
 * страны и все остальные редакции — обычные [CountryEntry.keys]. Подмена делается один раз, при
 * разборе, поэтому ниже по коду ветвления по редакции нет вообще: `EnsureDefaultCollectionsUseCase`
 * видит просто список ключей.
 */
class CountriesSource(private val edition: Edition) {
    private class Parsed(val entries: List<CountryEntry>, val version: Int)

    private val parsed: Parsed by lazy {
        val bytes = runBlocking { Res.readBytes(COUNTRIES_PATH) }
        val entries: List<CountryEntry> = CountriesJson.decodeFromString(bytes.decodeToString())
        Parsed(entries.map { withEditionKeys(it) }, bytes.contentHashCode())
    }

    /**
     * Расширенный набор берётся, только если страна записи — «своя» для редакции. Отсюда и
     * сравнение с кодом страны, а не просто «есть extendedKeys — взять»: российская редакция не
     * должна раздувать подборку Финляндии, даже если у той однажды появится своё расширение.
     */
    private fun withEditionKeys(entry: CountryEntry): CountryEntry {
        val extended = entry.extendedKeys?.takeIf { entry.code == edition.homeCountryCode }
            ?: return entry
        return entry.copy(keys = extended)
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
