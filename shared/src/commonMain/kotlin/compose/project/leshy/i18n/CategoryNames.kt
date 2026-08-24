package compose.project.leshy.i18n

import androidx.compose.runtime.Composable
import compose.project.leshy.data.catalog.CatalogSource
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import org.koin.mp.KoinPlatform.getKoin

/**
 * Display name of [category] — the overload every UI call site should use. A catalog species
 * resolves through its `nameKey` as before; a user-created/imported one carries its own
 * [Category.customNames] instead, since its `nameKey` is a generated technical id
 * (`user_<millis>_<random>`) that no catalog key will ever match.
 */
@Composable
fun categoryDisplayName(category: Category): String =
    customDisplayName(category, LocalAppLanguage.current) ?: categoryDisplayName(category.nameKey)

/** Non-composable counterpart of [categoryDisplayName], for contexts like sorting in a ViewModel. */
fun categoryDisplayName(category: Category, language: AppLanguage): String =
    customDisplayName(category, language) ?: categoryDisplayName(category.nameKey, language)

/**
 * The user-entered name for [language], falling back across the other language and then the Latin
 * name: a species named in Russian only must still show *something* readable after switching the
 * app to English, and vice versa. Null means "nothing user-entered here", i.e. a catalog species.
 */
private fun customDisplayName(category: Category, language: AppLanguage): String? =
    category.customNames[language]?.takeIf { it.isNotBlank() }
        ?: category.customNames.values.firstOrNull { it.isNotBlank() }
        ?: category.scientificName?.takeIf { it.isNotBlank() }

/** Resolves a [compose.project.leshy.domain.model.Category.nameKey] to a localized display name. */
@Composable
fun categoryDisplayName(nameKey: String): String =
    categoryNameStringKey(nameKey)?.let { stringResource(it) } ?: catalogDisplayName(nameKey, LocalAppLanguage.current)

/** Non-composable counterpart of [categoryDisplayName], for contexts like sorting in a ViewModel. */
fun categoryDisplayName(nameKey: String, language: AppLanguage): String =
    categoryNameStringKey(nameKey)?.let { string(it, language) } ?: catalogDisplayName(nameKey, language)

private fun categoryNameStringKey(nameKey: String): StringKey? = when (nameKey) {
    "category_misc" -> StringKey.CategoryMisc
    "category_unknown_mushroom" -> StringKey.CategoryUnknownMushroom
    else -> null
}

/**
 * Catalog naming rule (`.claude/plans/countries-and-languages.md` §3.1): [language]'s name from
 * [MushroomNames], else the catalog's Latin name, else [nameKey] itself as a last resort — an id
 * nobody could translate. [MushroomNames]/[CatalogSource] are looked up through Koin directly
 * rather than threaded in as a parameter, since this is reached from plain (non-composable, non-DI)
 * call sites like [compose.project.leshy.presentation.CategorySorting] — Koin is always started by
 * the time any UI or ViewModel code runs (`di/InitKoin.kt`).
 */
private fun catalogDisplayName(nameKey: String, language: AppLanguage): String {
    val koin = getKoin()
    return koin.get<MushroomNames>().namesFor(language)[nameKey]
        ?: koin.get<CatalogSource>().scientificName(nameKey)
        ?: nameKey
}
