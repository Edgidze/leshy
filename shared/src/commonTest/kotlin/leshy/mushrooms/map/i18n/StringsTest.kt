package leshy.mushrooms.map.i18n

import leshy.mushrooms.map.domain.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `uiTranslations` (`Strings.kt`) replaces the compiler's exhaustive-`when` completeness check for
 * every [AppLanguage] beyond `ru`/`en` (`.claude/plans/countries-and-languages.md` §3.2) — this is
 * that check, run at test time instead.
 *
 * The balance between the two tests has flipped since they were written in Phase 4. Back then
 * `uiTranslations` was empty and only [everyKeyFallsBackToEnglishForUntranslatedLanguages]
 * exercised anything; as of Phase 11 all 24 non-ru/en languages are translated, so
 * [everyTranslationMapIsCompleteAndNonBlank] carries the real load (26 × 251 assertions) and the
 * fallback test now iterates an empty list. It is kept deliberately rather than deleted: it starts
 * covering a 27th [AppLanguage] the moment one is added, which is exactly when the English
 * degradation path matters again.
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

    /**
     * Адрес тайл-хоста в тексте о приватности подставляется в рантайме
     * ([stringResourceWithHost]) — у двух продуктов он разный. Перевод, потерявший маркер, ничего
     * не уронит: он просто умолчит, куда именно уходят запросы, а это ровно то утверждение, ради
     * которого абзац написан. Отсюда проверка — дешевле, чем заметить пропажу глазами на
     * сорок втором языке.
     */
    @Test
    fun everyTranslationOfThePrivacyTextCarriesTheHostPlaceholder() {
        AppLanguage.entries.forEach { language ->
            val text = string(StringKey.LegalPrivacyText, language)
            assertTrue(
                TILE_HOST_PLACEHOLDER in text,
                "$language: в LegalPrivacyText нет маркера $TILE_HOST_PLACEHOLDER — «$text»",
            )
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
