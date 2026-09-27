package klev.fishing.map.i18n

/**
 * Ключи интерфейсных строк рыбацкого продукта — СВОИ, а не добавленные в грибной `StringKey`.
 *
 * Причина в том, что грибной `StringKey` кроется exhaustive `when` в сорока языковых файлах
 * (`i18n/CLAUDE.md`: полнота принудительная, `else ->` не обходить). Добавь рыбацкие ключи туда — и
 * каждый из сорока файлов грибного приложения обязан их упомянуть, а рыбацкий получит грибные.
 * Поэтому ключей два набора, и при трёх-четырёх предметах это единственный способ, при котором
 * перевод не множится на число продуктов. Разбор — `.claude/plans/product-family.md`, раздел 7.
 *
 * Нейтральные строки («Отмена», «Настройки», «Скачать регион») переиспользуются из `:shared` через
 * его же `stringResource(StringKey.…)` — они там уже есть на 42 языках, дублировать их здесь
 * нечего.
 */
enum class FishStringKey {
    AppName,

    // Нижняя навигация
    NavRecord,
    NavArchive,
    NavMap,
    NavSettings,

    // Экран «Рыбалка»
    RecordTitle,
    RecordStart,
    RecordFinish,
    RecordIdleHint,
    RecordDuration,
    RecordDistance,
    RecordCatchCount,
    RecordAddCatch,
    RecordMethodShore,
    RecordMethodBoat,
    RecordMethodIce,
    RecordWaterBodyLabel,
    RecordWaterBodyHint,
    RecordNoLocation,
    RecordSpeciesHidden,
    RecordTotalWeight,

    // Улов
    CatchTitle,
    CatchEditTitle,
    CatchSpecies,
    CatchSpeciesPick,
    CatchWeight,
    CatchWeightHint,
    CatchLength,
    CatchLengthHint,
    CatchBait,
    CatchBaitHint,
    CatchBaitRecent,
    CatchOutcome,
    CatchOutcomeKept,
    CatchOutcomeReleased,
    CatchOutcomeLost,
    CatchLostReason,
    CatchLostCameOff,
    CatchLostLineBroke,
    CatchLostHookFailed,
    CatchLostTackleBroke,
    CatchLostSnagged,
    CatchLostAtLanding,
    CatchLostOther,
    CatchNote,
    CatchNoteHint,
    CatchSave,
    CatchDelete,
    CatchDeleteConfirm,
    CatchWeightTooBig,
    CatchLengthTooBig,

    // Погода
    WeatherTitle,
    WeatherIntro,
    WeatherAirTemp,
    WeatherWaterTemp,
    WeatherPressure,
    WeatherPressureTrend,
    WeatherTrendRising,
    WeatherTrendSteady,
    WeatherTrendFalling,
    WeatherWind,
    WeatherWindDirection,
    WeatherCloudiness,
    WeatherClear,
    WeatherPartly,
    WeatherOvercast,
    WeatherPrecipitation,
    WeatherPrecipNone,
    WeatherPrecipDrizzle,
    WeatherPrecipRain,
    WeatherPrecipShower,
    WeatherPrecipSnow,
    WeatherSuggest,
    WeatherSuggesting,
    WeatherSuggestFailed,
    WeatherSuggestHint,
    WeatherSave,
    WeatherSkip,
    WeatherEdit,
    WeatherEmpty,
    WeatherFromUser,
    WeatherFromService,
    WeatherFromServiceEdited,
    WeatherMoonPhase,
    MoonNew,
    MoonWaxingCrescent,
    MoonFirstQuarter,
    MoonWaxingGibbous,
    MoonFull,
    MoonWaningGibbous,
    MoonLastQuarter,
    MoonWaningCrescent,

    // Румбы
    WindN, WindNE, WindE, WindSE, WindS, WindSW, WindW, WindNW,

    // Архив
    ArchiveTitle,
    ArchiveEmpty,
    ArchiveEmptyHint,
    ArchiveUnnamedTrip,
    ArchiveRename,
    ArchiveRenameTitle,
    ArchiveDelete,
    ArchiveDeleteConfirm,
    ArchiveCatchesNone,

    // Карта
    MapTitle,
    MapEmpty,
    MapEmptyHint,
    MapAllSpecies,

    // Настройки
    SettingsTitle,
    SettingsLanguage,
    SettingsTheme,
    SettingsUnits,
    SettingsPressureUnit,
    SettingsPressureHpa,
    SettingsPressureMmHg,
    SettingsWeightUnit,
    SettingsWeightMetric,
    SettingsWeightImperial,
    SettingsSpeciesTitle,
    SettingsSpeciesHint,
    SettingsAbout,

    // Общее
    UnitGram,
    UnitKilogram,
    UnitCentimeter,
    UnitMeterPerSecond,
    UnitCelsius,
    UnitKilometer,
    Cancel,
    Save,
    Delete,
    Skip,
    Close,
    Yes,
    No,
}
