package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Hungarian — Phase 10 of `.claude/plans/countries-and-languages.md`. `pluralCategory` gives `hu`
 * the plain two-way split (`One` at n = 1, else `Other`), but **Hungarian nouns stay singular after
 * any numeral** — "1 gomba", "5 gomba", never "5 gombák" — so all six forms of each unit carry the
 * same word. That is deliberate, exactly as it was for `tr` in Phase 8 and `ja`/`ko` before it, not
 * an unfilled placeholder: the counted noun genuinely never changes shape.
 *
 * Note also `RecordDefaultWalkNamePrefix`: the UI concatenates prefix + date, while natural
 * Hungarian puts the date first ("2026.08.25-i séta"). The prefix is therefore written as a label
 * with a colon ("Séta:"), which reads correctly in the fixed order the UI imposes. */
internal val hungarianStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Leshy gombatérképe",
    StringKey.NavRecord to "Új felvétel",
    StringKey.NavArchive to "Séták archívuma",
    StringKey.NavMap to "Leletek térképe",
    StringKey.NavData to "Exportálás/Importálás",
    StringKey.NavPreparation to "Előzetes letöltés",
    StringKey.NavSpecies to "Gombáim",
    StringKey.SettingsTitle to "Beállítások",
    StringKey.SettingsContentDescription to "Beállítások",
    StringKey.SettingsLanguageTitle to "A felület nyelve",
    StringKey.SettingsThemeTitle to "Megjelenés",
    StringKey.SettingsThemeLight to "Világos",
    StringKey.SettingsThemeDark to "Sötét",
    StringKey.SettingsThemeSystem to "Rendszer",
    StringKey.SettingsCategoriesTitle to "Jelölhető gombák",
    StringKey.SettingsMushroomSizeTitle to "Állítsa be a gombák méretét a térképen",
    StringKey.SettingsMushroomSortTitle to "Gombák sorrendje",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Gombák sorrendjének visszaállítása a séta végén",
    StringKey.SettingsFreezeMushroomOrder to "Rögzített gombasorrend",

    StringKey.MushroomImagesDisclaimer to
        "Az alkalmazásban látható gombaképek csak illusztrációk — ne használja őket ismeretlen " +
            "gombák meghatározására!",

    StringKey.SpeciesCollectionsTitle to "Gombagyűjtemények országok szerint",
    StringKey.SpeciesMyMushroomsTitle to "Hozzáadott gombák",
    StringKey.SpeciesMyMushroomsEmpty to "Itt jelennek meg a saját maga által hozzáadott gombák",
    StringKey.SpeciesAddButton to "Gomba hozzáadása",
    StringKey.SpeciesFormTitleCreate to "Új gomba",
    StringKey.SpeciesFormTitleEdit to "Gomba szerkesztése",
    StringKey.SpeciesFormNameHint to "Név",
    StringKey.SpeciesFormScientificNameHint to "Tudományos név",
    StringKey.SpeciesFormColorLabel to "Szín",
    StringKey.SpeciesFormTakePhotoButton to "Kamera",
    StringKey.SpeciesFormPickPhotoButton to "Galéria",
    StringKey.SpeciesFormPickCatalogButton to "Képek",
    StringKey.SpeciesFormSaveButton to "Mentés",
    StringKey.SpeciesFormCancelContentDescription to "Mégse",
    StringKey.SpeciesCollectionDialogTitle to "Melyik gyűjteménybe?",
    StringKey.SpeciesCollectionDialogBackContentDescription to "Vissza",
    StringKey.SpeciesCollectionDialogSaveContentDescription to "Mentés a gyűjteménybe",
    StringKey.SpeciesCollectionNameIsCountry to "Ez egy ország neve — válassz másikat",
    StringKey.SpeciesListImportedLabel to "archívumból",
    StringKey.SpeciesListEditContentDescription to "Szerkesztés",
    StringKey.SpeciesListDeleteContentDescription to "Faj törlése",
    StringKey.SpeciesDeleteConfirmTitle to "Törli ezt a gombát?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Biztosan törli ezt a fajt? A séták során rögzített összes jelölése az „Ismeretlen " +
            "gomba“ kategóriába kerül. A művelet nem vonható vissza.",
    StringKey.SpeciesDeleteConfirmYes to "Igen",
    StringKey.SpeciesDeleteConfirmNo to "Nem",

    StringKey.CatalogPhotoPickerTitle to "Válasszon képet",

    StringKey.IconEditorTitle to "Fotószerkesztő",
    StringKey.IconEditorToolEraser to "Radír",
    StringKey.IconEditorToolCrop to "Vágás",
    StringKey.IconEditorShapeRectangle to "Téglalap",
    StringKey.IconEditorShapeOval to "Ovális",
    StringKey.IconEditorBrushSizeLabel to "Ecsetméret",
    StringKey.IconEditorUndoContentDescription to "Visszavonás",
    StringKey.IconEditorRedoContentDescription to "Újra",
    StringKey.IconEditorDoneContentDescription to "Kész",

    StringKey.OnboardingTitle to "Üdvözöljük!",
    StringKey.OnboardingDescription to
        "Válassza ki az Önt érdeklő gombagyűjteményeket. Ezt később a Beállításokban " +
            "módosíthatja.",
    StringKey.OnboardingContinueButton to "Kezdés",
    StringKey.OnboardingNothingPickedWarning to
        "A folytatáshoz válasszon ki legalább egy gyűjteményt vagy egy gombát",

    StringKey.WelcomeIntro to
        "Az alkalmazás megjegyzi, merre járt és mit talált — és érezhetően segít a gombászásban: a jó " +
            "helyekre könnyű visszatérni, minden lelet pedig egyetlen térképen látszik.",
    StringKey.WelcomeRecordTitle to "Rögzítse a sétát",
    StringKey.WelcomeRecordText to
        "A nyomvonalat, az időt és a kilométereket az alkalmazás magától vezeti. Talált egy gombát — " +
            "érintse meg a hozzá tartozó kártyát; forrást, kidőlt fát vagy az autóját közvetlenül a térképen " +
            "jelölheti.",
    StringKey.WelcomeArchiveTitle to "Térjen vissza a leleteihez",
    StringKey.WelcomeArchiveText to
        "Az archívumban minden séta külön áll — saját nyomvonallal és leletekkel. A közös térkép pedig " +
            "együtt mutatja mindet: mit, hol és mennyit talált az összes szezonban.",
    StringKey.WelcomeHelpTitle to "Nem biztos benne? Érintse meg a „?” gombot",
    StringKey.WelcomeHelpText to
        "A „?” gomb a jobb felső sarokban minden szakaszban ott van, és elmagyarázza, hogyan épül fel az " +
            "adott szakasz.",
    StringKey.WelcomeMenuTitle to "A többi a menüben van",
    StringKey.WelcomeMenuText to
        "A bal felső sarokban lévő menügomb megnyitja az alkalmazás összes szakaszának és lehetőségének " +
            "listáját.",
    StringKey.WelcomeConsentTitle to "Mielőtt hozzákezd",
    StringKey.WelcomeConsentIntro to
        "Mielőtt továbblépne magára az alkalmazásra, el kell fogadnia az alábbi állításokat:",
    StringKey.WelcomeConsentImages to
        "Nem fogja megpróbálni az alkalmazás képei alapján meghatározni a gombákat. A képek illusztrációk, nem " +
            "pedig ellenőrzött gombahatározó.",
    StringKey.WelcomeConsentEating to
        "Semmilyen körülmények között nem eszik meg olyan gombát, amelyet nem ismer. A gombák lehetnek ehetetlenek, " +
            "sőt mérgezőek is. A legjobb — hívjon valakit, aki ért a környék gombáihoz, hogy megtudja, mely gombák " +
            "szedhetők, és hogyan kell azokat utána elkészíteni.",
    StringKey.WelcomeConsentBattery to
        "A séták során be fogja tartani a szükséges biztonsági szabályokat, és számol azzal, hogy futó alkalmazás " +
            "mellett a telefon gyorsabban lemerül. Alacsony töltöttségnél jobb leállítani a séta rögzítését és " +
            "bezárni az alkalmazást.",
    StringKey.WelcomeConsentWarning to
        "A folytatáshoz el kell fogadnia a fenti állításokat: tegyen pipát azon állítások elé, amelyekkel egyetért",

    StringKey.WelcomeNextButton to "Tovább",

    StringKey.LegalTitle to "Adatvédelem",
    StringKey.LegalPrivacyText to
        "A séták, a jelölések és a fényképek az Ön készülékén maradnak. Az alkalmazás nem hoz létre fiókot, " +
            "és sehová nem küldi el az adatait — az internetre csak a térképszelvények kérése megy ki az " +
            "openfreemap.org felé.",
    StringKey.LegalPrivacyLink to "Adatvédelmi tájékoztató",

    StringKey.AboutTitle to "Az alkalmazásról",
    StringKey.AboutMapDataTitle to "Térképadatok",
    StringKey.AboutMapDataText to
        "A térkép az OpenStreetMap adataira épül, amelyeket ODbL licenc alatt terjesztenek. A vektorcsempék " +
            "és a stílus az OpenMapTilestól származnak, kézbesítésüket az OpenFreeMap szolgáltatás végzi.",
    StringKey.AboutOpenSourceTitle to "Nyílt forráskód",
    StringKey.AboutOpenSourceText to
        "Az alkalmazás nyílt forráskódú programkönyvtárakból áll. A lista egy sorára koppintva megnyílik a " +
            "licencének teljes szövege.",

    StringKey.NavMenuContentDescription to "Menü",
    StringKey.HelpContentDescription to "Súgó",
    StringKey.HelpDialogTitle to "Súgó",
    StringKey.HelpDialogDismiss to "Értem",

    StringKey.CategoryMisc to "Egyéb",
    StringKey.CategoryUnknownMushroom to "Ismeretlen gomba",

    StringKey.CollectionOtherName to "Egyéb",
    StringKey.CollectionPickerSearchHint to "Gyűjtemény vagy gomba keresése",
    StringKey.CollectionPickerMoreMatches to "Nem látható minden találat — pontosítsa a keresést",

    StringKey.LanguagePickerSearchHint to "Nyelv keresése",
    StringKey.LanguagePickerBackContentDescription to "Vissza",
    StringKey.LanguagePickerConfirmContentDescription to "Megerősítés",

    StringKey.DefaultWalkName to "Séta",
    StringKey.RecordWalkNameHint to "A séta neve",
    StringKey.RecordStart to "Indítás",
    StringKey.RecordPause to "Szünet",
    StringKey.RecordResume to "Folytatás",
    StringKey.RecordFinish to "Befejezés",
    StringKey.RecordSetWalkNameTitle to "Adja meg a séta nevét:",
    StringKey.RecordDefaultWalkNamePrefix to "Séta:",
    StringKey.RecordConfirmWalkNameContentDescription to "Elfogadás",
    StringKey.RecordMarkLocationContentDescription to "Hely megjelölése",
    StringKey.RecordLocationUnavailable to "A helyadatok nem érhetők el — az útvonal nem kerül rögzítésre. Kapcsold be a helymeghatározást, és engedélyezd a hozzáférést a készülék beállításaiban.",
    StringKey.RecordLocationUnknownMessage to
        "A helyzeted még nem ismert — nincs mihez kötni a jelölést. Ellenőrizd, hogy a helymeghatározás be van-e kapcsolva, és várj a jelre.",
    StringKey.RecordSearchContentDescription to "Keresés",
    StringKey.RecordSearchDialogTitle to "Válassza ki a keresett gombát",
    StringKey.RecordBulkAddQuestion to "Hány új gombát talált?",
    StringKey.RecordBulkAddCancelContentDescription to "Mégse",

    StringKey.RecordBulkAddConfirmContentDescription to "Elfogadás",
    StringKey.RecordBulkAddLimitMessage to
        "Sétánként legfeljebb 999 azonos fajú lelet.",
    StringKey.DialogAcknowledge to "Értem",

    StringKey.NavigationDirectionToPrefix to "Irány:",
    StringKey.NavigationDistanceToTargetPrefix to "a célig",
    StringKey.NavigationMetersSuffix to "méter",
    StringKey.NavigationKeepRightPhrase to "tartson jobbra",
    StringKey.NavigationKeepLeftPhrase to "tartson balra",
    StringKey.NavigationGoStraightPhrase to "menjen egyenesen",
    StringKey.NavigationDeterminingDirection to "Irány meghatározása…",
    StringKey.NavigationArrivedPhrase to "Megérkezett",
    StringKey.NavigationCloseContentDescription to "Bezárás",

    StringKey.AddPlaceTitle to "Adjon hozzá egy helyet",
    StringKey.AddPlaceEditTitle to "Szerkessze a helyet",
    StringKey.AddPlaceDefaultName to "Hely",
    StringKey.AddPlaceNameHint to "A hely neve",
    StringKey.AddPlacePhotoContentDescription to "Fénykép készítése",
    StringKey.CameraPermissionDenied to "Nincs hozzáférés a kamerához. Engedélyezd az alkalmazásnak a készülék beállításaiban.",
    StringKey.AddPlaceDescriptionTitle to "Leírás",
    StringKey.AddPlaceDescriptionHint to "Írja le a helyet",
    StringKey.AddPlaceCoordinatesTitle to "Koordináták",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Koordináták másolása",
    StringKey.AddPlaceSaveContentDescription to "Hely mentése",
    StringKey.AddPlaceDiscardContentDescription to "Hely törlése",

    StringKey.PlaceViewEditContentDescription to "Hely szerkesztése",
    StringKey.PlaceViewDeleteContentDescription to "Hely törlése",
    StringKey.PlaceDeleteConfirmTitle to "Törli a helyet?",
    StringKey.PlaceDeleteConfirmMessage to
        "A hely véglegesen törlődik. A visszaállítása nem lesz lehetséges.",
    StringKey.PlaceDeleteConfirmYes to "Igen",
    StringKey.PlaceDeleteConfirmNo to "Nem",

    StringKey.ArchiveEmpty to "Még nincsenek séták",
    StringKey.ArchiveEmptyHint to
        "A rögzített séták itt jelennek meg: az útvonal, a talált gombák és a megjelölt helyek.",
    StringKey.EmptyStartWalkButton to "Séta indítása",
    StringKey.ArchiveDeleteWalksButton to "Séták törlése",
    StringKey.ArchiveDeleteConfirmMessage to
        "Biztosan véglegesen törli a kijelölt sétákat?",
    StringKey.ArchiveDeleteConfirmYes to "Igen",
    StringKey.ArchiveDeleteConfirmNo to "Nem",
    StringKey.WalkDetailStartTime to "Kezdés",
    StringKey.WalkDetailEndTime to "Befejezés",
    StringKey.WalkDetailInProgress to "nincs befejezve",
    StringKey.WalkDetailDistance to "Távolság",
    StringKey.WalkDetailDuration to "Időtartam",
    StringKey.WalkDetailAvgSpeed to "Átlagsebesség",
    StringKey.WalkDetailDurationDays to "n",
    StringKey.WalkDetailDurationHours to "ó",
    StringKey.WalkDetailDurationMinutes to "p",
    StringKey.WalkCardDurationHours to "ó",
    StringKey.WalkCardDurationMinutes to "p",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Leletek típus szerint",
    StringKey.WalkDetailFindsEmpty to "Nincs rögzített lelet",
    StringKey.WalkDetailPlacesTitle to "Megjelölt helyek",
    StringKey.WalkDetailViewMap to "Térkép megtekintése",
    StringKey.WalkDetailEditContentDescription to "A séta nevének szerkesztése",
    StringKey.WalkDetailEditWalkNameTitle to "Módosítsa a séta nevét:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Elfogadás",
    StringKey.WalkDetailDeleteContentDescription to "Séta törlése",
    StringKey.WalkDetailShareAction to "Megosztás",
    StringKey.WalkDetailDeleteAction to "Törlés",
    StringKey.WalkDetailDeleteConfirmTitle to "Törli a sétát?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "A séta és az összes lelet véglegesen törlődik. A visszaállításuk nem lesz lehetséges.",
    StringKey.WalkDetailDeleteConfirmYes to "Igen",
    StringKey.WalkDetailDeleteConfirmNo to "Nem",
    StringKey.WalkDetailMushroomsCountZero to "gomba",
    StringKey.WalkDetailMushroomsCountOne to "gomba",
    StringKey.WalkDetailMushroomsCountTwo to "gomba",
    StringKey.WalkDetailMushroomsCountFew to "gomba",
    StringKey.WalkDetailMushroomsCountMany to "gomba",
    StringKey.WalkDetailMushroomsCountOther to "gomba",
    StringKey.WalkDetailDescriptionTitle to "Leírás",
    StringKey.WalkDetailDescriptionEmpty to "Nincs hozzáadott leírás",
    StringKey.WalkDetailDescriptionHint to "Írja le a sétát",
    StringKey.WalkDetailEditDescriptionContentDescription to "Leírás szerkesztése",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Mégse",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Mentés",

    StringKey.WalkShareContentDescription to "Séta megosztása",
    StringKey.WalkShareDialogTitle to "Megosztás",
    StringKey.WalkShareOptionName to "A séta neve",
    StringKey.WalkShareOptionStats to "A séta statisztikái",
    StringKey.WalkShareOptionDescription to "A séta leírása",
    StringKey.WalkShareOptionDiagram to "Leletek diagramja",
    StringKey.WalkShareOptionMap to "Térkép jelölésekkel",
    StringKey.WalkShareMapWarning to "Mások is látni fogják, hol talált gombát",
    StringKey.WalkShareCancelButton to "Mégse",
    StringKey.WalkShareConfirmButton to "Megosztás",
    StringKey.WalkShareFooter to "A „Leshy gombatérképe“ alkalmazással készült",
    StringKey.WalkShareImageFooter to "A Leshy gombatérképe alkalmazásban készült",

    StringKey.MapStatsTitle to "Statisztika",
    StringKey.MapStatsWalksCount to "Séták",
    StringKey.MapStatsFindsCount to "Talált gombák",
    StringKey.MapStatsEmptyHint to "A statisztika magától összeáll, amint elkészül az első rögzített séta.",

    StringKey.MapFilterButtonLabel to "Szűrők",
    StringKey.MapFilterDialogTitle to
        "Állítsa be a térképen látható gombákra alkalmazott szűrőket:",
    StringKey.MapFilterBackContentDescription to "Vissza",
    StringKey.MapFilterDateRangeTitle to "Időszak",
    StringKey.MapFilterMonthRangeTitle to "Szezon",
    StringKey.MapFilterPastRoutesTitle to "Korábbi útvonalak megjelenítése",
    StringKey.MapFilterShowPastRoutes to "Korábbi útvonalak mutatása",

    StringKey.MonthJanuary to "Január",
    StringKey.MonthFebruary to "Február",
    StringKey.MonthMarch to "Március",
    StringKey.MonthApril to "Április",
    StringKey.MonthMay to "Május",
    StringKey.MonthJune to "Június",
    StringKey.MonthJuly to "Július",
    StringKey.MonthAugust to "Augusztus",
    StringKey.MonthSeptember to "Szeptember",
    StringKey.MonthOctober to "Október",
    StringKey.MonthNovember to "November",
    StringKey.MonthDecember to "December",

    StringKey.BackgroundRecordingChannelName to "Séta rögzítése",
    StringKey.BackgroundRecordingNotificationTitle to "A séta rögzítése folyamatban",
    StringKey.BackgroundRecordingNotificationText to
        "A nyomvonal rögzítése a háttérben folyik. Koppintson az alkalmazáshoz való " +
            "visszatéréshez.",

    StringKey.DataExportOption to "Exportálás",
    StringKey.DataImportOption to "Importálás",
    StringKey.DataArchiveNameLabel to "Az archívum neve",
    StringKey.DataChooseFileButton to "Fájl kiválasztása",
    StringKey.DataFileStatusLabel to "Importálandó fájl",
    StringKey.DataFileNotSelected to "nincs kiválasztva",
    StringKey.DataImportLabelFieldLabel to "A séták nevéhez fűzött megjegyzés",
    StringKey.DataDoneButton to "Kész",
    StringKey.DataSavedButton to "Mentve",
    StringKey.DataGoToArchiveButton to "Az archívumba",
    StringKey.DataCancelButton to "Mégse",
    StringKey.DataProcessingLabel to "Feldolgozás folyamatban…",
    StringKey.DataExportSuccessMessage to "Az archívum mentése sikerült",
    StringKey.DataImportedWalksLabel to "Importált séták",
    StringKey.DataImportFailedWalksLabel to "Az importálás nem sikerült",
    StringKey.DataErrorLabel to "Hiba",
    StringKey.DataImportRejectedTitle to "Ezt a fájlt nem lehet importálni",
    StringKey.DataImportRejectedNotArchive to "Ez nem archívum: a fájl nem olvasható ZIP-ként. Válassz a Leshyből exportált archívumot.",
    StringKey.DataImportRejectedNotLeshy to "Ez ZIP archívum, de nem a Leshyé: nincs benne manifest.json.",
    StringKey.DataImportRejectedNewerFormat to "Az archívumot az alkalmazás újabb verziója készítette. Frissítsd az alkalmazást, és próbáld újra.",
    StringKey.DataImportRejectedDamaged to "Az archívum sérült: a tartalom egy része nem olvasható. Semmi nem lett importálva.",
    StringKey.DataImportRejectedNoWalks to "Az archívum egyetlen túrát sem tartalmaz — nincs mit importálni.",
    StringKey.DataChooseWalksTitle to "Archiválandó séták",
    StringKey.DataWalksBackContentDescription to "Vissza a kijelölés mentése nélkül",
    StringKey.DataWalksConfirmContentDescription to "Kijelölés megerősítése",
    StringKey.DataWalksSelectedLabel to "Kiválasztva",
    StringKey.DataWalksCountZero to "séta",
    StringKey.DataWalksCountOne to "séta",
    StringKey.DataWalksCountTwo to "séta",
    StringKey.DataWalksCountFew to "séta",
    StringKey.DataWalksCountMany to "séta",
    StringKey.DataWalksCountOther to "séta",
    StringKey.PreparationSelectAreaButton to "Látható terület letöltése",
    StringKey.PreparationDownloadThisAreaButton to "Ezen terület letöltése",
    StringKey.PreparationRegionNameDialogTitle to "A terület neve",
    StringKey.PreparationRegionNameLabel to "Például: Erdő a falu mellett",
    StringKey.PreparationSaveButton to "Letöltés",
    StringKey.PreparationCancelButton to "Mégse",
    StringKey.PreparationDeleteConfirmTitle to "Törli a területet?",
    StringKey.PreparationDeleteConfirmMessage to
        "A letöltött térképcsempék véglegesen törlődnek.",
    StringKey.PreparationDeleteConfirmYes to "Igen",
    StringKey.PreparationDeleteConfirmNo to "Nem",
    StringKey.PreparationDeleteContentDescription to "Terület törlése",
    StringKey.PreparationPauseContentDescription to "Letöltés szüneteltetése",
    StringKey.PreparationResumeContentDescription to "Letöltés folytatása",
    StringKey.PreparationStatusDownloading to "Letöltés alatt",
    StringKey.PreparationStatusPaused to "Szüneteltetve",
    StringKey.PreparationStatusComplete to "Letöltve",
    StringKey.PreparationStatusError to "Hiba",
    StringKey.PreparationSubtitle to
        "Töltse le a térkép látható területét, hogy internet nélkül is használhassa",
    StringKey.PreparationRetryContentDescription to "Letöltés újrapróbálása",

    StringKey.MapTilesLoadFailed to "A térkép nem töltődött be teljesen innen:",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Értesítés bezárása",

    StringKey.SettingsMapDataTitle to "Térképadatok",
    StringKey.SettingsRefreshMapDataButton to "Térképadatok frissítése",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Frissíti a térképadatokat?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Ha a térkép tartalma megváltozott, az összes letöltött offline terület újra letöltődik. " +
            "Biztosan frissíti a térképadatokat?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Igen",
    StringKey.SettingsMapDataUpdateConfirmNo to "Nem",
    StringKey.SettingsMapDataRefreshError to
        "A frissítés nem sikerült — ellenőrizze az internetkapcsolatot",
    StringKey.SettingsMapDataRedownloadingPrefix to
        "A térképadatok frissültek. Újratöltés alatt:",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— a folyamat az „Előzetes letöltés“ szakaszban követhető.",
    StringKey.SettingsMapDataRegionsCountZero to "terület",
    StringKey.SettingsMapDataRegionsCountOne to "terület",
    StringKey.SettingsMapDataRegionsCountTwo to "terület",
    StringKey.SettingsMapDataRegionsCountFew to "terület",
    StringKey.SettingsMapDataRegionsCountMany to "terület",
    StringKey.SettingsMapDataRegionsCountOther to "terület",
    StringKey.SettingsClearMapCacheButton to "Térkép gyorsítótárának törlése",
    StringKey.SettingsClearMapCacheConfirmTitle to "Törli a térkép gyorsítótárát?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "A gyorsítótár törlésekor eltávolítjuk a megtekintett térképrészleteket, amelyeket nem " +
            "mentett el az „Előzetes letöltés“ szakaszban. Biztosan törli a gyorsítótárat?",
    StringKey.SettingsClearMapCacheConfirmYes to "Igen",
    StringKey.SettingsClearMapCacheConfirmNo to "Nem",
    StringKey.SettingsMapCacheCleared to "A gyorsítótár törölve",
)
