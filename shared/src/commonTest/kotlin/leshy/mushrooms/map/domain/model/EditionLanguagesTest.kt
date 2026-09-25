package leshy.mushrooms.map.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditionLanguagesTest {
    @Test
    fun worldKeepsAllFortyTwoLanguages() {
        val world = editionLanguagesFor(Edition.WORLD)
        assertEquals(AppLanguage.entries, world.available)
        assertEquals(AppLanguage.EN, world.fallback)
    }

    @Test
    fun russiaOffersRussianAndEnglishOnly() {
        val russia = editionLanguagesFor(Edition.RUSSIA)
        assertEquals(listOf(AppLanguage.RU, AppLanguage.EN), russia.available)
    }

    /**
     * Телефон с немецкой локалью в российской редакции обязан открыться по-русски: немецкого в
     * переключателе нет, а продукт российский. У мировой тот же телефон открывается по-немецки.
     */
    @Test
    fun aLanguageOutsideTheEditionFallsBackToItsOwnDefault() {
        assertEquals(AppLanguage.DE, editionLanguagesFor(Edition.WORLD).nearest(AppLanguage.DE))
        assertEquals(AppLanguage.RU, editionLanguagesFor(Edition.RUSSIA).nearest(AppLanguage.DE))
        assertEquals(AppLanguage.EN, editionLanguagesFor(Edition.RUSSIA).nearest(AppLanguage.EN))
    }

    /** Английский — фолбэк непереведённых ключей в самом i18n, без него в интерфейсе были бы дыры. */
    @Test
    fun everyEditionOffersEnglish() {
        Edition.entries.forEach { edition ->
            val languages = editionLanguagesFor(edition)
            assertTrue(AppLanguage.EN in languages.available, "$edition без английского")
            assertTrue(languages.fallback in languages.available, "$edition: фолбэк вне набора")
        }
    }
}
