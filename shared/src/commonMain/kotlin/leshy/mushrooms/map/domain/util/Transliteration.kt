package leshy.mushrooms.map.domain.util

import leshy.mushrooms.map.domain.model.AppLanguage

private val CYRILLIC_TO_LATIN = mapOf(
    'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g", 'д' to "d",
    'е' to "e", 'ё' to "yo", 'ж' to "zh", 'з' to "z", 'и' to "i",
    'й' to "y", 'к' to "k", 'л' to "l", 'м' to "m", 'н' to "n",
    'о' to "o", 'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t",
    'у' to "u", 'ф' to "f", 'х' to "kh", 'ц' to "ts", 'ч' to "ch",
    'ш' to "sh", 'щ' to "shch", 'ъ' to "", 'ы' to "y", 'ь' to "",
    'э' to "e", 'ю' to "yu", 'я' to "ya",
)

/** Practical (not GOST/ISO-strict) Cyrillic-to-Latin approximation — good enough to give a
 * Latin-script fallback for a Russian species name the user never filled a scientific name for.
 * Not a claim of botanical correctness, just a readable placeholder the user can overwrite later. */
fun transliterateRuToLatin(text: String): String = buildString {
    for (char in text) {
        val lower = char.lowercaseChar()
        val replacement = CYRILLIC_TO_LATIN[lower]
        when {
            replacement == null -> append(char)
            char.isUpperCase() && replacement.isNotEmpty() ->
                append(replacement.replaceFirstChar(Char::uppercase))
            else -> append(replacement)
        }
    }
}

/** Scientific-name fallback used when the user leaves that field blank when creating/editing a
 * species — see `.claude/plans/user-mushrooms.md` Phase 4. English is already Latin script, so
 * nothing to transliterate there. */
fun scientificNameFallback(name: String, language: AppLanguage): String =
    if (language == AppLanguage.RU) transliterateRuToLatin(name) else name
