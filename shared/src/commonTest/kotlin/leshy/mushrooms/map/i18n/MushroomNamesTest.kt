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

/** Every file under `composeResources/files/catalog/names/` (`docs/catalog/CLAUDE.md`: "36 files
 * written"). Hardcoded because the `files/` typed folder has no generated listing API, unlike
 * `drawable/`'s `allDrawableResources` — and [AppLanguage] itself only carries `ru`/`en` until
 * Phase 4, so [MushroomNames] alone can't be asked for the other 34. */
private val ALL_NAME_LANGUAGE_CODES = listOf(
    "be", "bg", "ca", "cs", "de", "en", "es", "et", "eu", "fi", "fr", "gl", "hch", "hr", "hu",
    "it", "ja", "ka", "ko", "lt", "lv", "maa", "mi", "nah", "pl", "ro", "ru", "sk", "sl",
    "sr-Cyrl", "sr", "sv", "tr", "tsz", "tzo", "uk",
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
