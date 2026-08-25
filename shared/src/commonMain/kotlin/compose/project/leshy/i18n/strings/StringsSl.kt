package compose.project.leshy.i18n.strings

import compose.project.leshy.i18n.StringKey

/** Slovenian — Phase 10 of `.claude/plans/countries-and-languages.md`. **The only language in the
 * set with a real dual**, and the reason `PluralCategory.Two` exists at all: `pluralCategory`
 * keys off n % 100 — 1 → `One`, 2 → `Two`, 3..4 → `Few`, everything else (0 included) → `Other`
 * (`Plurals.kt`). All four fire on ordinary counts, so every one of them carries a distinct real
 * form here, unlike the other 25 languages where most repeat a neighbour:
 *
 * - 1 goba / 2 gobi / 3 gobe / 5 gob
 * - 1 sprehod / 2 sprehoda / 3 sprehodi / 5 sprehodov
 * - 1 območje / 2 območji / 3 območja / 5 območij
 *
 * `Zero` and `Many` are the only unreachable ones and both repeat `Other`'s genitive plural — 0
 * resolves to `Other`, and no integer reaches `Many`. Because the rule reads n % 100, the dual
 * comes back at 102, 202 and so on; that is correct Slovenian, not an edge case to flatten. */
internal val slovenianStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Zemljevid gob od Lešija",
    StringKey.NavRecord to "Nov vnos",
    StringKey.NavArchive to "Arhiv sprehodov",
    StringKey.NavMap to "Zemljevid najdb",
    StringKey.NavData to "Izvoz/Uvoz",
    StringKey.NavPreparation to "Predhodni prenos",
    StringKey.NavSpecies to "Moje gobe",
    StringKey.SettingsTitle to "Nastavitve",
    StringKey.SettingsContentDescription to "Nastavitve",
    StringKey.SettingsLanguageTitle to "Jezik vmesnika",
    StringKey.SettingsCategoriesTitle to "Gobe za označevanje",
    StringKey.SettingsMushroomSizeTitle to "Nastavite velikost gob na zemljevidu",
    StringKey.SettingsMushroomSortTitle to "Vrstni red gob",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Ponastavi vrstni red gob ob koncu sprehoda",
    StringKey.SettingsFreezeMushroomOrder to "Nespremenljiv vrstni red gob",

    StringKey.MushroomImagesDisclaimer to
        "Vse slike gob v aplikaciji so zgolj ponazoritvene — ne uporabljajte jih za določanje " +
            "neznanih gob!",

    StringKey.SpeciesCollectionsTitle to "Zbirke gob",
    StringKey.SpeciesMyMushroomsTitle to "Dodane gobe",
    StringKey.SpeciesMyMushroomsEmpty to "Tukaj se bodo pojavile gobe, ki jih dodate sami",
    StringKey.SpeciesAddButton to "Dodaj gobo",
    StringKey.SpeciesFormTitleCreate to "Nova goba",
    StringKey.SpeciesFormTitleEdit to "Uredi gobo",
    StringKey.SpeciesFormNameHint to "Ime",
    StringKey.SpeciesFormScientificNameHint to "Znanstveno ime",
    StringKey.SpeciesFormColorLabel to "Barva",
    StringKey.SpeciesFormTakePhotoButton to "Fotoaparat",
    StringKey.SpeciesFormPickPhotoButton to "Galerija",
    StringKey.SpeciesFormPickCatalogButton to "Slike",
    StringKey.SpeciesFormSaveButton to "Shrani",
    StringKey.SpeciesFormCancelContentDescription to "Prekliči",
    StringKey.SpeciesListImportedLabel to "iz arhiva",
    StringKey.SpeciesListEditContentDescription to "Uredi",
    StringKey.SpeciesListDeleteContentDescription to "Izbriši vrsto",
    StringKey.SpeciesDeleteConfirmTitle to "Izbrisati to gobo?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Ali ste prepričani, da želite izbrisati to vrsto? Vse oznake te vrste v sprehodih bodo " +
            "premaknjene v kategorijo „Neznana goba“. Tega dejanja ni mogoče razveljaviti.",
    StringKey.SpeciesDeleteConfirmYes to "Da",
    StringKey.SpeciesDeleteConfirmNo to "Ne",

    StringKey.CatalogPhotoPickerTitle to "Izberite sliko",

    StringKey.IconEditorTitle to "Urejevalnik fotografij",
    StringKey.IconEditorToolEraser to "Radirka",
    StringKey.IconEditorToolCrop to "Obrezovanje",
    StringKey.IconEditorShapeRectangle to "Pravokotnik",
    StringKey.IconEditorShapeOval to "Oval",
    StringKey.IconEditorBrushSizeLabel to "Velikost čopiča",
    StringKey.IconEditorUndoContentDescription to "Razveljavi",
    StringKey.IconEditorRedoContentDescription to "Ponovi",
    StringKey.IconEditorDoneContentDescription to "Končano",

    StringKey.OnboardingTitle to "Dobrodošli!",
    StringKey.OnboardingDescription to
        "Izberite zbirke gob, ki vas zanimajo. To lahko pozneje spremenite v Nastavitvah.",
    StringKey.OnboardingContinueButton to "Začni",

    StringKey.NavMenuContentDescription to "Meni",
    StringKey.HelpContentDescription to "Pomoč",
    StringKey.HelpDialogTitle to "Pomoč",
    StringKey.HelpDialogMessage to
        "Navodila za ta zaslon se bodo tukaj pojavila v eni od naslednjih različic.",
    StringKey.HelpDialogDismiss to "Razumem",

    StringKey.CategoryMisc to "Razno",
    StringKey.CategoryUnknownMushroom to "Neznana goba",

    StringKey.CollectionPickerSearchHint to "Iskanje države",

    StringKey.LanguagePickerSearchHint to "Iskanje jezika",
    StringKey.LanguagePickerBackContentDescription to "Nazaj",
    StringKey.LanguagePickerConfirmContentDescription to "Potrdi",

    StringKey.DefaultWalkName to "Sprehod",
    StringKey.RecordWalkNameHint to "Ime sprehoda",
    StringKey.RecordStart to "Začni",
    StringKey.RecordPause to "Premor",
    StringKey.RecordResume to "Nadaljuj",
    StringKey.RecordFinish to "Končaj",
    StringKey.RecordSetWalkNameTitle to "Vnesite ime sprehoda:",
    StringKey.RecordDefaultWalkNamePrefix to "Sprehod z dne",
    StringKey.RecordConfirmWalkNameContentDescription to "Potrdi",
    StringKey.RecordMarkLocationContentDescription to "Označi mesto",
    StringKey.RecordSearchContentDescription to "Iskanje",
    StringKey.RecordSearchDialogTitle to "Izberite želeno gobo",
    StringKey.RecordBulkAddQuestion to "Koliko novih gob ste našli?",
    StringKey.RecordBulkAddCancelContentDescription to "Prekliči",
    StringKey.RecordBulkAddLimitMessage to
        "Največ 999 najdb iste vrste na en sprehod.",
    StringKey.RecordBulkAddLimitConfirm to "Razumem",

    StringKey.NavigationDirectionToPrefix to "Smer proti",
    StringKey.NavigationDistanceToTargetPrefix to "do cilja",
    StringKey.NavigationMetersSuffix to "metrov",
    StringKey.NavigationKeepRightPhrase to "držite se desno za",
    StringKey.NavigationKeepLeftPhrase to "držite se levo za",
    StringKey.NavigationGoStraightPhrase to "pojdite naravnost",
    StringKey.NavigationDeterminingDirection to "Določamo smer…",
    StringKey.NavigationArrivedPhrase to "Prispeli ste",
    StringKey.NavigationCloseContentDescription to "Zapri",

    StringKey.AddPlaceTitle to "Dodajte mesto",
    StringKey.AddPlaceEditTitle to "Uredite mesto",
    StringKey.AddPlaceDefaultName to "Mesto",
    StringKey.AddPlaceNameHint to "Ime mesta",
    StringKey.AddPlacePhotoContentDescription to "Fotografiraj",
    StringKey.AddPlaceDescriptionTitle to "Opis",
    StringKey.AddPlaceDescriptionHint to "Opišite mesto",
    StringKey.AddPlaceCoordinatesTitle to "Koordinate",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Kopiraj koordinate",
    StringKey.AddPlaceSaveContentDescription to "Shrani mesto",
    StringKey.AddPlaceDiscardContentDescription to "Izbriši mesto",

    StringKey.PlaceViewEditContentDescription to "Uredi mesto",
    StringKey.PlaceViewDeleteContentDescription to "Izbriši mesto",
    StringKey.PlaceDeleteConfirmTitle to "Izbrisati mesto?",
    StringKey.PlaceDeleteConfirmMessage to
        "Mesto bo trajno izbrisano. Obnovitev ne bo mogoča.",
    StringKey.PlaceDeleteConfirmYes to "Da",
    StringKey.PlaceDeleteConfirmNo to "Ne",

    StringKey.ArchiveEmpty to "Sprehodov še ni",
    StringKey.ArchiveDeleteWalksButton to "Izbriši sprehode",
    StringKey.ArchiveDeleteConfirmMessage to
        "Ali ste prepričani, da želite trajno izbrisati izbrane sprehode?",
    StringKey.ArchiveDeleteConfirmYes to "Da",
    StringKey.ArchiveDeleteConfirmNo to "Ne",
    StringKey.WalkDetailStartTime to "Začetek",
    StringKey.WalkDetailEndTime to "Konec",
    StringKey.WalkDetailInProgress to "ni končan",
    StringKey.WalkDetailDistance to "Prehojena pot",
    StringKey.WalkDetailDuration to "Trajanje",
    StringKey.WalkDetailAvgSpeed to "Povprečna hitrost",
    StringKey.WalkDetailDurationDays to "d",
    StringKey.WalkDetailDurationHours to "h",
    StringKey.WalkDetailDurationMinutes to "min",
    StringKey.WalkCardDurationHours to "h",
    StringKey.WalkCardDurationMinutes to "min",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Najdbe po vrstah",
    StringKey.WalkDetailFindsEmpty to "Ni zabeleženih najdb",
    StringKey.WalkDetailPlacesTitle to "Označena mesta",
    StringKey.WalkDetailViewMap to "Poglej zemljevid",
    StringKey.WalkDetailEditContentDescription to "Uredi ime sprehoda",
    StringKey.WalkDetailEditWalkNameTitle to "Spremenite ime sprehoda:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Potrdi",
    StringKey.WalkDetailDeleteContentDescription to "Izbriši sprehod",
    StringKey.WalkDetailDeleteConfirmTitle to "Izbrisati sprehod?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Sprehod in vse najdbe bodo trajno izbrisani. Obnovitev ne bo mogoča.",
    StringKey.WalkDetailDeleteConfirmYes to "Da",
    StringKey.WalkDetailDeleteConfirmNo to "Ne",
    StringKey.WalkDetailMushroomsCountZero to "gob",
    StringKey.WalkDetailMushroomsCountOne to "goba",
    StringKey.WalkDetailMushroomsCountTwo to "gobi",
    StringKey.WalkDetailMushroomsCountFew to "gobe",
    StringKey.WalkDetailMushroomsCountMany to "gob",
    StringKey.WalkDetailMushroomsCountOther to "gob",
    StringKey.WalkDetailDescriptionTitle to "Opis",
    StringKey.WalkDetailDescriptionEmpty to "Opis ni dodan",
    StringKey.WalkDetailDescriptionHint to "Opišite sprehod",
    StringKey.WalkDetailEditDescriptionContentDescription to "Uredi opis",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Prekliči",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Shrani",

    StringKey.WalkShareContentDescription to "Deli sprehod",
    StringKey.WalkShareDialogTitle to "Deljenje",
    StringKey.WalkShareOptionName to "Ime sprehoda",
    StringKey.WalkShareOptionStats to "Statistika sprehoda",
    StringKey.WalkShareOptionDescription to "Opis sprehoda",
    StringKey.WalkShareOptionDiagram to "Diagram najdb",
    StringKey.WalkShareOptionMap to "Zemljevid z oznakami",
    StringKey.WalkShareMapWarning to "Drugi ljudje bodo videli, kje ste našli gobe",
    StringKey.WalkShareCancelButton to "Prekliči",
    StringKey.WalkShareConfirmButton to "Deli",
    StringKey.WalkShareFooter to "Ustvarjeno z aplikacijo „Zemljevid gob od Lešija“",
    StringKey.WalkShareImageFooter to "Ustvarjeno v aplikaciji Zemljevid gob od Lešija",

    StringKey.MapToggleMap to "Zemljevid",
    StringKey.MapToggleStats to "Statistika",
    StringKey.MapStatsWalksCount to "Sprehodov",
    StringKey.MapStatsFindsCount to "Najdenih gob",

    StringKey.MapFilterButtonLabel to "Filtri",
    StringKey.MapFilterDialogTitle to
        "Nastavite filtre, ki se uporabijo za gobe na zemljevidu:",
    StringKey.MapFilterBackContentDescription to "Nazaj",
    StringKey.MapFilterDateRangeTitle to "Obdobje",
    StringKey.MapFilterMonthRangeTitle to "Sezona",

    StringKey.MonthJanuary to "Januar",
    StringKey.MonthFebruary to "Februar",
    StringKey.MonthMarch to "Marec",
    StringKey.MonthApril to "April",
    StringKey.MonthMay to "Maj",
    StringKey.MonthJune to "Junij",
    StringKey.MonthJuly to "Julij",
    StringKey.MonthAugust to "Avgust",
    StringKey.MonthSeptember to "September",
    StringKey.MonthOctober to "Oktober",
    StringKey.MonthNovember to "November",
    StringKey.MonthDecember to "December",

    StringKey.BackgroundRecordingChannelName to "Snemanje sprehoda",
    StringKey.BackgroundRecordingNotificationTitle to "Snemanje sprehoda poteka",
    StringKey.BackgroundRecordingNotificationText to
        "Sled se snema v ozadju. Tapnite za vrnitev v aplikacijo.",

    StringKey.DataExportOption to "Izvoz",
    StringKey.DataImportOption to "Uvoz",
    StringKey.DataArchiveNameLabel to "Ime arhiva",
    StringKey.DataChooseFileButton to "Izberi datoteko",
    StringKey.DataFileStatusLabel to "Datoteka za uvoz",
    StringKey.DataFileNotSelected to "ni izbrana",
    StringKey.DataImportLabelFieldLabel to "Pripis k imenom sprehodov",
    StringKey.DataDoneButton to "Končano",
    StringKey.DataSavedButton to "Shranjeno",
    StringKey.DataGoToArchiveButton to "V arhiv",
    StringKey.DataCancelButton to "Prekliči",
    StringKey.DataProcessingLabel to "Obdelava poteka…",
    StringKey.DataExportSuccessMessage to "Arhiv je bil uspešno shranjen",
    StringKey.DataImportedWalksLabel to "Uvoženih sprehodov",
    StringKey.DataImportFailedWalksLabel to "Uvoz ni uspel",
    StringKey.DataErrorLabel to "Napaka",
    StringKey.DataChooseWalksTitle to "Sprehodi za arhiv",
    StringKey.DataWalksBackContentDescription to "Nazaj brez shranjevanja izbire",
    StringKey.DataWalksConfirmContentDescription to "Potrdi izbiro",
    StringKey.DataWalksSelectedLabel to "Izbrano",
    StringKey.DataWalksCountZero to "sprehodov",
    StringKey.DataWalksCountOne to "sprehod",
    StringKey.DataWalksCountTwo to "sprehoda",
    StringKey.DataWalksCountFew to "sprehodi",
    StringKey.DataWalksCountMany to "sprehodov",
    StringKey.DataWalksCountOther to "sprehodov",
    StringKey.PreparationSelectAreaButton to "Prenesi vidno območje",
    StringKey.PreparationDownloadThisAreaButton to "Prenesi to območje",
    StringKey.PreparationRegionNameDialogTitle to "Ime območja",
    StringKey.PreparationRegionNameLabel to "Na primer: Gozd pri vasi",
    StringKey.PreparationSaveButton to "Prenesi",
    StringKey.PreparationCancelButton to "Prekliči",
    StringKey.PreparationDeleteConfirmTitle to "Izbrisati območje?",
    StringKey.PreparationDeleteConfirmMessage to
        "Preneseni deli zemljevida bodo trajno izbrisani.",
    StringKey.PreparationDeleteConfirmYes to "Da",
    StringKey.PreparationDeleteConfirmNo to "Ne",
    StringKey.PreparationDeleteContentDescription to "Izbriši območje",
    StringKey.PreparationPauseContentDescription to "Začasno ustavi prenos",
    StringKey.PreparationResumeContentDescription to "Nadaljuj prenos",
    StringKey.PreparationStatusDownloading to "Prenaša se",
    StringKey.PreparationStatusPaused to "Začasno ustavljeno",
    StringKey.PreparationStatusComplete to "Preneseno",
    StringKey.PreparationStatusError to "Napaka",
    StringKey.PreparationSubtitle to
        "Prenesite vidno območje zemljevida za uporabo brez povezave",
    StringKey.PreparationEstimatedSizeLabel to "Prostor na disku",
    StringKey.PreparationRetryContentDescription to "Ponovi prenos",

    StringKey.MapTilesLoadFailed to "Zemljevid se ni v celoti naložil z",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Zapri obvestilo",

    StringKey.SettingsMapDataTitle to "Podatki zemljevida",
    StringKey.SettingsRefreshMapDataButton to "Posodobi podatke zemljevida",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Posodobiti podatke zemljevida?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Če se je vsebina zemljevida spremenila, bodo vsa prenesena območja za uporabo brez " +
            "povezave prenesena znova. Ali ste prepričani, da želite posodobiti podatke " +
            "zemljevida?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Da",
    StringKey.SettingsMapDataUpdateConfirmNo to "Ne",
    StringKey.SettingsMapDataRefreshError to
        "Posodobitev ni uspela — preverite internetno povezavo",
    StringKey.SettingsMapDataRedownloadingPrefix to
        "Podatki zemljevida so posodobljeni. Znova se prenaša",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— napredek si lahko ogledate v razdelku „Predhodni prenos“.",
    StringKey.SettingsMapDataRegionsCountZero to "območij",
    StringKey.SettingsMapDataRegionsCountOne to "območje",
    StringKey.SettingsMapDataRegionsCountTwo to "območji",
    StringKey.SettingsMapDataRegionsCountFew to "območja",
    StringKey.SettingsMapDataRegionsCountMany to "območij",
    StringKey.SettingsMapDataRegionsCountOther to "območij",
    StringKey.SettingsClearMapCacheButton to "Počisti predpomnilnik zemljevida",
    StringKey.SettingsClearMapCacheConfirmTitle to "Počistiti predpomnilnik zemljevida?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "S čiščenjem predpomnilnika se odstranijo ogledani deli zemljevida, ki niso bili " +
            "shranjeni v razdelku „Predhodni prenos“. Ali ste prepričani, da želite počistiti " +
            "predpomnilnik?",
    StringKey.SettingsClearMapCacheConfirmYes to "Da",
    StringKey.SettingsClearMapCacheConfirmNo to "Ne",
    StringKey.SettingsMapCacheCleared to "Predpomnilnik je počiščen",
)
