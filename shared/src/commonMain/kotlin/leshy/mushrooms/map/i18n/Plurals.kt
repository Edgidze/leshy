package leshy.mushrooms.map.i18n

import leshy.mushrooms.map.domain.model.AppLanguage
import kotlin.math.abs

/**
 * CLDR cardinal plural categories.
 *
 * [Zero] is not in the plan's list (`.claude/plans/countries-and-languages.md` §5, Phase 5 names
 * `One`, `Two`, `Few`, `Many`, `Other`) but Latvian genuinely needs it: `lv` puts 0, every multiple
 * of ten and the whole 11–19 range into a form distinct from `other` ("10 sēņu" vs "2 sēnes"), so
 * folding `lv`'s zero into [Other] would give the wrong word for a large share of real counts.
 * Latvian is the only one of the 26 interface languages with this category.
 *
 * Not every category is reachable in every language, and several are unreachable *everywhere* here
 * because these counts are always non-negative integers: CLDR reserves `many` for fractions in
 * `cs`/`sk`/`lt` and `other` for fractions in `be`/`ru`/`uk`/`pl`. Every unit still carries a
 * [StringKey] for all six forms ([PluralForms]) — the resolver has to be total, and an unreachable
 * form simply repeats the neighbouring one in each translation.
 */
enum class PluralCategory { Zero, One, Two, Few, Many, Other }

/**
 * The six [StringKey]s of one countable unit ("mushroom", "walk", "area"), one per
 * [PluralCategory]. See [mushroomsUnitLabel] and friends in `Strings.kt` for the instances.
 */
internal class PluralForms(
    val zero: StringKey,
    val one: StringKey,
    val two: StringKey,
    val few: StringKey,
    val many: StringKey,
    val other: StringKey,
) {
    fun keyFor(category: PluralCategory): StringKey = when (category) {
        PluralCategory.Zero -> zero
        PluralCategory.One -> one
        PluralCategory.Two -> two
        PluralCategory.Few -> few
        PluralCategory.Many -> many
        PluralCategory.Other -> other
    }
}

/**
 * CLDR cardinal plural rules for all 26 [AppLanguage]s, specialized to non-negative integers —
 * the only kind of count this app formats (mushrooms found, walks selected, offline areas). That
 * specialization is what collapses the CLDR operands to two: with `v = f = t = 0` the operands
 * `n` and `i` coincide, so every rule below reads off `n`, `n % 10` and `n % 100` alone.
 *
 * An exhaustive `when` over [AppLanguage] rather than a `Map` on purpose: a 27th language then
 * fails to compile instead of silently inheriting English's two-way split, which is exactly the
 * class of error nobody would notice (§7 of `.claude/plans/countries-and-languages.md`).
 *
 * Rules transcribed from CLDR 46 `plurals.xml`. Where a language's category is unreachable for
 * integers it is simply absent from its branch below — see [PluralCategory]'s doc.
 */
fun pluralCategory(language: AppLanguage, count: Int): PluralCategory {
    // CLDR's operand `n` is the absolute value; widened to Long so that Int.MIN_VALUE can't wrap.
    val n = abs(count.toLong())
    val mod10 = (n % 10).toInt()
    val mod100 = (n % 100).toInt()
    return when (language) {
        // No grammatical number: everything is `other`.
        AppLanguage.JA, AppLanguage.KO -> PluralCategory.Other

        // Two-way split on n = 1. Germanic/Finnic/Turkic/Kartvelian plus Bulgarian and Hungarian:
        // CLDR writes some of these as `i = 1 and v = 0` and others as `n = 1`, which are the same
        // rule for integers.
        AppLanguage.BG, AppLanguage.DE, AppLanguage.EN, AppLanguage.ET, AppLanguage.FI,
        AppLanguage.HU, AppLanguage.KA, AppLanguage.SV, AppLanguage.TR,
        -> if (n == 1L) PluralCategory.One else PluralCategory.Other

        // French: 0 is singular too ("0 champignon"). `many` is the "million" form ("un million
        // *de* champignons") — unreachable at realistic counts, kept for exactness.
        AppLanguage.FR -> when {
            n == 0L || n == 1L -> PluralCategory.One
            n != 0L && n % 1_000_000L == 0L -> PluralCategory.Many
            else -> PluralCategory.Other
        }

        // Spanish and Italian: same `many` million-form as French, but 0 is plural here.
        AppLanguage.ES, AppLanguage.IT -> when {
            n == 1L -> PluralCategory.One
            n != 0L && n % 1_000_000L == 0L -> PluralCategory.Many
            else -> PluralCategory.Other
        }

        // East Slavic three-way (1 / 2–4 / 5–20), teens 11–14 forced to `many`.
        AppLanguage.BE, AppLanguage.RU, AppLanguage.UK -> when {
            mod100 in 11..14 -> PluralCategory.Many
            mod10 == 1 -> PluralCategory.One
            mod10 in 2..4 -> PluralCategory.Few
            else -> PluralCategory.Many
        }

        // Polish: like East Slavic, except 1 alone is `one` (21 is `many`, not `one`).
        AppLanguage.PL -> when {
            n == 1L -> PluralCategory.One
            mod10 in 2..4 && mod100 !in 12..14 -> PluralCategory.Few
            else -> PluralCategory.Many
        }

        // West Slavic: exactly 1 / exactly 2–4 / everything else. 22 is `other`, unlike Polish.
        AppLanguage.CS, AppLanguage.SK -> when {
            n == 1L -> PluralCategory.One
            n in 2L..4L -> PluralCategory.Few
            else -> PluralCategory.Other
        }

        // Serbo-Croatian: East Slavic shape without a `many` — 5–20 falls into `other`.
        AppLanguage.HR, AppLanguage.SR -> when {
            mod10 == 1 && mod100 != 11 -> PluralCategory.One
            mod10 in 2..4 && mod100 !in 12..14 -> PluralCategory.Few
            else -> PluralCategory.Other
        }

        // Slovenian — the only dual in the set: 1 / 2 / 3–4 / rest, all keyed off n % 100.
        AppLanguage.SL -> when (mod100) {
            1 -> PluralCategory.One
            2 -> PluralCategory.Two
            3, 4 -> PluralCategory.Few
            else -> PluralCategory.Other
        }

        // Lithuanian: `few` runs all the way to 9, and the whole 11–19 range is excluded from both
        // `one` and `few` (so 11–19 and every multiple of ten land in `other`).
        AppLanguage.LT -> when {
            mod10 == 1 && mod100 !in 11..19 -> PluralCategory.One
            mod10 in 2..9 && mod100 !in 11..19 -> PluralCategory.Few
            else -> PluralCategory.Other
        }

        // Latvian: the `zero` language — 0, every multiple of ten and 11–19 take their own form.
        AppLanguage.LV -> when {
            mod10 == 0 || mod100 in 11..19 -> PluralCategory.Zero
            mod10 == 1 -> PluralCategory.One
            else -> PluralCategory.Other
        }

        // Romanian: `few` covers 0 and the 2–19 window of every hundred (102–119 too), while 101 —
        // n % 100 = 1 but i != 1 — is `other`.
        AppLanguage.RO -> when {
            n == 1L -> PluralCategory.One
            n == 0L || mod100 in 2..19 -> PluralCategory.Few
            else -> PluralCategory.Other
        }
    }
}
