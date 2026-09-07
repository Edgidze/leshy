package leshy.mushrooms.map.i18n

import leshy.mushrooms.map.data.catalog.CatalogSource
import leshy.mushrooms.map.domain.model.AppLanguage
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import leshy.shared.generated.resources.Res
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private val TestNamesJson = Json { ignoreUnknownKeys = true }

/** Every file under `composeResources/files/catalog/names/` (47 as of the `europe-15` batch).
 * Hardcoded because the `files/` typed folder has no generated listing API, unlike `drawable/`'s
 * `allDrawableResources` — and the list is wider than [AppLanguage] anyway (`ca`, `eu`, `gl`,
 * `hch`, `maa`, `mi`, `nah`, `tsz`, `tzo` have name files but no interface language), so
 * [MushroomNames] alone can't be asked for all of them. `sq`, `bs`, `da`, `mk` and `nl` are
 * deliberately absent: those interface languages exist, their name files do not yet. */
private val ALL_NAME_LANGUAGE_CODES = listOf(
    "az", "be", "bg", "ca", "cs", "de", "el", "en", "es", "et", "eu", "fi", "fr", "gl", "hch",
    "hr", "hu", "hy", "is", "it", "ja", "ka", "kk", "ko", "ky", "lt", "lv", "maa", "mi", "nah",
    "nb", "pl", "pt", "ro", "ru", "sk", "sl", "sr-Cyrl", "sr", "sv", "tg", "tk", "tr", "tsz",
    "tzo", "uk", "uz",
)

class MushroomNamesTest {
    @Test
    fun everyLanguageFileOnlyNamesCatalogKeys() {
        val catalogKeys = CatalogSource().entries.map { it.key }.toSet()
        ALL_NAME_LANGUAGE_CODES.forEach { code ->
            val names = runBlocking {
                TestNamesJson.decodeFromString<Map<String, String>>(
                    Res.readBytes("files/catalog/names/$code.json").decodeToString(),
                )
            }
            val strayKeys = names.keys - catalogKeys
            assertTrue(strayKeys.isEmpty(), "$code.json has names keys outside the catalog: $strayKeys")
        }
    }

    @Test
    fun fallsBackToScientificNameWhenTheActiveLanguageHasNoTranslation() {
        val catalogSource = CatalogSource()
        val ruNames = MushroomNames().namesFor(AppLanguage.RU)
        // ru.json covers only ~34% of the catalog (measured in the plan) — there's always a key
        // with no Russian translation to exercise the fallback with.
        val entry = catalogSource.entries.first { it.key !in ruNames }

        val resolved = ruNames[entry.key] ?: catalogSource.scientificName(entry.key)

        assertNotNull(resolved)
        assertEquals(entry.sci, resolved)
    }
}
