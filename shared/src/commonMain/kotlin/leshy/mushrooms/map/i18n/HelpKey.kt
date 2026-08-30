package leshy.mushrooms.map.i18n

/**
 * Keys of the per-section help texts shown by the `?` button in every [
 * leshy.mushrooms.map.ui.components.SectionScaffold] top bar — a second key enum beside
 * [StringKey], deliberately not more values inside it. Three reasons, in order of weight:
 *
 * 1. **Different translation unit.** A [StringKey] is a button label or a one-line message; a
 *    [HelpKey] is a paragraph of prose that has to be read whole to be judged. Keeping them apart
 *    means a translation session can take one language's help texts (`i18n/help/HelpTexts<Xx>.kt`,
 *    ~40 lines) without diffing them out of a 310-line file of unrelated short strings.
 * 2. **Independent completeness.** `uiTranslations` is complete for all 24 non-ru/en languages and
 *    `StringsTest` asserts exactly that; help texts start out translated for `ru`/`en` only. Mixed
 *    into [StringKey] they would turn that assertion red for every language at once, with no way to
 *    tell a genuinely missing UI string from a help paragraph still awaiting its translation pass.
 * 3. **The English fallback is honest here.** A missing help paragraph degrades to English prose
 *    the reader can still act on ([helpText]); the same fallback for a lone button label inside an
 *    otherwise translated screen would just look broken.
 *
 * The catalog/country-name layers (`i18n/CLAUDE.md`) left [StringKey] for a different reason — they
 * are unbounded data, not interface text. Help texts are bounded and enumerable, so they stay keys
 * with an exhaustive `when`; only the enum is a separate one.
 */
enum class HelpKey {
    RecordPurpose,
    RecordActions,
    RecordDetails,

    ArchivePurpose,
    ArchiveActions,
    ArchiveDetails,

    MapPurpose,
    MapActions,
    MapDetails,

    SpeciesPurpose,
    SpeciesActions,
    SpeciesDetails,

    PreparationPurpose,
    PreparationActions,
    PreparationDetails,

    DataPurpose,
    DataActions,
    DataDetails,

    SettingsPurpose,
    SettingsActions,
    SettingsDetails,
}

/**
 * One screen's help entry: what the section is for, what its controls do, and — when the screen has
 * consequences worth spelling out (irreversible deletes, background work, settings that reach into
 * another screen) — a third paragraph of those.
 *
 * [details] is nullable because the third paragraph is genuinely conditional: a section with
 * nothing surprising about it should say two paragraphs and stop rather than pad out a third. Every
 * one of the seven current sections does have something to detail, so all seven fill it in.
 */
enum class HelpTopic(val purpose: HelpKey, val actions: HelpKey, val details: HelpKey?) {
    RECORD(HelpKey.RecordPurpose, HelpKey.RecordActions, HelpKey.RecordDetails),
    ARCHIVE(HelpKey.ArchivePurpose, HelpKey.ArchiveActions, HelpKey.ArchiveDetails),
    MAP(HelpKey.MapPurpose, HelpKey.MapActions, HelpKey.MapDetails),
    SPECIES(HelpKey.SpeciesPurpose, HelpKey.SpeciesActions, HelpKey.SpeciesDetails),
    PREPARATION(HelpKey.PreparationPurpose, HelpKey.PreparationActions, HelpKey.PreparationDetails),
    DATA(HelpKey.DataPurpose, HelpKey.DataActions, HelpKey.DataDetails),
    SETTINGS(HelpKey.SettingsPurpose, HelpKey.SettingsActions, HelpKey.SettingsDetails),
    ;

    /** This topic's paragraphs in reading order, skipping an absent [details]. */
    val paragraphs: List<HelpKey> get() = listOfNotNull(purpose, actions, details)
}
