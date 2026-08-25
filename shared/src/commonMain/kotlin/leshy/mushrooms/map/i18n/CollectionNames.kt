package leshy.mushrooms.map.i18n

import androidx.compose.runtime.Composable
import leshy.mushrooms.map.data.catalog.countryCodeForCollectionNameKey
import leshy.mushrooms.map.domain.model.AppLanguage
import org.koin.mp.KoinPlatform.getKoin

/**
 * Resolves a [leshy.mushrooms.map.domain.model.Collection.nameKey] to a localized display name.
 * 33 per-country collections can't go through `StringKey` (same reasoning as catalog species names,
 * `.claude/plans/countries-and-languages.md` §3.1) — the name comes from [CountryNames] instead,
 * falling back to English, then to the bare ISO code as a last resort (mirrors
 * `categoryDisplayName`'s `MushroomNames` → `CatalogSource.scientificName` → `nameKey` chain).
 *
 * [CountryNames] is resolved via [getKoin] rather than a constructor parameter for the same reason
 * `categoryDisplayName` does — this is called from a plain composable/pure-function context with no
 * DI plumbing, see `i18n/CLAUDE.md`.
 */
@Composable
fun collectionDisplayName(nameKey: String): String = collectionDisplayName(nameKey, LocalAppLanguage.current)

/** Non-composable counterpart of [collectionDisplayName]. */
fun collectionDisplayName(nameKey: String, language: AppLanguage): String {
    val countryCode = countryCodeForCollectionNameKey(nameKey) ?: return nameKey
    val countryNames = getKoin().get<CountryNames>()
    return countryNames.namesFor(language)[countryCode]
        ?: countryNames.namesFor(AppLanguage.EN)[countryCode]
        ?: countryCode
}
