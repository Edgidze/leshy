package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Turkmen — Phase 6 of `.claude/plans/post-soviet-countries.md`. Plural forms follow the two-way
 * split (`Plurals.kt`), but as in every Turkic language here the noun itself never inflects after a
 * numeral ("1 kömelek", "5 kömelek") — all six per-unit forms below carry the identical singular
 * word on purpose, not a placeholder. */
internal val turkmenStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Leşiden Kömelek Kartasy",
    StringKey.NavRecord to "Täze ýazgy",
    StringKey.NavArchive to "Gezelençler arhiwi",
    StringKey.NavMap to "Tapyndylar kartasy",
    StringKey.NavData to "Eksport/Import",
    StringKey.NavPreparation to "Öňünden ýükleme",
    StringKey.NavSpecies to "Meniň kömeleklerim",
    StringKey.SettingsTitle to "Sazlamalar",
    StringKey.SettingsContentDescription to "Sazlamalar",
    StringKey.SettingsLanguageTitle to "Interfeýs dili",
    StringKey.SettingsThemeTitle to "Görnüş",
    StringKey.SettingsThemeLight to "Açyk",
    StringKey.SettingsThemeDark to "Goýy",
    StringKey.SettingsThemeSystem to "Ulgam",
    StringKey.SettingsCategoriesTitle to "Bellenilýän kömelekler",
    StringKey.SettingsMushroomSizeTitle to "Kartadaky kömelegiň ölçegini sazlaň",
    StringKey.SettingsMushroomSortTitle to "Kömelekleriň tertibi",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Gezelenjiň ahyrynda kömelekleriň tertibini dikeltmek",
    StringKey.SettingsFreezeMushroomOrder to "Kömelekleriň tertibini berkitmek",

    StringKey.MushroomImagesDisclaimer to
        "Programmadaky ähli kömelek suratlary diňe maglumat üçindir — nätanyş kömelegi kesgitlemek " +
            "üçin olary ulanmaň!",

    StringKey.SpeciesCollectionsTitle to "Kömelek ýygyndylary",
    StringKey.SpeciesMyMushroomsTitle to "Goşulan kömelekler",
    StringKey.SpeciesMyMushroomsEmpty to "Özüňiziň goşan kömelekleriňiz şu ýerde görüner",
    StringKey.SpeciesAddButton to "Kömelek goşmak",
    StringKey.SpeciesFormTitleCreate to "Täze kömelek",
    StringKey.SpeciesFormTitleEdit to "Kömelegi redaktirlemek",
    StringKey.SpeciesFormNameHint to "Ady",
    StringKey.SpeciesFormScientificNameHint to "Ylmy ady",
    StringKey.SpeciesFormColorLabel to "Reňki",
    StringKey.SpeciesFormTakePhotoButton to "Kamera",
    StringKey.SpeciesFormPickPhotoButton to "Galereýa",
    StringKey.SpeciesFormPickCatalogButton to "Suratlar",
    StringKey.SpeciesFormSaveButton to "Ýatda saklamak",
    StringKey.SpeciesFormCancelContentDescription to "Ýatyrmak",
    StringKey.SpeciesListImportedLabel to "arhiwden",
    StringKey.SpeciesListEditContentDescription to "Redaktirlemek",
    StringKey.SpeciesListDeleteContentDescription to "Görnüşi pozmak",
    StringKey.SpeciesDeleteConfirmTitle to "Bu kömelek pozulsynmy?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Bu görnüşi pozmak isleýändigiňize ynamyňyz barmy? Oňa ýazylan ähli tapyndy \"Näbelli " +
            "kömelek\" toparyna geçiriler. Bu amal yzyna gaýtarylmaýar.",
    StringKey.SpeciesDeleteConfirmYes to "Hawa",
    StringKey.SpeciesDeleteConfirmNo to "Ýok",

    StringKey.CatalogPhotoPickerTitle to "Surat saýlaň",

    StringKey.IconEditorTitle to "Surat redaktory",
    StringKey.IconEditorToolEraser to "Pozguç",
    StringKey.IconEditorToolCrop to "Kesmek",
    StringKey.IconEditorShapeRectangle to "Gönüburçluk",
    StringKey.IconEditorShapeOval to "Oval",
    StringKey.IconEditorBrushSizeLabel to "Çotganyň ölçegi",
    StringKey.IconEditorUndoContentDescription to "Yzyna gaýtarmak",
    StringKey.IconEditorRedoContentDescription to "Gaýtalamak",
    StringKey.IconEditorDoneContentDescription to "Taýýar",

    StringKey.OnboardingTitle to "Hoş geldiňiz!",
    StringKey.OnboardingDescription to
        "Size gyzykly kömelek ýygyndylaryny saýlaň. Muny soňra Sazlamalarda üýtgedip bolýar.",
    StringKey.OnboardingContinueButton to "Başlalyň",

    StringKey.WelcomeIntro to
        "Programma nireden geçeniňizi we näme tapanyňyzy ýatda saklaýar — we kömelek ýygnamaga " +
            "hakykatdan-da kömek edýär: gowy ýerlere aňsat dolanyp bararsyňyz, ähli tapyndylar bolsa bir " +
            "kartada görünýär.",
    StringKey.WelcomeRecordTitle to "Gezelenjiňizi ýazyň",
    StringKey.WelcomeRecordText to
        "Ugry, wagty we kilometri programma özi ýöredýär. Kömelek tapdyňyzmy — onuň kartoçkasyna degip " +
            "belläň; çeşmäni, ýykylan agajy ýa-da awtoulagyňyzy göni kartada bellemek bolýar.",
    StringKey.WelcomeArchiveTitle to "Tapyndylaryňyza dolanyň",
    StringKey.WelcomeArchiveText to
        "Arhiwde her gezelenç aýratyn durýar — öz ugry we tapyndylary bilen. Umumy karta bolsa olaryň " +
            "hemmesini bilelikde görkezýär: ähli möwsümlerde nirede, nämäniň, näçe tapylandygyny.",
    StringKey.WelcomeHelpTitle to "Ynamyňyz ýok bolsa «?» düwmesine basyň",
    StringKey.WelcomeHelpText to
        "Sag ýokardaky «?» düwmesi her bölümde bar we şol bölümiň nähili gurnalandygyny düşündirýär.",
    StringKey.WelcomeMenuTitle to "Galany — menýuda",
    StringKey.WelcomeMenuText to
        "Çep ýokardaky menýu düwmesi programmanyň ähli bölümleriniň we mümkinçilikleriniň sanawyny açýar.",
    StringKey.WelcomeConsentTitle to "Ulanmazdan öň",
    StringKey.WelcomeConsentIntro to "Programmanyň özüne geçmezden ozal aşakdaky düzgünler bilen ylalaşmaly:",
    StringKey.WelcomeConsentImages to
        "Kömelekleri programmadaky suratlar boýunça kesgitlemäge synanyşmarsyňyz. Suratlar diňe şekillendiriş " +
            "roluny ýerine ýetirýär we barlanan kesgitleýji däl.",
    StringKey.WelcomeConsentEating to
        "Hiç bir ýagdaýda özüňiziň bilmeýän kömelekleriňizi iýmersiňiz. Kömelekler iýip bolmaýan bolup biler, hatda " +
            "zäherli hem bolup biler. Iň gowusy — öz ýeriňiziň kömeleklerine düşünýän birini çagyryň, şonda haýsy " +
            "kömelekleri ýygnamagyň bolýandygyny we olary soň nähili taýýarlamalydygyny bilersiňiz.",
    StringKey.WelcomeConsentWarning to
        "Dowam etmek üçin ýokardaky düzgünler bilen ylalaşyp, ylalaşýan düzgünleriňiziň öňünde bellik goýmaly",

    StringKey.WelcomeNextButton to "Öňe",

    StringKey.LegalTitle to "Gizlinlik",
    StringKey.LegalPrivacyText to
        "Gezelençleriňiz, bellikleriňiz we suratlaryňyz enjamyňyzda galýar. Programma hasap açmaýar we " +
            "maglumatlaryňyzy hiç ýere ibermeýär — internete diňe openfreemap.org salgysyndan karta " +
            "bölekleri üçin soraglar gidýär.",
    StringKey.LegalPrivacyLink to "Gizlinlik syýasaty",

    StringKey.AboutTitle to "Programma barada",
    StringKey.AboutMapDataTitle to "Karta maglumatlary",
    StringKey.AboutMapDataText to
        "Karta ODbL ygtyýarnamasy bilen ýaýradylýan OpenStreetMap maglumatlaryna esaslanýar. Wektor " +
            "bölekleri we stil — OpenMapTiles-den, gowşurylyşy — OpenFreeMap hyzmatyndan.",
    StringKey.AboutOpenSourceTitle to "Açyk kod",
    StringKey.AboutOpenSourceText to
        "Programma açyk kodly kitaphanalardan ýygnaldy. Sanawyň setirine degseňiz, onuň ygtyýarnamasynyň " +
            "doly teksti açylýar.",

    StringKey.NavMenuContentDescription to "Menýu",
    StringKey.HelpContentDescription to "Kömek",
    StringKey.HelpDialogTitle to "Kömek",
    StringKey.HelpDialogDismiss to "Düşnükli",

    StringKey.CategoryMisc to "Beýleki",
    StringKey.CategoryUnknownMushroom to "Näbelli kömelek",

    StringKey.CollectionPickerSearchHint to "Ýurt gözlemek",
    StringKey.LanguagePickerSearchHint to "Dil gözlemek",
    StringKey.LanguagePickerBackContentDescription to "Yza",
    StringKey.LanguagePickerConfirmContentDescription to "Tassyklamak",

    StringKey.DefaultWalkName to "Gezelenç",
    StringKey.RecordWalkNameHint to "Gezelenjiň ady",
    StringKey.RecordStart to "Başlamak",
    StringKey.RecordPause to "Arakesme",
    StringKey.RecordResume to "Dowam etmek",
    StringKey.RecordFinish to "Tamamlamak",
    StringKey.RecordSetWalkNameTitle to "Gezelenjiň adyny ýazyň:",
    StringKey.RecordDefaultWalkNamePrefix to "Gezelenç",
    StringKey.RecordConfirmWalkNameContentDescription to "Tassyklamak",
    StringKey.RecordMarkLocationContentDescription to "Ýeri bellemek",
    StringKey.RecordLocationUnavailable to
        "Ýerleşiş elýeterli däl — ýol ýazylmaýar. Enjamyň sazlamalarynda ýerleşiş hyzmatlaryny " +
            "açyň we programma olary ulanmaga rugsat beriň.",
    StringKey.RecordLocationUnknownMessage to
        "Ýerleşýän ýeriňiz heniz belli däl — bellik daňara zat ýok. Geolokasiýanyň açykdygyny barlaň we signala garaşyň.",
    StringKey.RecordSearchContentDescription to "Gözleg",
    StringKey.RecordSearchDialogTitle to "Gerekli kömelegi saýlaň",
    StringKey.RecordBulkAddQuestion to "Näçe täze kömelek tapyldy?",
    StringKey.RecordBulkAddCancelContentDescription to "Ýatyrmak",
    StringKey.RecordBulkAddConfirmContentDescription to "Tassyklamak",
    StringKey.RecordBulkAddLimitMessage to "Bir gezelençde bir görnüşden köpi bilen 999 tapyndy.",
    StringKey.DialogAcknowledge to "Düşnükli",

    StringKey.NavigationDirectionToPrefix to "Ugry:",
    StringKey.NavigationDistanceToTargetPrefix to "nyşana çenli",
    StringKey.NavigationMetersSuffix to "metr",
    StringKey.NavigationKeepRightPhrase to "saga sowuluň",
    StringKey.NavigationKeepLeftPhrase to "çepe sowuluň",
    StringKey.NavigationGoStraightPhrase to "göni gidiň",
    StringKey.NavigationDeterminingDirection to "Ugur kesgitlenýär…",
    StringKey.NavigationArrivedPhrase to "Siz bardyňyz",
    StringKey.NavigationCloseContentDescription to "Ýapmak",

    StringKey.AddPlaceTitle to "Ýer goşmak",
    StringKey.AddPlaceEditTitle to "Ýeri redaktirlemek",
    StringKey.AddPlaceDefaultName to "Ýer",
    StringKey.AddPlaceNameHint to "Ýeriň ady",
    StringKey.AddPlacePhotoContentDescription to "Surata düşürmek",
    StringKey.CameraPermissionDenied to
        "Kamera rugsat ýok. Enjamyň sazlamalarynda programma rugsat beriň.",
    StringKey.AddPlaceDescriptionTitle to "Beýany",
    StringKey.AddPlaceDescriptionHint to "Ýeri beýan ediň",
    StringKey.AddPlaceCoordinatesTitle to "Koordinatlar",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Koordinatlary göçürmek",
    StringKey.AddPlaceSaveContentDescription to "Ýeri ýatda saklamak",
    StringKey.AddPlaceDiscardContentDescription to "Ýerden ýüz öwürmek",
    StringKey.PlaceViewEditContentDescription to "Ýeri redaktirlemek",
    StringKey.PlaceViewDeleteContentDescription to "Ýeri pozmak",
    StringKey.PlaceDeleteConfirmTitle to "Ýer pozulsynmy?",
    StringKey.PlaceDeleteConfirmMessage to
        "Ýer hemişelik pozular. Bu amal yzyna gaýtarylmaýar.",
    StringKey.PlaceDeleteConfirmYes to "Hawa",
    StringKey.PlaceDeleteConfirmNo to "Ýok",

    StringKey.ArchiveEmpty to "Entek hiç hili gezelenç ýazylmadyk",
    StringKey.ArchiveEmptyHint to "Ýazylan gezelençler şu ýerde bolar: ýol, tapyndylar we bellenen ýerler.",
    StringKey.EmptyStartWalkButton to "Gezelenje başlamak",
    StringKey.ArchiveDeleteWalksButton to "Gezelençleri pozmak",
    StringKey.ArchiveDeleteConfirmMessage to
        "Saýlanan gezelençleri hemişelik pozmak isleýändigiňize ynamyňyz barmy?",
    StringKey.ArchiveDeleteConfirmYes to "Hawa",
    StringKey.ArchiveDeleteConfirmNo to "Ýok",

    StringKey.WalkDetailStartTime to "Başlanyşy",
    StringKey.WalkDetailEndTime to "Tamamlanyşy",
    StringKey.WalkDetailInProgress to "dowam edýär",
    StringKey.WalkDetailDistance to "Aralyk",
    StringKey.WalkDetailDuration to "Dowamlylygy",
    StringKey.WalkDetailAvgSpeed to "Ortaça tizlik",
    StringKey.WalkDetailDurationDays to "gün",
    StringKey.WalkDetailDurationHours to "sag",
    StringKey.WalkDetailDurationMinutes to "min",
    StringKey.WalkCardDurationHours to "sag",
    StringKey.WalkCardDurationMinutes to "m",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/sag",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Görnüşler boýunça tapyndylar",
    StringKey.WalkDetailFindsEmpty to "Tapyndy bellenilmedik",
    StringKey.WalkDetailPlacesTitle to "Bellenen ýerler",
    StringKey.WalkDetailViewMap to "Kartany görmek",
    StringKey.WalkDetailEditContentDescription to "Gezelenjiň adyny üýtgetmek",
    StringKey.WalkDetailEditWalkNameTitle to "Gezelenjiň adyny üýtgediň:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Tassyklamak",
    StringKey.WalkDetailDeleteContentDescription to "Gezelenji pozmak",
    StringKey.WalkDetailShareAction to "Paýlaşmak",
    StringKey.WalkDetailDeleteAction to "Pozmak",
    StringKey.WalkDetailDeleteConfirmTitle to "Gezelenç pozulsynmy?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Gezelenç we onuň ähli tapyndysy hemişelik pozular. Bu amal yzyna gaýtarylmaýar.",
    StringKey.WalkDetailDeleteConfirmYes to "Hawa",
    StringKey.WalkDetailDeleteConfirmNo to "Ýok",
    StringKey.WalkDetailMushroomsCountZero to "kömelek",
    StringKey.WalkDetailMushroomsCountOne to "kömelek",
    StringKey.WalkDetailMushroomsCountTwo to "kömelek",
    StringKey.WalkDetailMushroomsCountFew to "kömelek",
    StringKey.WalkDetailMushroomsCountMany to "kömelek",
    StringKey.WalkDetailMushroomsCountOther to "kömelek",
    StringKey.WalkDetailDescriptionTitle to "Beýany",
    StringKey.WalkDetailDescriptionEmpty to "Beýan goşulmadyk",
    StringKey.WalkDetailDescriptionHint to "Gezelenji beýan ediň",
    StringKey.WalkDetailEditDescriptionContentDescription to "Beýany redaktirlemek",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Ýatyrmak",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Ýatda saklamak",

    StringKey.WalkShareContentDescription to "Gezelenji paýlaşmak",
    StringKey.WalkShareDialogTitle to "Paýlaşmak",
    StringKey.WalkShareOptionName to "Gezelenjiň ady",
    StringKey.WalkShareOptionStats to "Gezelenjiň statistikasy",
    StringKey.WalkShareOptionDescription to "Gezelenjiň beýany",
    StringKey.WalkShareOptionDiagram to "Tapyndylaryň diagrammasy",
    StringKey.WalkShareOptionMap to "Bellikli karta",
    StringKey.WalkShareMapWarning to "Beýlekiler kömelegi nireden tapandygyňyzy görüp bilerler",
    StringKey.WalkShareCancelButton to "Ýatyrmak",
    StringKey.WalkShareConfirmButton to "Paýlaşmak",
    StringKey.WalkShareFooter to "\"Leşiden Kömelek Kartasy\" programmasy bilen döredildi",
    StringKey.WalkShareImageFooter to "Leşiden Kömelek Kartasy programmasynda döredildi",

    StringKey.MapStatsTitle to "Statistika",
    StringKey.MapStatsWalksCount to "Gezelençler",
    StringKey.MapStatsFindsCount to "Tapylan kömelekler",
    StringKey.MapStatsEmptyHint to "Ilkinji gezelenç ýazylan badyna statistika özi ýygnalar.",
    StringKey.MapFilterButtonLabel to "Filtrler",
    StringKey.MapFilterDialogTitle to "Kartadaky kömeleklere ulanylýan filtrleri sazlaň:",
    StringKey.MapFilterBackContentDescription to "Yza",
    StringKey.MapFilterDateRangeTitle to "Seneleriň aralygy",
    StringKey.MapFilterMonthRangeTitle to "Möwsüm",
    StringKey.MapFilterPastRoutesTitle to "Geçen ýollary görkezmek",
    StringKey.MapFilterShowPastRoutes to "Geçen ýollary görkezmek",

    StringKey.MonthJanuary to "Ýanwar",
    StringKey.MonthFebruary to "Fewral",
    StringKey.MonthMarch to "Mart",
    StringKey.MonthApril to "Aprel",
    StringKey.MonthMay to "Maý",
    StringKey.MonthJune to "Iýun",
    StringKey.MonthJuly to "Iýul",
    StringKey.MonthAugust to "Awgust",
    StringKey.MonthSeptember to "Sentýabr",
    StringKey.MonthOctober to "Oktýabr",
    StringKey.MonthNovember to "Noýabr",
    StringKey.MonthDecember to "Dekabr",

    StringKey.BackgroundRecordingChannelName to "Gezelenji ýazmak",
    StringKey.BackgroundRecordingNotificationTitle to "Gezelenjiňiz ýazylýar",
    StringKey.BackgroundRecordingNotificationText to
        "Ýol arka fonda ýazylýar. Programma dolanmak üçin basyň.",

    StringKey.DataExportOption to "Eksport",
    StringKey.DataImportOption to "Import",
    StringKey.DataArchiveNameLabel to "Arhiwiň ady",
    StringKey.DataChooseFileButton to "Faýl saýlamak",
    StringKey.DataFileStatusLabel to "Import faýly",
    StringKey.DataFileNotSelected to "saýlanmadyk",
    StringKey.DataImportLabelFieldLabel to "Import edilen gezelençleriň adyna goşulýan bellik",
    StringKey.DataDoneButton to "Taýýar",
    StringKey.DataSavedButton to "Ýatda saklandy",
    StringKey.DataGoToArchiveButton to "Arhiwe",
    StringKey.DataCancelButton to "Ýatyrmak",
    StringKey.DataProcessingLabel to "Işlenilýär…",
    StringKey.DataExportSuccessMessage to "Arhiw üstünlikli ýatda saklandy",
    StringKey.DataImportedWalksLabel to "Import edilen gezelençler",
    StringKey.DataImportFailedWalksLabel to "Import edip bolmady",
    StringKey.DataErrorLabel to "Ýalňyşlyk",
    StringKey.DataImportRejectedTitle to "Bu faýly import edip bolmaýar",
    StringKey.DataImportRejectedNotArchive to
        "Bu arhiw däl: faýl ZIP hökmünde okalmaýar. Leşiden eksport edilen arhiwi saýlaň.",
    StringKey.DataImportRejectedNotLeshy to
        "Bu ZIP arhiw, ýöne Leşiniňki däl: içinde manifest.json ýok.",
    StringKey.DataImportRejectedNewerFormat to
        "Arhiw programmanyň täzeräk görnüşi bilen ýazylan. Programmany täzeläň we ýene synanyşyň.",
    StringKey.DataImportRejectedDamaged to
        "Arhiw zeperlenen: mazmunynyň bir bölegi okalmaýar. Hiç zat import edilmedi.",
    StringKey.DataImportRejectedNoWalks to "Arhiwde gezelenç ýok — import etmäge zat ýok.",
    StringKey.DataChooseWalksTitle to "Eksport ediljek gezelençler",
    StringKey.DataWalksBackContentDescription to "Saýlawy ýatda saklaman yza",
    StringKey.DataWalksConfirmContentDescription to "Saýlawy tassyklamak",
    StringKey.DataWalksSelectedLabel to "Saýlandy",
    StringKey.DataWalksCountZero to "gezelenç",
    StringKey.DataWalksCountOne to "gezelenç",
    StringKey.DataWalksCountTwo to "gezelenç",
    StringKey.DataWalksCountFew to "gezelenç",
    StringKey.DataWalksCountMany to "gezelenç",
    StringKey.DataWalksCountOther to "gezelenç",

    StringKey.PreparationSelectAreaButton to "Görünýän sebiti ýüklemek",
    StringKey.PreparationDownloadThisAreaButton to "Şu sebiti ýüklemek",
    StringKey.PreparationRegionNameDialogTitle to "Sebitiň ady",
    StringKey.PreparationRegionNameLabel to "Meselem, obanyň golaýyndaky tokaý",
    StringKey.PreparationSaveButton to "Ýüklemek",
    StringKey.PreparationCancelButton to "Ýatyrmak",
    StringKey.PreparationDeleteConfirmTitle to "Sebit pozulsynmy?",
    StringKey.PreparationDeleteConfirmMessage to "Ýüklenen karta bölekleri hemişelik pozular.",
    StringKey.PreparationDeleteConfirmYes to "Hawa",
    StringKey.PreparationDeleteConfirmNo to "Ýok",
    StringKey.PreparationDeleteContentDescription to "Sebiti pozmak",
    StringKey.PreparationPauseContentDescription to "Ýüklemegi saklamak",
    StringKey.PreparationResumeContentDescription to "Ýüklemegi dowam etmek",
    StringKey.PreparationStatusDownloading to "Ýüklenýär",
    StringKey.PreparationStatusPaused to "Saklandy",
    StringKey.PreparationStatusComplete to "Ýüklendi",
    StringKey.PreparationStatusError to "Ýalňyşlyk",
    StringKey.PreparationSubtitle to
        "Internetsiz ulanmak üçin kartanyň görünýän bölegini ýükläň",
    StringKey.PreparationRetryContentDescription to "Ýüklemegi gaýtalamak",

    StringKey.MapTilesLoadFailed to "Karta doly ýüklenmedi:",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Habary ýapmak",

    StringKey.SettingsMapDataTitle to "Karta maglumatlary",
    StringKey.SettingsRefreshMapDataButton to "Karta maglumatlaryny täzelemek",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Karta maglumatlary täzelensinmi?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Eger kartanyň mazmuny üýtgän bolsa, ýüklenen ähli oflaýn sebit gaýtadan ýüklener. " +
            "Karta maglumatlaryny täzelemek isleýändigiňize ynamyňyz barmy?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Hawa",
    StringKey.SettingsMapDataUpdateConfirmNo to "Ýok",
    StringKey.SettingsMapDataRefreshError to "Täzeleme başartmady — internet birikmesini barlaň",
    StringKey.SettingsMapDataRedownloadingPrefix to "Karta maglumatlary täzelendi. Gaýtadan ýüklenýär:",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— barşyny Öňünden ýükleme bölüminde görüň.",
    StringKey.SettingsMapDataRegionsCountZero to "sebit",
    StringKey.SettingsMapDataRegionsCountOne to "sebit",
    StringKey.SettingsMapDataRegionsCountTwo to "sebit",
    StringKey.SettingsMapDataRegionsCountFew to "sebit",
    StringKey.SettingsMapDataRegionsCountMany to "sebit",
    StringKey.SettingsMapDataRegionsCountOther to "sebit",
    StringKey.SettingsClearMapCacheButton to "Karta keşini arassalamak",
    StringKey.SettingsClearMapCacheConfirmTitle to "Karta keşi arassalansynmy?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Karta keşini arassalamak Öňünden ýükleme bölüminde ýatda saklanmadyk görlen karta " +
            "sebitlerini aýyrýar. Keşi arassalamak isleýändigiňize ynamyňyz barmy?",
    StringKey.SettingsClearMapCacheConfirmYes to "Hawa",
    StringKey.SettingsClearMapCacheConfirmNo to "Ýok",
    StringKey.SettingsMapCacheCleared to "Keş arassalandy",
)
