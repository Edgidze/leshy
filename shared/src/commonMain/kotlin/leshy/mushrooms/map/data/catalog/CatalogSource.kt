package leshy.mushrooms.map.data.catalog

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import leshy.shared.generated.resources.Res

private const val CATALOG_PATH = "files/catalog/catalog.json"

private val CatalogJson = Json { ignoreUnknownKeys = true }

/** Mirrors one entry of `composeResources/files/catalog/catalog.json` — see `docs/catalog/CLAUDE.md`
 * for how it's generated and `.claude/plans/countries-and-languages.md` §1/§3.1 for the shape. */
@Serializable
data class CatalogEntry(
    val key: String,
    val sci: String,
    val image: String,
    val color: String,
    val breadth: String,
    val importance: Double,
    val dangerous: Boolean,
)

/**
 * Parses `catalog.json` (408 entries, ~60 KB) once and caches the result — registered as a Koin
 * singleton (`di/DomainModule.kt`), so the whole app shares one parsed copy. `Res.readBytes` is
 * suspend-only (bundled-resource I/O is async on both Android and iOS), so the first access blocks
 * its calling thread via [runBlocking]; the file is small enough for that first read to be
 * effectively instant, and every access after it is a plain in-memory map lookup.
 */
class CatalogSource {
    private class Parsed(val entries: List<CatalogEntry>, val version: Int)

    private val parsed: Parsed by lazy {
        val bytes = runBlocking { Res.readBytes(CATALOG_PATH) }
        Parsed(CatalogJson.decodeFromString(bytes.decodeToString()), bytes.contentHashCode())
    }

    val entries: List<CatalogEntry> get() = parsed.entries

    /**
     * Fingerprint of the bundled `catalog.json`, used to skip the (408-row) reseeding diff when
     * nothing has changed — see `EnsureDefaultCategoriesUseCase`. Derived from the file's bytes
     * rather than a hand-maintained constant precisely so it can't be forgotten: any rerun of
     * `tools/build_catalog.py` that changes the output invalidates it automatically.
     */
    val version: Int get() = parsed.version

    private val byKey: Map<String, CatalogEntry> by lazy { entries.associateBy { it.key } }

    /** The catalog's Latin name for [key], or `null` if [key] isn't a catalog entry (a service
     * key like `category_misc`, or — until Phase 2's Room migration — a pre-migration legacy
     * `category_*` key). */
    fun scientificName(key: String): String? = byKey[key]?.sci
}
