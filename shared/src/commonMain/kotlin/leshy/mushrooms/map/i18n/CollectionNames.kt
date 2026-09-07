package leshy.mushrooms.map.i18n

import androidx.compose.runtime.Composable
import leshy.mushrooms.map.data.catalog.CountriesSource
import leshy.mushrooms.map.data.catalog.countryCodeForCollectionNameKey
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Collection
import leshy.mushrooms.map.domain.usecase.OTHER_COLLECTION_NAME_KEY
import org.koin.mp.KoinPlatform.getKoin

/**
 * Resolves a [Collection] to a localized display name. Three kinds of row, three sources of text:
 *
 * - per-country presets can't go through `StringKey` (same reasoning as catalog species names,
 *   `.claude/plans/countries-and-languages.md` §3.1) — the name comes from [CountryNames], falling
 *   back to English, then to the bare ISO code as a last resort (mirrors `categoryDisplayName`'s
 *   `MushroomNames` → `CatalogSource.scientificName` → `nameKey` chain);
 * - the service "Other" collection is a fixed key and DOES go through `StringKey`, so it follows
 *   the interface language like any other piece of UI;
 * - a named user collection shows the text the user typed, as-is in every language — it's user
 *   input in one particular language, not an interface string (`.claude/plans/user-collections.md`).
 *
 * [CountryNames] is resolved via [getKoin] rather than a constructor parameter for the same reason
 * `categoryDisplayName` does — this is called from a plain composable/pure-function context with no
 * DI plumbing, see `i18n/CLAUDE.md`.
 */
@Composable
fun collectionDisplayName(collection: Collection): String =
    collectionDisplayName(collection, LocalAppLanguage.current)

/** Non-composable counterpart of [collectionDisplayName]. */
fun collectionDisplayName(collection: Collection, language: AppLanguage): String {
    collection.name?.let { return it }
    if (collection.nameKey == OTHER_COLLECTION_NAME_KEY) return string(StringKey.CollectionOtherName, language)
    val countryCode = countryCodeForCollectionNameKey(collection.nameKey) ?: return collection.nameKey
    val countryNames = getKoin().get<CountryNames>()
    return countryNames.namesFor(language)[countryCode]
        ?: countryNames.namesFor(AppLanguage.EN)[countryCode]
        ?: countryCode
}

/**
 * `true` if [input] is the name of one of the bundled country presets, in [language] or in English
 * — those two and no more (`.claude/plans/user-collections.md`). Checking all 42 languages would
 * forbid a Russian-speaking user from naming a collection `Norge`, with no way to explain the
 * refusal on screen; checking only the current language would let the same collision appear later
 * by switching the interface language, which is why English is in regardless.
 *
 * Only the countries actually bundled count: [CountryNames] carries names for far more codes than
 * `countries.json` has presets, and forbidding a name that collides with nothing visible in the app
 * would be a refusal the user can't act on.
 */
fun collidesWithCountryName(input: String, language: AppLanguage): Boolean {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return false
    val bundledCodes = getKoin().get<CountriesSource>().entries.map { it.code }.toSet()
    val countryNames = getKoin().get<CountryNames>()
    return listOf(language, AppLanguage.EN).any { candidate ->
        countryNames.namesFor(candidate).any { (code, name) ->
            code in bundledCodes && name.equals(trimmed, ignoreCase = true)
        }
    }
}

/** `true` if [input] is just the localized name of the "Other" collection typed out by hand — it
 * must land in the service collection itself rather than creating a look-alike beside it. English
 * is checked alongside [language] for the same reason [collidesWithCountryName] checks it. */
fun isOtherCollectionName(input: String, language: AppLanguage): Boolean {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return false
    return listOf(language, AppLanguage.EN).any {
        string(StringKey.CollectionOtherName, it).equals(trimmed, ignoreCase = true)
    }
}
