package leshy.mushrooms.map.i18n

/**
 * Keys of the per-section help texts shown by the `?` button in every [
 * leshy.mushrooms.map.ui.components.SectionScaffold] top bar — a second key enum beside
 * [StringKey], deliberately not more values inside it. Three reasons, in order of weight:
 *
 * 1. **Different translation unit.** A [StringKey] is a button label or a one-line message; a
 *    [HelpKey] is a block of prose that has to be read whole to be judged. Keeping them apart
 *    means a translation session can take one language's help texts (`i18n/help/HelpTexts<Xx>.kt`,
 *    one file) without diffing them out of a 310-line file of unrelated short strings.
 * 2. **Independent completeness.** `uiTranslations` is complete for all non-ru/en languages and
 *    `StringsTest` asserts exactly that; help texts start out translated for `ru`/`en` only. Mixed
 *    into [StringKey] they would turn that assertion red for every language at once, with no way to
 *    tell a genuinely missing UI string from a help block still awaiting its translation pass.
 * 3. **The English fallback is honest here.** A missing help block degrades to English prose the
 *    reader can still act on ([helpText]); the same fallback for a lone button label inside an
 *    otherwise translated screen would just look broken.
 *
 * The catalog/country-name layers (`i18n/CLAUDE.md`) left [StringKey] for a different reason — they
 * are unbounded data, not interface text. Help texts are bounded and enumerable, so they stay keys
 * with an exhaustive `when`; only the enum is a separate one.
 *
 * **One key is one block: a picture of a control plus one to three sentences about it.** The
 * illustration for a key lives in `ui/components/HelpIllustrations.kt`, which matches on this enum
 * exhaustively — adding a value here makes that `when` red until the new block gets its picture
 * (or an explicit "no picture" branch). Keys are ordered exactly as the section shows them.
 */
enum class HelpKey {
    RecordPurpose,
    RecordStartFinish,
    RecordTiles,
    RecordPlace,
    RecordNavigation,
    RecordSearchAndOwn,
    RecordFilters,
    RecordBackground,

    ArchivePurpose,
    ArchiveDetail,
    ArchiveShare,
    ArchiveSelection,
    ArchiveUnfinished,

    MapPurpose,
    MapFullScreen,
    MapSliders,
    MapStats,
    MapFilters,
    MapPlaces,

    SpeciesPurpose,
    SpeciesCollections,
    SpeciesOwn,
    SpeciesCheckboxes,
    SpeciesImages,

    PreparationPurpose,
    PreparationDownload,
    PreparationRegions,
    PreparationAreaSize,
    PreparationBackground,

    DataPurpose,
    DataExport,
    DataImport,
    DataArchiveContents,
    DataDuplicates,

    SettingsPurpose,
    SettingsLanguage,
    SettingsTheme,
    SettingsMushroomSize,
    SettingsMushroomOrder,
    SettingsMapData,
}

/**
 * One screen's help: the blocks it is read as, in order. A block is a picture of a control and the
 * sentences about that control — deliberately small, because the previous shape of this help (three
 * long paragraphs per section: purpose / every control at once / consequences) read as a wall of
 * text on a phone, which is the one thing a reader who pressed `?` in a forest cannot afford.
 *
 * Two rules keep it that way when a section grows:
 *
 * - **A block is about one thing.** If a block needs the word "besides" to cover a second control,
 *   it is two blocks.
 * - **Consequences stay with what causes them** (a permanent delete is described in the block about
 *   the delete button), rather than being collected into a "details" paragraph at the end that the
 *   reader has to map back onto the controls by hand.
 */
enum class HelpTopic(val title: StringKey, val blocks: List<HelpKey>) {
    // Заголовок берётся отсюда, а не из `SectionScaffold`, у которого он тоже есть: над «Записью»
    // тот показывает название приложения (это домашний экран), а справке нужно имя раздела — то
    // же, что стоит пунктом в боковой панели.
    RECORD(
        StringKey.NavRecord,
        listOf(
            HelpKey.RecordPurpose,
            HelpKey.RecordStartFinish,
            HelpKey.RecordTiles,
            HelpKey.RecordPlace,
            HelpKey.RecordNavigation,
            HelpKey.RecordSearchAndOwn,
            HelpKey.RecordFilters,
            HelpKey.RecordBackground,
        ),
    ),
    ARCHIVE(
        StringKey.NavArchive,
        listOf(
            HelpKey.ArchivePurpose,
            HelpKey.ArchiveDetail,
            HelpKey.ArchiveShare,
            HelpKey.ArchiveSelection,
            HelpKey.ArchiveUnfinished,
        ),
    ),
    MAP(
        StringKey.NavMap,
        listOf(
            HelpKey.MapPurpose,
            HelpKey.MapFullScreen,
            HelpKey.MapSliders,
            HelpKey.MapStats,
            HelpKey.MapFilters,
            HelpKey.MapPlaces,
        ),
    ),
    SPECIES(
        StringKey.NavSpecies,
        listOf(
            HelpKey.SpeciesPurpose,
            HelpKey.SpeciesCollections,
            HelpKey.SpeciesOwn,
            HelpKey.SpeciesCheckboxes,
            HelpKey.SpeciesImages,
        ),
    ),
    PREPARATION(
        StringKey.NavPreparation,
        listOf(
            HelpKey.PreparationPurpose,
            HelpKey.PreparationDownload,
            HelpKey.PreparationRegions,
            HelpKey.PreparationAreaSize,
            HelpKey.PreparationBackground,
        ),
    ),
    DATA(
        StringKey.NavData,
        listOf(
            HelpKey.DataPurpose,
            HelpKey.DataExport,
            HelpKey.DataImport,
            HelpKey.DataArchiveContents,
            HelpKey.DataDuplicates,
        ),
    ),
    SETTINGS(
        StringKey.SettingsTitle,
        listOf(
            HelpKey.SettingsPurpose,
            HelpKey.SettingsLanguage,
            HelpKey.SettingsTheme,
            HelpKey.SettingsMushroomSize,
            HelpKey.SettingsMushroomOrder,
            HelpKey.SettingsMapData,
        ),
    ),
}
