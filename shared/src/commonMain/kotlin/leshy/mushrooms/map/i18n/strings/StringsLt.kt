package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Lithuanian — Phase 11 of `.claude/plans/countries-and-languages.md`. Plurals reach
 * `One`/`Few`/`Other` (`Plurals.kt`), and the Lithuanian rule is wider than the Slavic ones it
 * resembles: `Few` runs from 2 all the way to 9, and the **entire 11–19 range** is excluded from
 * both `One` and `Few`, so the teens and every multiple of ten land in `Other` — "21 grybas",
 * "22 grybai", but "11 grybų" and "20 grybų". `Zero` is unreachable (0 has n % 10 = 0 and resolves
 * to `Other`) and repeats the genitive plural; `Two` repeats `Few`, which is what 2 resolves to;
 * `Many` is unreachable for integers (CLDR reserves it for fractions) and repeats `Other`.
 *
 * "Žygis" for a walk: a mushrooming outing is a *žygis* in ordinary Lithuanian, and it keeps the
 * navigation tabs short, where "pasivaikščiojimas" would not fit. Undo is "Anuliuoti" rather than
 * "Atšaukti" so it does not collide with Cancel, which every dialog already uses. */
internal val lithuanianStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Lešio grybų žemėlapis",
    StringKey.NavRecord to "Naujas įrašas",
    StringKey.NavArchive to "Žygių archyvas",
    StringKey.NavMap to "Radinių žemėlapis",
    StringKey.NavData to "Eksportas/Importas",
    StringKey.NavPreparation to "Išankstinis atsisiuntimas",
    StringKey.NavSpecies to "Mano grybai",
    StringKey.SettingsTitle to "Nustatymai",
    StringKey.SettingsContentDescription to "Nustatymai",
    StringKey.SettingsLanguageTitle to "Sąsajos kalba",
    StringKey.SettingsThemeTitle to "Išvaizda",
    StringKey.SettingsThemeLight to "Šviesi",
    StringKey.SettingsThemeDark to "Tamsi",
    StringKey.SettingsThemeSystem to "Sistemos",
    StringKey.SettingsCategoriesTitle to "Žymimi grybai",
    StringKey.SettingsMushroomSizeTitle to "Nustatykite grybų dydį žemėlapyje",
    StringKey.SettingsMushroomSortTitle to "Grybų tvarka",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Atkurti grybų tvarką žygio pabaigoje",
    StringKey.SettingsFreezeMushroomOrder to "Nekintama grybų tvarka",

    StringKey.MushroomImagesDisclaimer to
        "Visi grybų paveikslėliai programėlėje yra tik iliustraciniai — nenaudokite jų " +
            "nepažįstamiems grybams nustatyti!",

    StringKey.SpeciesCollectionsTitle to "Grybų rinkiniai",
    StringKey.SpeciesMyMushroomsTitle to "Pridėti grybai",
    StringKey.SpeciesMyMushroomsEmpty to "Čia atsiras jūsų pačių pridėti grybai",
    StringKey.SpeciesAddButton to "Pridėti grybą",
    StringKey.SpeciesFormTitleCreate to "Naujas grybas",
    StringKey.SpeciesFormTitleEdit to "Redaguoti grybą",
    StringKey.SpeciesFormNameHint to "Pavadinimas",
    StringKey.SpeciesFormScientificNameHint to "Mokslinis pavadinimas",
    StringKey.SpeciesFormColorLabel to "Spalva",
    StringKey.SpeciesFormTakePhotoButton to "Kamera",
    StringKey.SpeciesFormPickPhotoButton to "Galerija",
    StringKey.SpeciesFormPickCatalogButton to "Paveikslėliai",
    StringKey.SpeciesFormSaveButton to "Išsaugoti",
    StringKey.SpeciesFormCancelContentDescription to "Atšaukti",
    StringKey.SpeciesListImportedLabel to "iš archyvo",
    StringKey.SpeciesListEditContentDescription to "Redaguoti",
    StringKey.SpeciesListDeleteContentDescription to "Ištrinti rūšį",
    StringKey.SpeciesDeleteConfirmTitle to "Ištrinti šį grybą?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Ar tikrai norite ištrinti šią rūšį? Visos šios rūšies žymos žygiuose bus perkeltos į " +
            "kategoriją „Nežinomas grybas“. Šio veiksmo atšaukti negalima.",
    StringKey.SpeciesDeleteConfirmYes to "Taip",
    StringKey.SpeciesDeleteConfirmNo to "Ne",

    StringKey.CatalogPhotoPickerTitle to "Pasirinkite paveikslėlį",

    StringKey.IconEditorTitle to "Nuotraukų redaktorius",
    StringKey.IconEditorToolEraser to "Trintukas",
    StringKey.IconEditorToolCrop to "Apkarpymas",
    StringKey.IconEditorShapeRectangle to "Stačiakampis",
    StringKey.IconEditorShapeOval to "Ovalas",
    StringKey.IconEditorBrushSizeLabel to "Teptuko dydis",
    StringKey.IconEditorUndoContentDescription to "Anuliuoti",
    StringKey.IconEditorRedoContentDescription to "Pakartoti",
    StringKey.IconEditorDoneContentDescription to "Atlikta",

    StringKey.OnboardingTitle to "Sveiki atvykę!",
    StringKey.OnboardingDescription to
        "Pasirinkite jus dominančius grybų rinkinius. Tai galėsite pakeisti vėliau Nustatymuose.",
    StringKey.OnboardingContinueButton to "Pradėti",
    StringKey.OnboardingNothingPickedWarning to
        "Norėdami tęsti, pasirinkite bent vieną rinkinį arba vieną grybą",

    StringKey.WelcomeIntro to
        "Programėlė įsimena, kur ėjote ir ką radote, — ir grybaujant padeda išties pastebimai: į gerus " +
            "plotus lengva sugrįžti, o visi radiniai matyti viename žemėlapyje.",
    StringKey.WelcomeRecordTitle to "Įrašykite savo išvyką",
    StringKey.WelcomeRecordText to
        "Pėdsaką, laiką ir kilometrus programėlė veda pati. Radote grybą — pažymėkite jį bakstelėdami jo " +
            "kortelę; šaltinį, nuvirtusį medį ar automobilį pažymėsite tiesiai žemėlapyje.",
    StringKey.WelcomeArchiveTitle to "Grįžkite prie savo radinių",
    StringKey.WelcomeArchiveText to
        "Archyve kiekviena išvyka guli atskirai — su savo pėdsaku ir radiniais. O bendras žemėlapis rodo " +
            "jas visas kartu: kas, kur ir kiek rasta per visus sezonus.",
    StringKey.WelcomeHelpTitle to "Nesate tikri — spauskite „?“",
    StringKey.WelcomeHelpText to
        "Mygtukas „?“ viršuje dešinėje yra kiekviename skyriuje ir paaiškina, kaip tas skyrius sutvarkytas.",
    StringKey.WelcomeMenuTitle to "Visa kita — meniu",
    StringKey.WelcomeMenuText to
        "Meniu mygtukas viršuje kairėje atveria visų programėlės skyrių ir galimybių sąrašą.",
    StringKey.WelcomeConsentTitle to "Prieš pradedant",
    StringKey.WelcomeConsentIntro to "Prieš pereidami prie pačios programėlės turite sutikti su šiais teiginiais:",
    StringKey.WelcomeConsentImages to
        "Nebandysite atpažinti grybų pagal programėlės paveikslėlius. Paveikslėliai atlieka iliustracijų vaidmenį " +
            "ir nėra patikrintas grybų atlasas.",
    StringKey.WelcomeConsentEating to
        "Jokiomis aplinkybėmis nevalgysite grybų, kurių nepažįstate. Grybai gali būti nevalgomi, o gali būti ir " +
            "nuodingi. Geriausia — pasikvieskite ką nors, kas išmano jūsų krašto grybus, kad sužinotumėte, kuriuos " +
            "grybus galima rinkti ir kaip juos paskui reikia paruošti.",
    StringKey.WelcomeConsentWarning to
        "Norint tęsti, reikia sutikti su pirmiau pateiktais teiginiais pažymint langelius prie tų teiginių, su " +
            "kuriais sutinkate",

    StringKey.WelcomeNextButton to "Toliau",

    StringKey.LegalTitle to "Privatumas",
    StringKey.LegalPrivacyText to
        "Jūsų išvykos, žymos ir nuotraukos lieka jūsų įrenginyje. Programėlė nekuria paskyrų ir niekur " +
            "nesiunčia jūsų duomenų — į internetą keliauja tik žemėlapio fragmentų užklausos į " +
            "openfreemap.org.",
    StringKey.LegalPrivacyLink to "Privatumo politika",

    StringKey.AboutTitle to "Apie programėlę",
    StringKey.AboutMapDataTitle to "Žemėlapio duomenys",
    StringKey.AboutMapDataText to
        "Žemėlapis paremtas OpenStreetMap duomenimis, platinamais pagal ODbL licenciją. Vektorines plyteles " +
            "ir stilių teikia OpenMapTiles, pristatymą — paslauga OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "Atvirasis kodas",
    StringKey.AboutOpenSourceText to
        "Programėlė sudėta iš atvirojo kodo bibliotekų. Palietus sąrašo eilutę atsiveria visas jos " +
            "licencijos tekstas.",

    StringKey.NavMenuContentDescription to "Meniu",
    StringKey.HelpContentDescription to "Pagalba",
    StringKey.HelpDialogTitle to "Pagalba",
    StringKey.HelpDialogDismiss to "Supratau",

    StringKey.CategoryMisc to "Įvairūs",
    StringKey.CategoryUnknownMushroom to "Nežinomas grybas",

    StringKey.CollectionPickerSearchHint to "Ieškoti šalies arba grybo",
    StringKey.CollectionPickerMoreMatches to "Rodomi ne visi atitikmenys — patikslinkite užklausą",

    StringKey.LanguagePickerSearchHint to "Ieškoti kalbos",
    StringKey.LanguagePickerBackContentDescription to "Atgal",
    StringKey.LanguagePickerConfirmContentDescription to "Patvirtinti",

    StringKey.DefaultWalkName to "Žygis",
    StringKey.RecordWalkNameHint to "Žygio pavadinimas",
    StringKey.RecordStart to "Pradėti",
    StringKey.RecordPause to "Pauzė",
    StringKey.RecordResume to "Tęsti",
    StringKey.RecordFinish to "Baigti",
    StringKey.RecordSetWalkNameTitle to "Įveskite žygio pavadinimą:",
    StringKey.RecordDefaultWalkNamePrefix to "Žygis",
    StringKey.RecordConfirmWalkNameContentDescription to "Patvirtinti",
    StringKey.RecordMarkLocationContentDescription to "Pažymėti vietą",
    StringKey.RecordLocationUnavailable to "Vietovė nepasiekiama — maršrutas neįrašomas. Įjunkite vietos nustatymo paslaugas ir leiskite programai jomis naudotis įrenginio nustatymuose.",
    StringKey.RecordLocationUnknownMessage to
        "Vieta dar nežinoma — nėra prie ko pririšti žymos. Patikrinkite, ar įjungta vietos nustatymo paslauga, ir palaukite signalo.",
    StringKey.RecordSearchContentDescription to "Paieška",
    StringKey.RecordSearchDialogTitle to "Pasirinkite reikiamą grybą",
    StringKey.RecordBulkAddQuestion to "Kiek naujų grybų rasta?",
    StringKey.RecordBulkAddCancelContentDescription to "Atšaukti",

    StringKey.RecordBulkAddConfirmContentDescription to "Patvirtinti",
    StringKey.RecordBulkAddLimitMessage to
        "Ne daugiau kaip 999 tos pačios rūšies radiniai per vieną žygį.",
    StringKey.DialogAcknowledge to "Supratau",

    StringKey.NavigationDirectionToPrefix to "Kryptis į",
    StringKey.NavigationDistanceToTargetPrefix to "iki tikslo",
    StringKey.NavigationMetersSuffix to "metrų",
    StringKey.NavigationKeepRightPhrase to "laikykitės dešiniau",
    StringKey.NavigationKeepLeftPhrase to "laikykitės kairiau",
    StringKey.NavigationGoStraightPhrase to "eikite tiesiai",
    StringKey.NavigationDeterminingDirection to "Nustatoma kryptis…",
    StringKey.NavigationArrivedPhrase to "Esate vietoje",
    StringKey.NavigationCloseContentDescription to "Uždaryti",

    StringKey.AddPlaceTitle to "Pridėkite vietą",
    StringKey.AddPlaceEditTitle to "Redaguokite vietą",
    StringKey.AddPlaceDefaultName to "Vieta",
    StringKey.AddPlaceNameHint to "Vietos pavadinimas",
    StringKey.AddPlacePhotoContentDescription to "Nufotografuoti",
    StringKey.CameraPermissionDenied to "Nėra prieigos prie kameros. Leiskite ją programai įrenginio nustatymuose.",
    StringKey.AddPlaceDescriptionTitle to "Aprašymas",
    StringKey.AddPlaceDescriptionHint to "Aprašykite vietą",
    StringKey.AddPlaceCoordinatesTitle to "Koordinatės",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Kopijuoti koordinates",
    StringKey.AddPlaceSaveContentDescription to "Išsaugoti vietą",
    StringKey.AddPlaceDiscardContentDescription to "Ištrinti vietą",

    StringKey.PlaceViewEditContentDescription to "Redaguoti vietą",
    StringKey.PlaceViewDeleteContentDescription to "Ištrinti vietą",
    StringKey.PlaceDeleteConfirmTitle to "Ištrinti vietą?",
    StringKey.PlaceDeleteConfirmMessage to
        "Vieta bus negrįžtamai ištrinta. Jos atkurti nebus galima.",
    StringKey.PlaceDeleteConfirmYes to "Taip",
    StringKey.PlaceDeleteConfirmNo to "Ne",

    StringKey.ArchiveEmpty to "Žygių kol kas nėra",
    StringKey.ArchiveEmptyHint to "Čia bus įrašyti žygiai: maršrutas, radiniai ir pažymėtos vietos.",
    StringKey.EmptyStartWalkButton to "Pradėti žygį",
    StringKey.ArchiveDeleteWalksButton to "Ištrinti žygius",
    StringKey.ArchiveDeleteConfirmMessage to
        "Ar tikrai norite negrįžtamai ištrinti pasirinktus žygius?",
    StringKey.ArchiveDeleteConfirmYes to "Taip",
    StringKey.ArchiveDeleteConfirmNo to "Ne",
    StringKey.WalkDetailStartTime to "Pradžia",
    StringKey.WalkDetailEndTime to "Pabaiga",
    StringKey.WalkDetailInProgress to "nebaigtas",
    StringKey.WalkDetailDistance to "Nueitas kelias",
    StringKey.WalkDetailDuration to "Trukmė",
    StringKey.WalkDetailAvgSpeed to "Vidutinis greitis",
    StringKey.WalkDetailDurationDays to "d",
    StringKey.WalkDetailDurationHours to "val",
    StringKey.WalkDetailDurationMinutes to "min",
    StringKey.WalkCardDurationHours to "val",
    StringKey.WalkCardDurationMinutes to "min",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Radiniai pagal rūšis",
    StringKey.WalkDetailFindsEmpty to "Radinių neužfiksuota",
    StringKey.WalkDetailPlacesTitle to "Pažymėtos vietos",
    StringKey.WalkDetailViewMap to "Žiūrėti žemėlapį",
    StringKey.WalkDetailEditContentDescription to "Redaguoti žygio pavadinimą",
    StringKey.WalkDetailEditWalkNameTitle to "Pakeiskite žygio pavadinimą:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Patvirtinti",
    StringKey.WalkDetailDeleteContentDescription to "Ištrinti žygį",
    StringKey.WalkDetailShareAction to "Bendrinti",
    StringKey.WalkDetailDeleteAction to "Ištrinti",
    StringKey.WalkDetailDeleteConfirmTitle to "Ištrinti žygį?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Žygis ir visi radiniai bus negrįžtamai ištrinti. Jų atkurti nebus galima.",
    StringKey.WalkDetailDeleteConfirmYes to "Taip",
    StringKey.WalkDetailDeleteConfirmNo to "Ne",
    StringKey.WalkDetailMushroomsCountZero to "grybų",
    StringKey.WalkDetailMushroomsCountOne to "grybas",
    StringKey.WalkDetailMushroomsCountTwo to "grybai",
    StringKey.WalkDetailMushroomsCountFew to "grybai",
    StringKey.WalkDetailMushroomsCountMany to "grybų",
    StringKey.WalkDetailMushroomsCountOther to "grybų",
    StringKey.WalkDetailDescriptionTitle to "Aprašymas",
    StringKey.WalkDetailDescriptionEmpty to "Aprašymas nepridėtas",
    StringKey.WalkDetailDescriptionHint to "Aprašykite žygį",
    StringKey.WalkDetailEditDescriptionContentDescription to "Redaguoti aprašymą",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Atšaukti",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Išsaugoti",

    StringKey.WalkShareContentDescription to "Dalintis žygiu",
    StringKey.WalkShareDialogTitle to "Dalijimasis",
    StringKey.WalkShareOptionName to "Žygio pavadinimas",
    StringKey.WalkShareOptionStats to "Žygio statistika",
    StringKey.WalkShareOptionDescription to "Žygio aprašymas",
    StringKey.WalkShareOptionDiagram to "Radinių diagrama",
    StringKey.WalkShareOptionMap to "Žemėlapis su žymomis",
    StringKey.WalkShareMapWarning to "Kiti žmonės matys, kur radote grybų",
    StringKey.WalkShareCancelButton to "Atšaukti",
    StringKey.WalkShareConfirmButton to "Dalintis",
    StringKey.WalkShareFooter to "Sukurta naudojant programėlę „Lešio grybų žemėlapis“",
    StringKey.WalkShareImageFooter to "Sukurta programėlėje Lešio grybų žemėlapis",

    StringKey.MapStatsTitle to "Statistika",
    StringKey.MapStatsWalksCount to "Žygių",
    StringKey.MapStatsFindsCount to "Rasta grybų",
    StringKey.MapStatsEmptyHint to "Statistika susidarys pati, kai tik bus įrašytas pirmasis žygis.",

    StringKey.MapFilterButtonLabel to "Filtrai",
    StringKey.MapFilterDialogTitle to
        "Nustatykite filtrus, taikomus grybams žemėlapyje:",
    StringKey.MapFilterBackContentDescription to "Atgal",
    StringKey.MapFilterDateRangeTitle to "Datų intervalas",
    StringKey.MapFilterMonthRangeTitle to "Sezonas",
    StringKey.MapFilterPastRoutesTitle to "Ankstesnių maršrutų rodymas",
    StringKey.MapFilterShowPastRoutes to "Rodyti ankstesnius maršrutus",

    StringKey.MonthJanuary to "Sausis",
    StringKey.MonthFebruary to "Vasaris",
    StringKey.MonthMarch to "Kovas",
    StringKey.MonthApril to "Balandis",
    StringKey.MonthMay to "Gegužė",
    StringKey.MonthJune to "Birželis",
    StringKey.MonthJuly to "Liepa",
    StringKey.MonthAugust to "Rugpjūtis",
    StringKey.MonthSeptember to "Rugsėjis",
    StringKey.MonthOctober to "Spalis",
    StringKey.MonthNovember to "Lapkritis",
    StringKey.MonthDecember to "Gruodis",

    StringKey.BackgroundRecordingChannelName to "Žygio įrašymas",
    StringKey.BackgroundRecordingNotificationTitle to "Vyksta žygio įrašymas",
    StringKey.BackgroundRecordingNotificationText to
        "Maršrutas įrašomas fone. Bakstelėkite, kad grįžtumėte į programėlę.",

    StringKey.DataExportOption to "Eksportas",
    StringKey.DataImportOption to "Importas",
    StringKey.DataArchiveNameLabel to "Archyvo pavadinimas",
    StringKey.DataChooseFileButton to "Pasirinkti failą",
    StringKey.DataFileStatusLabel to "Importuojamas failas",
    StringKey.DataFileNotSelected to "nepasirinktas",
    StringKey.DataImportLabelFieldLabel to "Prierašas prie žygių pavadinimų",
    StringKey.DataDoneButton to "Atlikta",
    StringKey.DataSavedButton to "Išsaugota",
    StringKey.DataGoToArchiveButton to "Į archyvą",
    StringKey.DataCancelButton to "Atšaukti",
    StringKey.DataProcessingLabel to "Vyksta apdorojimas…",
    StringKey.DataExportSuccessMessage to "Archyvas sėkmingai išsaugotas",
    StringKey.DataImportedWalksLabel to "Importuota žygių",
    StringKey.DataImportFailedWalksLabel to "Importuoti nepavyko",
    StringKey.DataErrorLabel to "Klaida",
    StringKey.DataImportRejectedTitle to "Šio failo importuoti negalima",
    StringKey.DataImportRejectedNotArchive to "Tai ne archyvas: failo nepavyksta perskaityti kaip ZIP. Pasirinkite iš Leshy eksportuotą archyvą.",
    StringKey.DataImportRejectedNotLeshy to "Tai ZIP archyvas, bet ne Leshy: jame nėra manifest.json.",
    StringKey.DataImportRejectedNewerFormat to "Archyvą sukūrė naujesnė programos versija. Atnaujinkite programą ir bandykite dar kartą.",
    StringKey.DataImportRejectedDamaged to "Archyvas sugadintas: dalies turinio nepavyksta perskaityti. Nieko neimportuota.",
    StringKey.DataImportRejectedNoWalks to "Archyve nėra nė vieno pasivaikščiojimo — nėra ko importuoti.",
    StringKey.DataChooseWalksTitle to "Žygiai archyvui",
    StringKey.DataWalksBackContentDescription to "Atgal neišsaugant pasirinkimo",
    StringKey.DataWalksConfirmContentDescription to "Patvirtinti pasirinkimą",
    StringKey.DataWalksSelectedLabel to "Pasirinkta",
    StringKey.DataWalksCountZero to "žygių",
    StringKey.DataWalksCountOne to "žygis",
    StringKey.DataWalksCountTwo to "žygiai",
    StringKey.DataWalksCountFew to "žygiai",
    StringKey.DataWalksCountMany to "žygių",
    StringKey.DataWalksCountOther to "žygių",
    StringKey.PreparationSelectAreaButton to "Atsisiųsti matomą sritį",
    StringKey.PreparationDownloadThisAreaButton to "Atsisiųsti šią sritį",
    StringKey.PreparationRegionNameDialogTitle to "Srities pavadinimas",
    StringKey.PreparationRegionNameLabel to "Pvz.: Miškas prie kaimo",
    StringKey.PreparationSaveButton to "Atsisiųsti",
    StringKey.PreparationCancelButton to "Atšaukti",
    StringKey.PreparationDeleteConfirmTitle to "Ištrinti sritį?",
    StringKey.PreparationDeleteConfirmMessage to
        "Atsisiųstos žemėlapio plytelės bus negrįžtamai ištrintos.",
    StringKey.PreparationDeleteConfirmYes to "Taip",
    StringKey.PreparationDeleteConfirmNo to "Ne",
    StringKey.PreparationDeleteContentDescription to "Ištrinti sritį",
    StringKey.PreparationPauseContentDescription to "Pristabdyti atsisiuntimą",
    StringKey.PreparationResumeContentDescription to "Tęsti atsisiuntimą",
    StringKey.PreparationStatusDownloading to "Atsiunčiama",
    StringKey.PreparationStatusPaused to "Pristabdyta",
    StringKey.PreparationStatusComplete to "Atsiųsta",
    StringKey.PreparationStatusError to "Klaida",
    StringKey.PreparationSubtitle to
        "Atsisiųskite matomą žemėlapio sritį, kad galėtumėte ja naudotis neprisijungę",
    StringKey.PreparationRetryContentDescription to "Kartoti atsisiuntimą",

    StringKey.MapTilesLoadFailed to "Žemėlapis ne visas įkeltas iš",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Uždaryti pranešimą",

    StringKey.SettingsMapDataTitle to "Žemėlapio duomenys",
    StringKey.SettingsRefreshMapDataButton to "Atnaujinti žemėlapio duomenis",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Atnaujinti žemėlapio duomenis?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Jei žemėlapio turinys pasikeitė, visos atsisiųstos neprisijungus naudojamos sritys bus " +
            "atsiųstos iš naujo. Ar tikrai norite atnaujinti žemėlapio duomenis?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Taip",
    StringKey.SettingsMapDataUpdateConfirmNo to "Ne",
    StringKey.SettingsMapDataRefreshError to
        "Atnaujinti nepavyko — patikrinkite interneto ryšį",
    StringKey.SettingsMapDataRedownloadingPrefix to
        "Žemėlapio duomenys atnaujinti. Iš naujo atsiunčiama",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— eigą galite matyti skiltyje „Išankstinis atsisiuntimas“.",
    StringKey.SettingsMapDataRegionsCountZero to "sričių",
    StringKey.SettingsMapDataRegionsCountOne to "sritis",
    StringKey.SettingsMapDataRegionsCountTwo to "sritys",
    StringKey.SettingsMapDataRegionsCountFew to "sritys",
    StringKey.SettingsMapDataRegionsCountMany to "sričių",
    StringKey.SettingsMapDataRegionsCountOther to "sričių",
    StringKey.SettingsClearMapCacheButton to "Išvalyti žemėlapio podėlį",
    StringKey.SettingsClearMapCacheConfirmTitle to "Išvalyti žemėlapio podėlį?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Išvalius podėlį bus pašalintos peržiūrėtos žemėlapio dalys, kurios nebuvo išsaugotos " +
            "skiltyje „Išankstinis atsisiuntimas“. Ar tikrai norite išvalyti podėlį?",
    StringKey.SettingsClearMapCacheConfirmYes to "Taip",
    StringKey.SettingsClearMapCacheConfirmNo to "Ne",
    StringKey.SettingsMapCacheCleared to "Podėlis išvalytas",
)
