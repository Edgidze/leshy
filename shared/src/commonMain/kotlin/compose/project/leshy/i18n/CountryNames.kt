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
 * стран пока только en/ru") — same as [MushroomNames], every current [AppLanguage] value has a
 * matching file, so there's no fallback to build here yet; Phase 4's 24 new languages will need one
 * when they land.
 */
class CountryNames {
    private val cache = mutableMapOf<AppLanguage, Map<String, String>>()

    fun namesFor(language: AppLanguage): Map<String, String> =
        cache.getOrPut(language) {
            runBlocking {
                CountryNamesJson.decodeFromString<Map<String, String>>(
                    Res.readBytes("files/catalog/countries/${language.code}.json").decodeToString(),
                )
            }
        }
}
