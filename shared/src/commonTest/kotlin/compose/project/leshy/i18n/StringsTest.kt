package compose.project.leshy.i18n

import compose.project.leshy.domain.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `uiTranslations` (`Strings.kt`) replaces the compiler's exhaustive-`when` completeness check for
 * every [AppLanguage] beyond `ru`/`en` (`.claude/plans/countries-and-languages.md` §3.2) — this is
 * that check, run at test time instead. As of Phase 4 `uiTranslations` is empty (translations land
 * in Phases 6–11), so [everyKeyFallsBackToEnglishForUntranslatedLanguages] is the only one that
 * currently exercises anything; [everyTranslationMapIsCompleteAndNonBlank] is written now so a
 * later phase adding an entry to `uiTranslations` gets the completeness check for free, without
 * having to remember to add a test alongside the translation.
 */
class StringsTest {
    @Test
    fun everyKeyFallsBackToEnglishForUntranslatedLanguages() {
        val untranslated = AppLanguage.entries.filter { it != AppLanguage.RU && it !in uiTranslations }
        untranslated.forEach { language ->
            StringKey.entries.forEach { key ->
                assertEquals(string(key, AppLanguage.EN), string(key, language), "$language/$key")
            }
        }
    }

    @Test
    fun everyTranslationMapIsCompleteAndNonBlank() {
        uiTranslations.forEach { (language, translations) ->
            val missing = StringKey.entries.toSet() - translations.keys
            assertTrue(missing.isEmpty(), "$language is missing translations for: $missing")
            translations.forEach { (key, value) -> assertTrue(value.isNotBlank(), "$language/$key is blank") }
        }
    }
}
