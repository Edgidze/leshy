package leshy.mushrooms.map.domain.model

/**
 * 42 official-language interface locales — 26 from `.claude/plans/countries-and-languages.md` §4.1,
 * seven from `.claude/plans/post-soviet-countries.md` (Phase 4) and nine from
 * `.claude/plans/europe-15-countries.md` (Phase 4). Regional (`ca`, `eu`, `gl`)
 * and indigenous (`nah`, `tsz`, `tzo`, `maa`, `hch`, `mi`) name languages have no interface here,
 * and Cyrillic Serbian (`sr-Cyrl`) stays reserved for a future entry rather than being a distinct
 * [AppLanguage] value (§4.2) — `sr` alone covers Serbian. Cyrillic Uzbek (`uz-Cyrl`) is held in
 * reserve the same way: Latin has been the official script since 1993, and `uz` alone covers it.
 * Montenegrin (`cnr`) is not a value either: CLDR has no separate locale for it — the code resolves
 * to Serbian in Latin script — so `ME`'s collection is served by `sr`. Luxembourgish (`lb`) and
 * Irish (`ga`) were considered for `LU` and `IE` and left out on purpose: their speakers all read
 * `fr`/`de`/`en`, and an interface language nobody switches to would cost 292 translated strings
 * while its mushroom names stayed invisible to everyone who doesn't switch.
 *
 * Norwegian is `nb` (Bokmål), not the macrolanguage `no`: that is what both platforms report for a
 * Norwegian device, and [leshy.mushrooms.map.data.platform.currentDeviceLanguage] matches on the
 * reported code. Nynorsk (`nn`) is not a separate value.
 *
 * Script per language follows what the country actually uses today, not what it plans to:
 * `kk`/`ky`/`tg` are Cyrillic (Kazakhstan's Latin transition is scheduled for 2031, so Cyrillic is
 * still the standard), `uz`/`tk`/`az` Latin, `hy` Armenian. Armenian needs no font work — the
 * platform fonts cover it on both sides, as they already do Georgian.
 *
 * [endonym] is what the language picker shows first (a speaker recognizes their own language even
 * when the current interface language is unfamiliar to them); [englishName] is the smaller second
 * line, same idea as a phone's system language picker. Neither is used for interface strings
 * themselves — those go through [leshy.mushrooms.map.i18n.string]/[leshy.mushrooms.map.i18n.stringResource].
 */
enum class AppLanguage(val code: String, val endonym: String, val englishName: String) {
    AZ("az", "Azərbaycanca", "Azerbaijani"),
    BE("be", "Беларуская", "Belarusian"),
    BG("bg", "Български", "Bulgarian"),
    BS("bs", "Bosanski", "Bosnian"),
    CS("cs", "Čeština", "Czech"),
    DA("da", "Dansk", "Danish"),
    DE("de", "Deutsch", "German"),
    EL("el", "Ελληνικά", "Greek"),
    EN("en", "English", "English"),
    ES("es", "Español", "Spanish"),
    ET("et", "Eesti", "Estonian"),
    FI("fi", "Suomi", "Finnish"),
    FR("fr", "Français", "French"),
    HR("hr", "Hrvatski", "Croatian"),
    HU("hu", "Magyar", "Hungarian"),
    HY("hy", "Հայերեն", "Armenian"),
    IS("is", "Íslenska", "Icelandic"),
    IT("it", "Italiano", "Italian"),
    JA("ja", "日本語", "Japanese"),
    KA("ka", "ქართული", "Georgian"),
    KK("kk", "Қазақша", "Kazakh"),
    KO("ko", "한국어", "Korean"),
    KY("ky", "Кыргызча", "Kyrgyz"),
    LT("lt", "Lietuvių", "Lithuanian"),
    LV("lv", "Latviešu", "Latvian"),
    MK("mk", "Македонски", "Macedonian"),
    NB("nb", "Norsk bokmål", "Norwegian Bokmål"),
    NL("nl", "Nederlands", "Dutch"),
    PL("pl", "Polski", "Polish"),
    PT("pt", "Português", "Portuguese"),
    RO("ro", "Română", "Romanian"),
    RU("ru", "Русский", "Russian"),
    SK("sk", "Slovenčina", "Slovak"),
    SL("sl", "Slovenščina", "Slovenian"),
    SQ("sq", "Shqip", "Albanian"),
    SR("sr", "Српски", "Serbian"),
    SV("sv", "Svenska", "Swedish"),
    TG("tg", "Тоҷикӣ", "Tajik"),
    TK("tk", "Türkmençe", "Turkmen"),
    TR("tr", "Türkçe", "Turkish"),
    UK("uk", "Українська", "Ukrainian"),
    UZ("uz", "Oʻzbekcha", "Uzbek"),
}
