package compose.project.leshy.data.catalog

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
)

/** Prefix all per-country [compose.project.leshy.domain.model.Collection.nameKey]s share — the only
 * place this string is spelled out, everything else (seeding, display-name resolution) derives from
 * it or from [CountryEntry.code]. */
private const val COUNTRY_COLLECTION_PREFIX = "collection_country_"

fun countryCollectionNameKey(countryCode: String): String = COUNTRY_COLLECTION_PREFIX + countryCode

/** `null` unless [nameKey] is a per-country collection — i.e. it isn't one of the removed
 * `collection_demo_*` rows or something else entirely. */
fun countryCodeForCollectionNameKey(nameKey: String): String? =
    nameKey.takeIf { it.startsWith(COUNTRY_COLLECTION_PREFIX) }?.removePrefix(COUNTRY_COLLECTION_PREFIX)

/**
 * Parses `countries.json` (33 entries, ~40 KB) once and caches the result — Koin singleton
 * (`di/DataModule.kt`), same shape and reasoning as [CatalogSource].
 */
class CountriesSource {
    private class Parsed(val entries: List<CountryEntry>, val version: Int)

    private val parsed: Parsed by lazy {
        val bytes = runBlocking { Res.readBytes(COUNTRIES_PATH) }
        Parsed(CountriesJson.decodeFromString(bytes.decodeToString()), bytes.contentHashCode())
    }

    val entries: List<CountryEntry> get() = parsed.entries

    /** Fingerprint of the bundled `countries.json`, used to gate `EnsureDefaultCollectionsUseCase`'s
     * reseeding diff — same reasoning as [CatalogSource.version]. */
    val version: Int get() = parsed.version
}
