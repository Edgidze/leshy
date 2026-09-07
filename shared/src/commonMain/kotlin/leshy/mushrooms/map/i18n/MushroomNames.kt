package leshy.mushrooms.map.i18n

import leshy.mushrooms.map.domain.model.AppLanguage
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import leshy.shared.generated.resources.Res

private val NamesJson = Json { ignoreUnknownKeys = true }

/**
 * `composeResources/files/catalog/names/<lang>.json` (`{key: name}`) — one file per language,
 * loaded and cached lazily per [AppLanguage] the first time it's asked for, so switching the app's
 * language loads only the newly active language's file instead of parsing every language upfront
 * (`.claude/plans/countries-and-languages.md` §3.1). Registered as a Koin singleton
 * (`di/DomainModule.kt`), same reasoning as [leshy.mushrooms.map.data.catalog.CatalogSource].
 *
 * [runCatching] mirrors [CountryNames]: a language whose file isn't on disk yet reads as an empty
 * map, and every species falls back to its Latin name through [catalogDisplayName]. That is not
 * hypothetical — Phase 4 of `.claude/plans/europe-15-countries.md` adds nine [AppLanguage] values
 * whose `names/<lang>.json` only arrives with the research of Phase 2, and without this the first
 * tile drawn after switching to Danish would crash on a missing resource instead.
 */
class MushroomNames {
    private val cache = mutableMapOf<AppLanguage, Map<String, String>>()

    fun namesFor(language: AppLanguage): Map<String, String> =
        cache.getOrPut(language) {
            runCatching {
                runBlocking {
                    NamesJson.decodeFromString<Map<String, String>>(
                        Res.readBytes("files/catalog/names/${language.code}.json").decodeToString(),
                    )
                }
            }.getOrDefault(emptyMap())
        }
}
