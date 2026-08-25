package compose.project.leshy.domain.model

/**
 * 26 official-language interface locales (`.claude/plans/countries-and-languages.md` §4.1) — a
 * strict subset of the 36 languages catalog/country names exist for (`i18n/MushroomNames.kt`,
 * `i18n/CountryNames.kt`): regional (`ca`, `eu`, `gl`) and indigenous (`nah`, `tsz`, `tzo`, `maa`,
 * `hch`, `mi`) name languages have no interface here, and Cyrillic Serbian (`sr-Cyrl`) stays
 * reserved for a future entry rather than being a distinct [AppLanguage] value (§4.2) — `sr` alone
 * covers Serbian.
 *
 * [endonym] is what the language picker shows first (a speaker recognizes their own language even
 * when the current interface language is unfamiliar to them); [englishName] is the smaller second
 * line, same idea as a phone's system language picker. Neither is used for interface strings
 * themselves — those go through [compose.project.leshy.i18n.string]/[compose.project.leshy.i18n.stringResource].
 */
enum class AppLanguage(val code: String, val endonym: String, val englishName: String) {
    BE("be", "Беларуская", "Belarusian"),
    BG("bg", "Български", "Bulgarian"),
    CS("cs", "Čeština", "Czech"),
    DE("de", "Deutsch", "German"),
    EN("en", "English", "English"),
    ES("es", "Español", "Spanish"),
    ET("et", "Eesti", "Estonian"),
    FI("fi", "Suomi", "Finnish"),
    FR("fr", "Français", "French"),
    HR("hr", "Hrvatski", "Croatian"),
    HU("hu", "Magyar", "Hungarian"),
    IT("it", "Italiano", "Italian"),
    JA("ja", "日本語", "Japanese"),
    KA("ka", "ქართული", "Georgian"),
    KO("ko", "한국어", "Korean"),
    LT("lt", "Lietuvių", "Lithuanian"),
    LV("lv", "Latviešu", "Latvian"),
    PL("pl", "Polski", "Polish"),
    RO("ro", "Română", "Romanian"),
    RU("ru", "Русский", "Russian"),
    SK("sk", "Slovenčina", "Slovak"),
    SL("sl", "Slovenščina", "Slovenian"),
    SR("sr", "Српски", "Serbian"),
    SV("sv", "Svenska", "Swedish"),
    TR("tr", "Türkçe", "Turkish"),
    UK("uk", "Українська", "Ukrainian"),
}
