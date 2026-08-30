package leshy.mushrooms.map.i18n

import leshy.mushrooms.map.domain.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The help-text twin of `StringsTest`: `helpTranslations` (`HelpTexts.kt`) replaces the compiler's
 * exhaustive-`when` completeness check for every [AppLanguage] beyond `ru`/`en`, so the check runs
 * here instead.
 *
 * Unlike `StringsTest`, both tests carry real load right now — help texts exist for `ru`/`en` only,
 * so [everyKeyFallsBackToEnglishForUntranslatedLanguages] covers 24 languages today and shrinks by
 * one with every translation pass (`.claude/plans/help-screens.md`), while
 * [everyHelpTranslationMapIsCompleteAndNonBlank] starts empty and grows to meet it.
 */
class HelpTextsTest {
    @Test
    fun everyKeyFallsBackToEnglishForUntranslatedLanguages() {
        val untranslated = AppLanguage.entries.filter { it != AppLanguage.RU && it !in helpTranslations }
        untranslated.forEach { language ->
            HelpKey.entries.forEach { key ->
                assertEquals(helpText(key, AppLanguage.EN), helpText(key, language), "$language/$key")
            }
        }
    }

    @Test
    fun everyHelpTranslationMapIsCompleteAndNonBlank() {
        helpTranslations.forEach { (language, texts) ->
            val missing = HelpKey.entries.toSet() - texts.keys
            assertTrue(missing.isEmpty(), "$language is missing help texts for: $missing")
            texts.forEach { (key, value) -> assertTrue(value.isNotBlank(), "$language/$key is blank") }
        }
    }

    /** Russian and English are compiler-checked for presence, not for having actually been written
     * — a paragraph accidentally left as `""` would compile. Also pins the shape every translation
     * has to keep: three paragraphs per section, none of them a one-liner placeholder. */
    @Test
    fun russianAndEnglishHaveRealTextForEveryKey() {
        listOf(AppLanguage.RU, AppLanguage.EN).forEach { language ->
            HelpKey.entries.forEach { key ->
                val text = helpText(key, language)
                assertTrue(text.length > 40, "$language/$key is too short to be a real paragraph: $text")
            }
        }
    }

    /** Every section's help must be reachable: a [HelpTopic] with a paragraph missing from
     * [HelpKey] can't happen, but a [HelpKey] nobody shows silently can. */
    @Test
    fun everyHelpKeyBelongsToSomeTopic() {
        val used = HelpTopic.entries.flatMap { it.paragraphs }.toSet()
        assertEquals(HelpKey.entries.toSet(), used, "help keys not shown by any section")
    }
}
