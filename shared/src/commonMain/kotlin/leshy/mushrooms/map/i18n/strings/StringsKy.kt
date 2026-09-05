package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Kyrgyz — Phase 6 of `.claude/plans/post-soviet-countries.md`. Plural forms follow the two-way
 * split (`Plurals.kt`), but as in every Turkic language here the noun itself never inflects after a
 * numeral ("1 козу карын", "5 козу карын") — all six per-unit forms below carry the identical
 * singular word on purpose, not a placeholder.
 *
 * "Козу карын" (literally "lamb's stomach") is the ordinary Kyrgyz word for mushroom in general,
 * not only for the morel the country's export trade is built on — the same word the research
 * session used for `names/ky.json`, so the interface and the species names stay consistent. */
internal val kyrgyzStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Лешийден козу карын картасы",
    StringKey.NavRecord to "Жаңы жазуу",
    StringKey.NavArchive to "Сейилдөөлөр архиви",
    StringKey.NavMap to "Табылгалар картасы",
    StringKey.NavData to "Экспорт/Импорт",
    StringKey.NavPreparation to "Алдын ала жүктөө",
    StringKey.NavSpecies to "Менин козу карындарым",
    StringKey.SettingsTitle to "Жөндөөлөр",
    StringKey.SettingsContentDescription to "Жөндөөлөр",
    StringKey.SettingsLanguageTitle to "Интерфейс тили",
    StringKey.SettingsThemeTitle to "Көрүнүшү",
    StringKey.SettingsThemeLight to "Ачык",
    StringKey.SettingsThemeDark to "Караңгы",
    StringKey.SettingsThemeSystem to "Системалык",
    StringKey.SettingsCategoriesTitle to "Белгиленүүчү козу карындар",
    StringKey.SettingsMushroomSizeTitle to "Картадагы козу карындын өлчөмүн жөндөңүз",
    StringKey.SettingsMushroomSortTitle to "Козу карындардын тартиби",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Сейилдөө аягында козу карындардын тартибин калыбына келтирүү",
    StringKey.SettingsFreezeMushroomOrder to "Козу карындардын тартибин бекитүү",

    StringKey.MushroomImagesDisclaimer to
        "Колдонмодогу бардык козу карын сүрөттөрү маалымат үчүн гана — тааныш эмес козу карынды " +
            "аныктоо үчүн аларды колдонбоңуз!",

    StringKey.SpeciesCollectionsTitle to "Козу карын жыйнактары",
    StringKey.SpeciesMyMushroomsTitle to "Кошулган козу карындар",
    StringKey.SpeciesMyMushroomsEmpty to "Өзүңүз кошкон козу карындар ушул жерде көрүнөт",
    StringKey.SpeciesAddButton to "Козу карын кошуу",
    StringKey.SpeciesFormTitleCreate to "Жаңы козу карын",
    StringKey.SpeciesFormTitleEdit to "Козу карынды түзөтүү",
    StringKey.SpeciesFormNameHint to "Аталышы",
    StringKey.SpeciesFormScientificNameHint to "Илимий аталышы",
    StringKey.SpeciesFormColorLabel to "Түсү",
    StringKey.SpeciesFormTakePhotoButton to "Камера",
    StringKey.SpeciesFormPickPhotoButton to "Галерея",
    StringKey.SpeciesFormPickCatalogButton to "Сүрөттөр",
    StringKey.SpeciesFormSaveButton to "Сактоо",
    StringKey.SpeciesFormCancelContentDescription to "Жокко чыгаруу",
    StringKey.SpeciesListImportedLabel to "архивден",
    StringKey.SpeciesListEditContentDescription to "Түзөтүү",
    StringKey.SpeciesListDeleteContentDescription to "Түрдү өчүрүү",
    StringKey.SpeciesDeleteConfirmTitle to "Бул козу карын өчүрүлсүнбү?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Бул түрдү өчүрүүнү каалаганыңыз анык бекен? Ага катталган бардык табылгалар «Белгисиз " +
            "козу карын» категориясына которулат. Бул аракетти артка кайтарууга болбойт.",
    StringKey.SpeciesDeleteConfirmYes to "Ооба",
    StringKey.SpeciesDeleteConfirmNo to "Жок",

    StringKey.CatalogPhotoPickerTitle to "Сүрөт тандаңыз",

    StringKey.IconEditorTitle to "Сүрөт түзөткүч",
    StringKey.IconEditorToolEraser to "Өчүргүч",
    StringKey.IconEditorToolCrop to "Кесүү",
    StringKey.IconEditorShapeRectangle to "Тик бурчтук",
    StringKey.IconEditorShapeOval to "Сүйрү",
    StringKey.IconEditorBrushSizeLabel to "Калемдин өлчөмү",
    StringKey.IconEditorUndoContentDescription to "Артка кайтаруу",
    StringKey.IconEditorRedoContentDescription to "Кайталоо",
    StringKey.IconEditorDoneContentDescription to "Даяр",

    StringKey.OnboardingTitle to "Кош келиңиз!",
    StringKey.OnboardingDescription to
        "Сизди кызыктырган козу карын жыйнактарын тандаңыз. Муну кийин Жөндөөлөрдөн өзгөртсө болот.",
    StringKey.OnboardingContinueButton to "Баштайлы",

    StringKey.WelcomeIntro to
        "Колдонмо кайдан өткөнүңүздү жана эмне тапканыңызды эстеп калат — жана козу карын терүүгө чындап " +
            "жардам берет: жакшы жерлерге оңой кайтасыз, ал табылгалардын баары бир картадан көрүнөт.",
    StringKey.WelcomeRecordTitle to "Сейилиңизди жазып туруңуз",
    StringKey.WelcomeRecordText to
        "Трек, убакыт жана километрди колдонмо өзү жүргүзөт. Козу карын таптыңызбы — анын тактайчасын басып " +
            "белгилеңиз; булакты, кулаган даракты же унааңызды картанын өзүндө белгилесе болот.",
    StringKey.WelcomeArchiveTitle to "Табылгаларыңызга кайтып келиңиз",
    StringKey.WelcomeArchiveText to
        "Архивде ар бир сейил өзүнчө турат — өз треги жана табылгалары менен. Ал эми жалпы карта алардын " +
            "баарын чогуу көрсөтөт: бардык мезгилдерде кайдан, эмнени, канча тапканыңызды.",
    StringKey.WelcomeHelpTitle to "Ишенбесеңиз «?» баскычын басыңыз",
    StringKey.WelcomeHelpText to
        "Жогорку оң жактагы «?» баскычы ар бир бөлүмдө бар жана ошол бөлүмдүн кандай түзүлгөнүн түшүндүрөт.",
    StringKey.WelcomeMenuTitle to "Калганы — менюда",
    StringKey.WelcomeMenuText to
        "Жогорку сол жактагы меню баскычы колдонмонун бардык бөлүмдөрүнүн жана мүмкүнчүлүктөрүнүн тизмесин " +
            "ачат.",
    StringKey.WelcomeNextButton to "Андан ары",

    StringKey.LegalTitle to "Келишим жана купуялык",
    StringKey.LegalTermsHeading to "Колдонуучу келишими",
    StringKey.LegalTermsText to
        "Толук текст колдонмо жарыяланганга чейин ушул жерде пайда болот. Кыскача: колдонмо — жардамчы, " +
            "козу карын аныктагыч эмес. Табылганын жегенге жарактуулугу жөнүндөгү чечим ар дайым сизде — ал " +
            "чечим үчүн жоопкерчилик да.",
    StringKey.LegalPrivacyHeading to "Купуялык",
    StringKey.LegalPrivacyText to
        "Толук текст колдонмо жарыяланганга чейин ушул жерде пайда болот. Кыскача: сейилдер, белгилер жана " +
            "сүрөттөр түзмөгүңүздө калат. Колдонмо каттоо эсебин ачпайт жана маалыматыңызды эч жакка жөнөтпөйт " +
            "— интернетке openfreemap.org сайтынан карта бөлүктөрүн суроо гана кетет.",
    StringKey.LegalAcceptButton to "Кабыл алам",

    StringKey.NavMenuContentDescription to "Меню",
    StringKey.HelpContentDescription to "Жардам",
    StringKey.HelpDialogTitle to "Жардам",
    StringKey.HelpDialogDismiss to "Түшүнүктүү",

    StringKey.CategoryMisc to "Башка",
    StringKey.CategoryUnknownMushroom to "Белгисиз козу карын",

    StringKey.CollectionPickerSearchHint to "Өлкө издөө",
    StringKey.LanguagePickerSearchHint to "Тил издөө",
    StringKey.LanguagePickerBackContentDescription to "Артка",
    StringKey.LanguagePickerConfirmContentDescription to "Ырастоо",

    StringKey.DefaultWalkName to "Сейилдөө",
    StringKey.RecordWalkNameHint to "Сейилдөөнүн аталышы",
    StringKey.RecordStart to "Баштоо",
    StringKey.RecordPause to "Тыныгуу",
    StringKey.RecordResume to "Улантуу",
    StringKey.RecordFinish to "Аяктоо",
    StringKey.RecordSetWalkNameTitle to "Сейилдөөнүн аталышын жазыңыз:",
    StringKey.RecordDefaultWalkNamePrefix to "Сейилдөө",
    StringKey.RecordConfirmWalkNameContentDescription to "Ырастоо",
    StringKey.RecordMarkLocationContentDescription to "Жерди белгилөө",
    StringKey.RecordLocationUnavailable to
        "Жайгашуу жеткиликсиз — маршрут жазылбай жатат. Түзмөктүн жөндөөлөрүнөн жайгашуу " +
            "кызматтарын күйгүзүп, колдонмого аларды пайдаланууга уруксат бериңиз.",
    StringKey.RecordLocationUnknownMessage to
        "Жайгашкан жер азырынча белгисиз — белгини байлаганга эч нерсе жок. Геолокация күйгүзүлгөнүн текшериңиз жана сигналды күтүңүз.",
    StringKey.RecordSearchContentDescription to "Издөө",
    StringKey.RecordSearchDialogTitle to "Керектүү козу карынды тандаңыз",
    StringKey.RecordBulkAddQuestion to "Канча жаңы козу карын табылды?",
    StringKey.RecordBulkAddCancelContentDescription to "Жокко чыгаруу",
    StringKey.RecordBulkAddConfirmContentDescription to "Ырастоо",
    StringKey.RecordBulkAddLimitMessage to "Бир сейилдөөдө бир түрдөн эң көп дегенде 999 табылга.",
    StringKey.DialogAcknowledge to "Түшүнүктүү",

    StringKey.NavigationDirectionToPrefix to "Багыты:",
    StringKey.NavigationDistanceToTargetPrefix to "бутага чейин",
    StringKey.NavigationMetersSuffix to "метр",
    StringKey.NavigationKeepRightPhrase to "оңго карай алыңыз",
    StringKey.NavigationKeepLeftPhrase to "солго карай алыңыз",
    StringKey.NavigationGoStraightPhrase to "түз жүрүңүз",
    StringKey.NavigationDeterminingDirection to "Багыт аныкталууда…",
    StringKey.NavigationArrivedPhrase to "Сиз жеттиңиз",
    StringKey.NavigationCloseContentDescription to "Жабуу",

    StringKey.AddPlaceTitle to "Жер кошуу",
    StringKey.AddPlaceEditTitle to "Жерди түзөтүү",
    StringKey.AddPlaceDefaultName to "Жер",
    StringKey.AddPlaceNameHint to "Жердин аталышы",
    StringKey.AddPlacePhotoContentDescription to "Сүрөткө тартуу",
    StringKey.CameraPermissionDenied to
        "Камерага уруксат жок. Түзмөктүн жөндөөлөрүнөн колдонмого уруксат бериңиз.",
    StringKey.AddPlaceDescriptionTitle to "Сүрөттөмө",
    StringKey.AddPlaceDescriptionHint to "Жерди сүрөттөңүз",
    StringKey.AddPlaceCoordinatesTitle to "Координаттар",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Координаттарды көчүрүү",
    StringKey.AddPlaceSaveContentDescription to "Жерди сактоо",
    StringKey.AddPlaceDiscardContentDescription to "Жерден баш тартуу",
    StringKey.PlaceViewEditContentDescription to "Жерди түзөтүү",
    StringKey.PlaceViewDeleteContentDescription to "Жерди өчүрүү",
    StringKey.PlaceDeleteConfirmTitle to "Жер өчүрүлсүнбү?",
    StringKey.PlaceDeleteConfirmMessage to
        "Жер биротоло өчүрүлөт. Бул аракетти артка кайтарууга болбойт.",
    StringKey.PlaceDeleteConfirmYes to "Ооба",
    StringKey.PlaceDeleteConfirmNo to "Жок",

    StringKey.ArchiveEmpty to "Азырынча бир да сейилдөө жазылган эмес",
    StringKey.ArchiveEmptyHint to
        "Бул жерде жазылган сейилдөөлөр болот: маршрут, табылган козу карындар жана белгиленген жерлер.",
    StringKey.EmptyStartWalkButton to "Сейилдөөнү баштоо",
    StringKey.ArchiveDeleteWalksButton to "Сейилдөөлөрдү өчүрүү",
    StringKey.ArchiveDeleteConfirmMessage to
        "Тандалган сейилдөөлөрдү биротоло өчүрүүнү каалаганыңыз анык бекен?",
    StringKey.ArchiveDeleteConfirmYes to "Ооба",
    StringKey.ArchiveDeleteConfirmNo to "Жок",

    StringKey.WalkDetailStartTime to "Башталышы",
    StringKey.WalkDetailEndTime to "Аякташы",
    StringKey.WalkDetailInProgress to "жүрүп жатат",
    StringKey.WalkDetailDistance to "Аралык",
    StringKey.WalkDetailDuration to "Узактыгы",
    StringKey.WalkDetailAvgSpeed to "Орточо ылдамдык",
    StringKey.WalkDetailDurationDays to "күн",
    StringKey.WalkDetailDurationHours to "с",
    StringKey.WalkDetailDurationMinutes to "мүн",
    StringKey.WalkCardDurationHours to "с",
    StringKey.WalkCardDurationMinutes to "м",
    StringKey.UnitKilometers to "км",
    StringKey.UnitKmh to "км/с",
    StringKey.UnitMegabytes to "МБ",
    StringKey.WalkDetailFindsTitle to "Түрлөрү боюнча табылгалар",
    StringKey.WalkDetailFindsEmpty to "Табылга катталган эмес",
    StringKey.WalkDetailPlacesTitle to "Белгиленген жерлер",
    StringKey.WalkDetailViewMap to "Картаны көрүү",
    StringKey.WalkDetailEditContentDescription to "Сейилдөөнүн аталышын өзгөртүү",
    StringKey.WalkDetailEditWalkNameTitle to "Сейилдөөнүн аталышын өзгөртүңүз:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Ырастоо",
    StringKey.WalkDetailDeleteContentDescription to "Сейилдөөнү өчүрүү",
    StringKey.WalkDetailShareAction to "Бөлүшүү",
    StringKey.WalkDetailDeleteAction to "Өчүрүү",
    StringKey.WalkDetailDeleteConfirmTitle to "Сейилдөө өчүрүлсүнбү?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Сейилдөө жана анын бардык табылгалары биротоло өчүрүлөт. Бул аракетти артка кайтарууга " +
            "болбойт.",
    StringKey.WalkDetailDeleteConfirmYes to "Ооба",
    StringKey.WalkDetailDeleteConfirmNo to "Жок",
    StringKey.WalkDetailMushroomsCountZero to "козу карын",
    StringKey.WalkDetailMushroomsCountOne to "козу карын",
    StringKey.WalkDetailMushroomsCountTwo to "козу карын",
    StringKey.WalkDetailMushroomsCountFew to "козу карын",
    StringKey.WalkDetailMushroomsCountMany to "козу карын",
    StringKey.WalkDetailMushroomsCountOther to "козу карын",
    StringKey.WalkDetailDescriptionTitle to "Сүрөттөмө",
    StringKey.WalkDetailDescriptionEmpty to "Сүрөттөмө кошулган эмес",
    StringKey.WalkDetailDescriptionHint to "Сейилдөөнү сүрөттөңүз",
    StringKey.WalkDetailEditDescriptionContentDescription to "Сүрөттөмөнү түзөтүү",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Жокко чыгаруу",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Сактоо",

    StringKey.WalkShareContentDescription to "Сейилдөө менен бөлүшүү",
    StringKey.WalkShareDialogTitle to "Бөлүшүү",
    StringKey.WalkShareOptionName to "Сейилдөөнүн аталышы",
    StringKey.WalkShareOptionStats to "Сейилдөөнүн статистикасы",
    StringKey.WalkShareOptionDescription to "Сейилдөөнүн сүрөттөмөсү",
    StringKey.WalkShareOptionDiagram to "Табылгалар диаграммасы",
    StringKey.WalkShareOptionMap to "Белгилери менен карта",
    StringKey.WalkShareMapWarning to "Башкалар козу карынды кайдан тапканыңызды көрө алышат",
    StringKey.WalkShareCancelButton to "Жокко чыгаруу",
    StringKey.WalkShareConfirmButton to "Бөлүшүү",
    StringKey.WalkShareFooter to "«Лешийден козу карын картасы» колдонмосу менен жасалган",
    StringKey.WalkShareImageFooter to "«Лешийден козу карын картасы» колдонмосунда түзүлгөн",

    StringKey.MapStatsTitle to "Статистика",
    StringKey.MapStatsWalksCount to "Сейилдөөлөр",
    StringKey.MapStatsFindsCount to "Табылган козу карындар",
    StringKey.MapStatsEmptyHint to "Биринчи сейилдөө жазылганда эле, статистика өзү чогулат.",
    StringKey.MapFilterButtonLabel to "Чыпкалар",
    StringKey.MapFilterDialogTitle to
        "Картадагы козу карындарга колдонулуучу чыпкаларды жөндөңүз:",
    StringKey.MapFilterBackContentDescription to "Артка",
    StringKey.MapFilterDateRangeTitle to "Күндөр аралыгы",
    StringKey.MapFilterMonthRangeTitle to "Мезгил",
    StringKey.MapFilterPastRoutesTitle to "Өткөн маршруттарды көрсөтүү",
    StringKey.MapFilterShowPastRoutes to "Өткөн маршруттарды көрсөтүү",

    StringKey.MonthJanuary to "Январь",
    StringKey.MonthFebruary to "Февраль",
    StringKey.MonthMarch to "Март",
    StringKey.MonthApril to "Апрель",
    StringKey.MonthMay to "Май",
    StringKey.MonthJune to "Июнь",
    StringKey.MonthJuly to "Июль",
    StringKey.MonthAugust to "Август",
    StringKey.MonthSeptember to "Сентябрь",
    StringKey.MonthOctober to "Октябрь",
    StringKey.MonthNovember to "Ноябрь",
    StringKey.MonthDecember to "Декабрь",

    StringKey.BackgroundRecordingChannelName to "Сейилдөөнү жазуу",
    StringKey.BackgroundRecordingNotificationTitle to "Сейилдөөңүз жазылып жатат",
    StringKey.BackgroundRecordingNotificationText to
        "Маршрут фондо жазылууда. Колдонмого кайтуу үчүн басыңыз.",

    StringKey.DataExportOption to "Экспорт",
    StringKey.DataImportOption to "Импорт",
    StringKey.DataArchiveNameLabel to "Архивдин аталышы",
    StringKey.DataChooseFileButton to "Файл тандоо",
    StringKey.DataFileStatusLabel to "Импорт файлы",
    StringKey.DataFileNotSelected to "тандалган эмес",
    StringKey.DataImportLabelFieldLabel to "Импорттолгон сейилдөөлөрдүн аталышына кошулуучу белги",
    StringKey.DataDoneButton to "Даяр",
    StringKey.DataSavedButton to "Сакталды",
    StringKey.DataGoToArchiveButton to "Архивге",
    StringKey.DataCancelButton to "Жокко чыгаруу",
    StringKey.DataProcessingLabel to "Иштетилүүдө…",
    StringKey.DataExportSuccessMessage to "Архив ийгиликтүү сакталды",
    StringKey.DataImportedWalksLabel to "Импорттолгон сейилдөөлөр",
    StringKey.DataImportFailedWalksLabel to "Импорттоо мүмкүн болбоду",
    StringKey.DataErrorLabel to "Ката",
    StringKey.DataImportRejectedTitle to "Бул файлды импорттоо мүмкүн эмес",
    StringKey.DataImportRejectedNotArchive to
        "Бул архив эмес: файл ZIP катары окулбайт. Лешийден экспорттолгон архивди тандаңыз.",
    StringKey.DataImportRejectedNotLeshy to
        "Бул ZIP архив, бирок Лешийдики эмес: ичинде manifest.json жок.",
    StringKey.DataImportRejectedNewerFormat to
        "Архив колдонмонун жаңыраак версиясы менен жазылган. Колдонмону жаңыртып, кайра аракет кылыңыз.",
    StringKey.DataImportRejectedDamaged to
        "Архив бузулган: мазмунунун бир бөлүгү окулбайт. Эч нерсе импорттолгон жок.",
    StringKey.DataImportRejectedNoWalks to "Архивде сейилдөө жок — импорттоочу эч нерсе жок.",
    StringKey.DataChooseWalksTitle to "Экспорттолуучу сейилдөөлөр",
    StringKey.DataWalksBackContentDescription to "Тандоону сактабай артка",
    StringKey.DataWalksConfirmContentDescription to "Тандоону ырастоо",
    StringKey.DataWalksSelectedLabel to "Тандалды",
    StringKey.DataWalksCountZero to "сейилдөө",
    StringKey.DataWalksCountOne to "сейилдөө",
    StringKey.DataWalksCountTwo to "сейилдөө",
    StringKey.DataWalksCountFew to "сейилдөө",
    StringKey.DataWalksCountMany to "сейилдөө",
    StringKey.DataWalksCountOther to "сейилдөө",

    StringKey.PreparationSelectAreaButton to "Көрүнүп турган аймакты жүктөө",
    StringKey.PreparationDownloadThisAreaButton to "Ушул аймакты жүктөө",
    StringKey.PreparationRegionNameDialogTitle to "Аймактын аталышы",
    StringKey.PreparationRegionNameLabel to "Мисалы, айылдын жанындагы токой",
    StringKey.PreparationSaveButton to "Жүктөө",
    StringKey.PreparationCancelButton to "Жокко чыгаруу",
    StringKey.PreparationDeleteConfirmTitle to "Аймак өчүрүлсүнбү?",
    StringKey.PreparationDeleteConfirmMessage to "Жүктөлгөн карта бөлүктөрү биротоло өчүрүлөт.",
    StringKey.PreparationDeleteConfirmYes to "Ооба",
    StringKey.PreparationDeleteConfirmNo to "Жок",
    StringKey.PreparationDeleteContentDescription to "Аймакты өчүрүү",
    StringKey.PreparationPauseContentDescription to "Жүктөөнү токтотуу",
    StringKey.PreparationResumeContentDescription to "Жүктөөнү улантуу",
    StringKey.PreparationStatusDownloading to "Жүктөлүүдө",
    StringKey.PreparationStatusPaused to "Токтотулган",
    StringKey.PreparationStatusComplete to "Жүктөлдү",
    StringKey.PreparationStatusError to "Ката",
    StringKey.PreparationSubtitle to
        "Интернетсиз пайдалануу үчүн картанын көрүнүп турган бөлүгүн жүктөңүз",
    StringKey.PreparationRetryContentDescription to "Жүктөөнү кайталоо",

    StringKey.MapTilesLoadFailed to "Карта толук жүктөлгөн жок:",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Билдирүүнү жабуу",

    StringKey.SettingsMapDataTitle to "Карта маалыматтары",
    StringKey.SettingsRefreshMapDataButton to "Карта маалыматтарын жаңыртуу",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Карта маалыматтары жаңыртылсынбы?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Эгер картанын мазмуну өзгөргөн болсо, жүктөлгөн бардык оффлайн аймактар кайра жүктөлөт. " +
            "Карта маалыматтарын жаңыртууну каалаганыңыз анык бекен?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Ооба",
    StringKey.SettingsMapDataUpdateConfirmNo to "Жок",
    StringKey.SettingsMapDataRefreshError to "Жаңыртуу ишке ашкан жок — интернет байланышын текшериңиз",
    StringKey.SettingsMapDataRedownloadingPrefix to "Карта маалыматтары жаңыртылды. Кайра жүктөлүүдө:",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— жүрүшүн Алдын ала жүктөө бөлүмүнөн караңыз.",
    StringKey.SettingsMapDataRegionsCountZero to "аймак",
    StringKey.SettingsMapDataRegionsCountOne to "аймак",
    StringKey.SettingsMapDataRegionsCountTwo to "аймак",
    StringKey.SettingsMapDataRegionsCountFew to "аймак",
    StringKey.SettingsMapDataRegionsCountMany to "аймак",
    StringKey.SettingsMapDataRegionsCountOther to "аймак",
    StringKey.SettingsClearMapCacheButton to "Карта кэшин тазалоо",
    StringKey.SettingsClearMapCacheConfirmTitle to "Карта кэши тазалансынбы?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Карта кэшин тазалоо Алдын ала жүктөө бөлүмүндө сакталбаган каралган карта аймактарын " +
            "өчүрөт. Кэшти тазалоону каалаганыңыз анык бекен?",
    StringKey.SettingsClearMapCacheConfirmYes to "Ооба",
    StringKey.SettingsClearMapCacheConfirmNo to "Жок",
    StringKey.SettingsMapCacheCleared to "Кэш тазаланды",
)
