package compose.project.leshy.i18n

import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.i18n.PluralCategory.Few
import compose.project.leshy.i18n.PluralCategory.Many
import compose.project.leshy.i18n.PluralCategory.One
import compose.project.leshy.i18n.PluralCategory.Other
import compose.project.leshy.i18n.PluralCategory.Two
import compose.project.leshy.i18n.PluralCategory.Zero
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The one guard on [pluralCategory]. A wrong rule here is invisible — the app runs, one language
 * out of twenty-six just says "5 гриб" — so the expectations below are transcribed from CLDR's own
 * sample values rather than derived from the implementation: every count that CLDR names as a
 * boundary for a language (the teens, the multiples of ten, the second and third hundred) is
 * listed explicitly, including the ones where two languages of the same family diverge (Polish 21
 * vs Russian 21, Czech 22 vs Polish 22, Croatian 5 vs Russian 5).
 */
class PluralsTest {
    /** count → expected category, per language. Counts not listed are not asserted. */
    private val expectations: Map<AppLanguage, Map<Int, PluralCategory>> = mapOf(
        // No grammatical number.
        AppLanguage.JA to mapOf(0 to Other, 1 to Other, 2 to Other, 5 to Other, 21 to Other),
        AppLanguage.KO to mapOf(0 to Other, 1 to Other, 2 to Other, 5 to Other, 21 to Other),

        // Two-way split on n = 1.
        AppLanguage.BG to twoWay(),
        AppLanguage.DE to twoWay(),
        AppLanguage.EN to twoWay(),
        AppLanguage.ET to twoWay(),
        AppLanguage.FI to twoWay(),
        AppLanguage.HU to twoWay(),
        AppLanguage.KA to twoWay(),
        AppLanguage.SV to twoWay(),
        AppLanguage.TR to twoWay(),

        // French: 0 is singular; `many` is the million form.
        AppLanguage.FR to mapOf(
            0 to One, 1 to One,
            2 to Other, 5 to Other, 11 to Other, 21 to Other, 101 to Other,
            1_000_000 to Many, 2_000_000 to Many, 1_000_001 to Other,
        ),

        // Spanish/Italian: same million form, but 0 is plural.
        AppLanguage.ES to romanceWithMillions(),
        AppLanguage.IT to romanceWithMillions(),

        // East Slavic: 1/21/101 one, 2–4 few, teens and 5–20 many.
        AppLanguage.BE to eastSlavic(),
        AppLanguage.RU to eastSlavic(),
        AppLanguage.UK to eastSlavic(),

        // Polish: 21 is `many`, unlike Russian's `one`.
        AppLanguage.PL to mapOf(
            1 to One,
            2 to Few, 3 to Few, 4 to Few, 22 to Few, 23 to Few, 24 to Few, 102 to Few,
            0 to Many, 5 to Many, 11 to Many, 12 to Many, 13 to Many, 14 to Many,
            21 to Many, 25 to Many, 101 to Many, 111 to Many,
        ),

        // West Slavic: exactly 2–4 few; 22 is `other`, unlike Polish's `few`.
        AppLanguage.CS to westSlavic(),
        AppLanguage.SK to westSlavic(),

        // Serbo-Croatian: East Slavic shape with `other` where Russian has `many`.
        AppLanguage.HR to serboCroatian(),
        AppLanguage.SR to serboCroatian(),

        // Slovenian dual.
        AppLanguage.SL to mapOf(
            1 to One, 101 to One, 201 to One,
            2 to Two, 102 to Two,
            3 to Few, 4 to Few, 103 to Few, 104 to Few,
            0 to Other, 5 to Other, 11 to Other, 12 to Other, 100 to Other, 200 to Other,
        ),

        // Lithuanian: few runs to 9; the whole 11–19 window is `other`.
        AppLanguage.LT to mapOf(
            1 to One, 21 to One, 101 to One, 121 to One,
            2 to Few, 9 to Few, 22 to Few, 29 to Few, 102 to Few,
            0 to Other, 10 to Other, 11 to Other, 19 to Other, 20 to Other,
            110 to Other, 111 to Other, 119 to Other,
        ),

        // Latvian: the only `zero` of the 26.
        AppLanguage.LV to mapOf(
            0 to Zero, 10 to Zero, 11 to Zero, 15 to Zero, 19 to Zero, 20 to Zero, 30 to Zero,
            110 to Zero, 111 to Zero, 115 to Zero,
            1 to One, 21 to One, 101 to One, 131 to One,
            2 to Other, 9 to Other, 22 to Other, 102 to Other,
        ),

        // Romanian: few covers 0 and the 2–19 window of every hundred; 101 is `other`.
        AppLanguage.RO to mapOf(
            1 to One,
            0 to Few, 2 to Few, 19 to Few, 102 to Few, 119 to Few,
            20 to Other, 100 to Other, 101 to Other, 120 to Other,
        ),
    )

    private fun twoWay() = mapOf(
        1 to One,
        0 to Other, 2 to Other, 5 to Other, 11 to Other, 21 to Other, 101 to Other,
    )

    private fun romanceWithMillions() = mapOf(
        1 to One,
        0 to Other, 2 to Other, 5 to Other, 21 to Other, 101 to Other, 1_000_001 to Other,
        1_000_000 to Many, 2_000_000 to Many,
    )

    private fun eastSlavic() = mapOf(
        1 to One, 21 to One, 101 to One,
        2 to Few, 3 to Few, 4 to Few, 22 to Few, 104 to Few,
        0 to Many, 5 to Many, 11 to Many, 12 to Many, 13 to Many, 14 to Many,
        25 to Many, 111 to Many,
    )

    private fun westSlavic() = mapOf(
        1 to One,
        2 to Few, 3 to Few, 4 to Few,
        0 to Other, 5 to Other, 11 to Other, 21 to Other, 22 to Other, 101 to Other,
    )

    private fun serboCroatian() = mapOf(
        1 to One, 21 to One, 101 to One,
        2 to Few, 3 to Few, 4 to Few, 22 to Few, 104 to Few,
        0 to Other, 5 to Other, 11 to Other, 12 to Other, 14 to Other, 25 to Other, 111 to Other,
    )

    @Test
    fun everyLanguageFollowsItsCldrRule() {
        expectations.forEach { (language, counts) ->
            counts.forEach { (count, expected) ->
                assertEquals(expected, pluralCategory(language, count), "$language / $count")
            }
        }
    }

    /** The table above is the test; a language missing from it would be silently unverified. */
    @Test
    fun everyLanguageIsCovered() {
        val uncovered = AppLanguage.entries.toSet() - expectations.keys
        assertTrue(uncovered.isEmpty(), "No CLDR expectations for: $uncovered")
    }

    /** Every form of every unit resolves to a distinct key and carries text in both base
     * languages — the six-key sets in `Strings.kt` are hand-written and easy to get wrong. */
    @Test
    fun everyUnitHasSixDistinctNonBlankForms() {
        listOf(mushroomsForms, walksForms, regionsForms).forEach { forms ->
            val keys = PluralCategory.entries.map { forms.keyFor(it) }
            assertEquals(PluralCategory.entries.size, keys.toSet().size, "duplicate keys in $keys")
            keys.forEach { key ->
                assertTrue(string(key, AppLanguage.RU).isNotBlank(), "blank ru: $key")
                assertTrue(string(key, AppLanguage.EN).isNotBlank(), "blank en: $key")
            }
        }
    }

    /** Counts are never negative in the app, but a stray one must not pick a form by accident of
     * Kotlin's signed `%` — [pluralCategory] takes the absolute value like CLDR's `n` does. */
    @Test
    fun negativeCountsMirrorPositiveOnes() {
        AppLanguage.entries.forEach { language ->
            (1..200).forEach { count ->
                assertEquals(
                    pluralCategory(language, count),
                    pluralCategory(language, -count),
                    "$language / -$count",
                )
            }
            // No overflow on the one value whose negation doesn't fit in an Int.
            pluralCategory(language, Int.MIN_VALUE)
        }
    }
}
