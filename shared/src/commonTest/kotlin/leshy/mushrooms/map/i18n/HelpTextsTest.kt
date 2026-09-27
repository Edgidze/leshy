package leshy.mushrooms.map.i18n

import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Edition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The help-text twin of `StringsTest`: `helpTranslations` (`HelpTexts.kt`) replaces the compiler's
 * exhaustive-`when` completeness check for every [AppLanguage] beyond `ru`/`en`, so the check runs
 * here instead.
 *
 * Both tests now carry the same load `StringsTest` does: every non-ru/en language has its own table
 * (`.claude/plans/help-screens.md`), so [everyHelpTranslationMapIsCompleteAndNonBlank] checks all 40
 * of them and [everyKeyFallsBackToEnglishForUntranslatedLanguages] covers nobody — it stays as the
 * guard for a 41st language added before its file is written, exactly like the `?: englishHelpTexts`
 * fallback it mirrors.
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
            val missing = HelpKey.entries.toSet() - texts.keys - EDITION_ONLY_HELP_KEYS
            assertTrue(missing.isEmpty(), "$language is missing help texts for: $missing")
            texts.forEach { (key, value) -> assertTrue(value.isNotBlank(), "$language/$key is blank") }
        }
    }

    /** Russian and English are compiler-checked for presence, not for having actually been written
     * — a block accidentally left as `""` would compile. Also pins the shape every translation has
     * to keep: a real block of prose, not a one-liner placeholder. */
    @Test
    fun russianAndEnglishHaveRealTextForEveryKey() {
        listOf(AppLanguage.RU, AppLanguage.EN).forEach { language ->
            HelpKey.entries.forEach { key ->
                val text = helpText(key, language)
                assertTrue(text.length > 40, "$language/$key is too short to be a real block: $text")
            }
        }
    }

    /** Every section's help must be reachable: a [HelpTopic] with a block missing from [HelpKey]
     * can't happen, but a [HelpKey] nobody shows silently can. */
    @Test
    fun everyHelpKeyBelongsToSomeTopic() {
        val used = HelpTopic.entries.flatMap { it.blocks }.toSet()
        assertEquals(HelpKey.entries.toSet() - EDITION_ONLY_HELP_KEYS, used, "help keys not shown by any section")
    }

    /**
     * ...а ключи редакции — тем, что их показывает [editionHelpBlocks] этой редакции. Без этой
     * проверки исключение выше стало бы дырой: забытый ключ редакции не показал бы никто, и
     * заметить это было бы некому.
     */
    @Test
    fun everyEditionOnlyHelpKeyIsShownBySomeEdition() {
        val shownByEditions = Edition.entries.flatMap { edition ->
            HelpTopic.entries.flatMap { topic -> editionHelpBlocks(topic, edition) }
        }.toSet()
        val unused = EDITION_ONLY_HELP_KEYS - shownByEditions
        assertTrue(unused.isEmpty(), "edition help keys shown by nobody: $unused")
    }

    /** Блок редакции обязан иметь настоящий русский и английский текст — на остальных языках он
     * штатно уходит на английский фолбэк, ровно как строки из `EDITION_ONLY_KEYS`. */
    @Test
    fun everyEditionOnlyHelpKeyHasRussianAndEnglishText() {
        EDITION_ONLY_HELP_KEYS.forEach { key ->
            listOf(AppLanguage.RU, AppLanguage.EN).forEach { language ->
                assertTrue(helpText(key, language).length > 40, "$language/$key без настоящего текста")
            }
        }
    }

    /** ...and to exactly one section: a block shown by two sections would be a copy-paste slip in
     * [HelpTopic], invisible in the app until someone notices the same picture and the same
     * sentences in two places. */
    @Test
    fun noHelpKeyIsSharedBetweenTopics() {
        val shown = HelpTopic.entries.flatMap { it.blocks }
        assertEquals(shown.size, shown.toSet().size, "help keys shown by more than one section")
    }

    /** A block is a picture plus a sentence or three, not a paragraph in disguise — the length cap
     * is what keeps the screen scannable as sections grow. The longest Russian block today sits
     * around 300 characters; 420 leaves room for a translation that runs longer than the original
     * without leaving room for a wall of text. */
    @Test
    fun blocksStayShortEnoughToReadUnderAPicture() {
        AppLanguage.entries.forEach { language ->
            HelpKey.entries.forEach { key ->
                val text = helpText(key, language)
                assertTrue(text.length <= 420, "$language/$key is ${text.length} chars, too long for one block: $text")
            }
        }
    }
}
