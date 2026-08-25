package compose.project.leshy.i18n

import compose.project.leshy.domain.model.AppLanguage
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import leshy.shared.generated.resources.Res

private val CountryNamesJson = Json { ignoreUnknownKeys = true }

/**
 * `composeResources/files/catalog/countries/<lang>.json` (`{countryCode: name}`) — mirrors
 * [MushroomNames] exactly (one file per language, loaded and cached lazily per [AppLanguage]).
 * Only `en`/`ru` exist as of Phase 3 (`.claude/plans/countries-and-languages.md` §3, "названия
 * стран пока только en/ru"), but [AppLanguage] grew to 26 values in Phase 4 — unlike
 * [MushroomNames] (which has a `names/<lang>.json` file for every current [AppLanguage]),
 * most languages now have no `countries/<lang>.json` on disk at all. [runCatching] turns that
 * missing-resource read into an empty map instead of a crash; [collectionDisplayName]'s
 * `namesFor(language) ?: namesFor(EN)` fallback chain is what actually resolves the name for those
 * languages.
 */
class CountryNames {
    private val cache = mutableMapOf<AppLanguage, Map<String, String>>()

    fun namesFor(language: AppLanguage): Map<String, String> =
        cache.getOrPut(language) {
            runCatching {
                runBlocking {
                    CountryNamesJson.decodeFromString<Map<String, String>>(
                        Res.readBytes("files/catalog/countries/${language.code}.json").decodeToString(),
                    )
                }
            }.getOrDefault(emptyMap())
        }
}
