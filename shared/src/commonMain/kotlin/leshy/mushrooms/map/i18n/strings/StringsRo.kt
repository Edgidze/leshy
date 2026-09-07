package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Romanian — Phase 8 of `.claude/plans/countries-and-languages.md`. Plurals reach `One`/`Few`/
 * `Other` (`Plurals.kt`): 1 takes the singular; 0 and `n % 100` in 2..19 take the bare plural
 * ("2 ciuperci"); everything else — `n % 100` in 20..99, or `n % 100 == 1` with `n != 1` such as
 * 101 — takes the plural with the "de" article ("21 de ciuperci", "101 de ciuperci"), standard
 * Romanian numeral agreement. `Zero` is unreachable in `pluralCategory` for `ro` (0 resolves to
 * `Few`, not `Zero`) so it repeats `Few`'s value here rather than `Other`'s; `Two`/`Many` are
 * unreachable altogether and repeat `Other`, same convention Phase 6 used for `es`/`it`. */
internal val romanianStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Harta ciupercilor de la Leshy",
    StringKey.NavRecord to "Înregistrare nouă",
    StringKey.NavArchive to "Arhivă plimbări",
    StringKey.NavMap to "Harta descoperirilor",
    StringKey.NavData to "Export/Import",
    StringKey.NavPreparation to "Preîncărcare",
    StringKey.NavSpecies to "Ciupercile mele",
    StringKey.SettingsTitle to "Setări",
    StringKey.SettingsContentDescription to "Setări",
    StringKey.SettingsLanguageTitle to "Limba interfeței",
    StringKey.SettingsThemeTitle to "Aspect",
    StringKey.SettingsThemeLight to "Luminos",
    StringKey.SettingsThemeDark to "Întunecat",
    StringKey.SettingsThemeSystem to "Sistem",
    StringKey.SettingsCategoriesTitle to "Ciuperci de urmărit",
    StringKey.SettingsMushroomSizeTitle to "Ajustați dimensiunea ciupercilor pe hartă",
    StringKey.SettingsMushroomSortTitle to "Ordinea ciupercilor",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to "Resetați ordinea ciupercilor la sfârșitul plimbării",
    StringKey.SettingsFreezeMushroomOrder to "Blocați ordinea ciupercilor",

    StringKey.MushroomImagesDisclaimer to
        "Toate imaginile ciupercilor din aplicație sunt orientative — nu le folosiți pentru a " +
            "identifica ciuperci necunoscute!",

    StringKey.SpeciesCollectionsTitle to "Colecții de ciuperci pe țări",
    StringKey.SpeciesMyMushroomsTitle to "Ciuperci adăugate",
    StringKey.SpeciesMyMushroomsEmpty to "Ciupercile pe care le adăugați chiar dvs. vor apărea aici",
    StringKey.SpeciesAddButton to "Adaugă ciupercă",
    StringKey.SpeciesFormTitleCreate to "Ciupercă nouă",
    StringKey.SpeciesFormTitleEdit to "Editează ciuperca",
    StringKey.SpeciesFormNameHint to "Nume",
    StringKey.SpeciesFormScientificNameHint to "Nume științific",
    StringKey.SpeciesFormColorLabel to "Culoare",
    StringKey.SpeciesFormTakePhotoButton to "Cameră",
    StringKey.SpeciesFormPickPhotoButton to "Galerie",
    StringKey.SpeciesFormPickCatalogButton to "Catalog",
    StringKey.SpeciesFormSaveButton to "Salvează",
    StringKey.SpeciesFormCancelContentDescription to "Anulează",
    StringKey.SpeciesCollectionDialogTitle to "În ce colecție?",
    StringKey.SpeciesCollectionDialogBackContentDescription to "Înapoi",
    StringKey.SpeciesCollectionDialogSaveContentDescription to "Salvează în colecție",
    StringKey.SpeciesCollectionNameIsCountry to "Acesta este numele unei țări — alege altul",
    StringKey.SpeciesListImportedLabel to "din arhivă",
    StringKey.SpeciesListEditContentDescription to "Editează",
    StringKey.SpeciesListDeleteContentDescription to "Șterge specia",
    StringKey.SpeciesDeleteConfirmTitle to "Ștergeți această ciupercă?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Sigur doriți să ștergeți această specie? Toate descoperirile înregistrate la ea vor fi " +
            "mutate în categoria „Ciupercă necunoscută”. Această acțiune nu poate fi anulată.",
    StringKey.SpeciesDeleteConfirmYes to "Da",
    StringKey.SpeciesDeleteConfirmNo to "Nu",

    StringKey.CatalogPhotoPickerTitle to "Alegeți o imagine",

    StringKey.IconEditorTitle to "Editor foto",
    StringKey.IconEditorToolEraser to "Radieră",
    StringKey.IconEditorToolCrop to "Decupare",
    StringKey.IconEditorShapeRectangle to "Dreptunghi",
    StringKey.IconEditorShapeOval to "Oval",
    StringKey.IconEditorBrushSizeLabel to "Dimensiune pensulă",
    StringKey.IconEditorUndoContentDescription to "Anulează",
    StringKey.IconEditorRedoContentDescription to "Refă",
    StringKey.IconEditorDoneContentDescription to "Gata",

    StringKey.OnboardingTitle to "Bine ați venit!",
    StringKey.OnboardingDescription to
        "Alegeți colecțiile de ciuperci care vă interesează. Puteți schimba asta mai târziu din Setări.",
    StringKey.OnboardingContinueButton to "Începeți",
    StringKey.OnboardingNothingPickedWarning to
        "Alegeți cel puțin o colecție sau o ciupercă pentru a continua",

    StringKey.WelcomeIntro to
        "Aplicația ține minte pe unde ați umblat și ce ați găsit — și chiar ajută la cules: la locurile " +
            "bune vă întoarceți ușor, iar toate găsirile se văd pe o singură hartă.",
    StringKey.WelcomeRecordTitle to "Înregistrați plimbarea",
    StringKey.WelcomeRecordText to
        "Traseul, timpul și kilometrii îi ține aplicația singură. Ați găsit o ciupercă — atingeți plăcuța " +
            "ei; un izvor, un copac căzut sau mașina le marcați direct pe hartă.",
    StringKey.WelcomeArchiveTitle to "Reveniți la ce ați găsit",
    StringKey.WelcomeArchiveText to
        "În arhivă fiecare plimbare stă separat — cu traseul și găsirile ei. Iar harta comună le arată pe " +
            "toate laolaltă: ce, unde și cât s-a găsit de-a lungul tuturor sezoanelor.",
    StringKey.WelcomeHelpTitle to "Nu sunteți sigur — apăsați „?”",
    StringKey.WelcomeHelpText to
        "Butonul „?” din dreapta sus există în fiecare secțiune și explică cum este alcătuită acea " +
            "secțiune.",
    StringKey.WelcomeMenuTitle to "Restul este în meniu",
    StringKey.WelcomeMenuText to
        "Butonul de meniu din stânga sus deschide lista tuturor secțiunilor și posibilităților aplicației.",
    StringKey.WelcomeConsentTitle to "Înainte de utilizare",
    StringKey.WelcomeConsentIntro to
        "Înainte de a trece la aplicația propriu-zisă, trebuie să fiți de acord cu următoarele afirmații:",
    StringKey.WelcomeConsentImages to
        "Nu veți încerca să identificați ciupercile după imaginile din aplicație. Imaginile au rol de ilustrații și " +
            "nu sunt un ghid verificat.",
    StringKey.WelcomeConsentEating to
        "În niciun caz nu veți mânca ciuperci pe care nu le cunoașteți. Ciupercile pot fi necomestibile și pot fi " +
            "chiar otrăvitoare. Cel mai bine — chemați pe cineva care se pricepe la ciupercile din zona " +
            "dumneavoastră, ca să aflați ce ciuperci se pot culege și cum trebuie gătite după aceea.",
    StringKey.WelcomeConsentWarning to
        "Pentru a continua trebuie să fiți de acord cu afirmațiile de mai sus, bifând căsuțele din dreptul " +
            "afirmațiilor cu care sunteți de acord",

    StringKey.WelcomeNextButton to "Mai departe",

    StringKey.LegalTitle to "Confidențialitate",
    StringKey.LegalPrivacyText to
        "Plimbările, marcajele și fotografiile rămân pe dispozitivul dumneavoastră. Aplicația nu creează " +
            "conturi și nu trimite nicăieri datele dumneavoastră — pe internet pleacă doar cererile pentru " +
            "porțiuni de hartă către openfreemap.org.",
    StringKey.LegalPrivacyLink to "Politica de confidențialitate",

    StringKey.AboutTitle to "Despre aplicație",
    StringKey.AboutMapDataTitle to "Datele hărții",
    StringKey.AboutMapDataText to
        "Harta se bazează pe datele OpenStreetMap, distribuite sub licența ODbL. Dalele vectoriale și " +
            "stilul provin de la OpenMapTiles, livrarea este asigurată de serviciul OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "Cod deschis",
    StringKey.AboutOpenSourceText to
        "Aplicația este alcătuită din biblioteci cu sursă deschisă. Atingerea unui rând din listă deschide " +
            "textul integral al licenței sale.",

    StringKey.NavMenuContentDescription to "Meniu",
    StringKey.HelpContentDescription to "Ajutor",
    StringKey.HelpDialogTitle to "Ajutor",
    StringKey.HelpDialogDismiss to "Am înțeles",

    StringKey.CategoryMisc to "Diverse",
    StringKey.CategoryUnknownMushroom to "Ciupercă necunoscută",

    StringKey.CollectionOtherName to "Altele",
    StringKey.CollectionPickerSearchHint to "Caută colecția sau ciuperca",
    StringKey.CollectionPickerMoreMatches to "Nu sunt afișate toate rezultatele — precizează căutarea",

    StringKey.LanguagePickerSearchHint to "Caută limba",
    StringKey.LanguagePickerBackContentDescription to "Înapoi",
    StringKey.LanguagePickerConfirmContentDescription to "Confirmă",

    StringKey.DefaultWalkName to "Plimbare",
    StringKey.RecordWalkNameHint to "Numele plimbării",
    StringKey.RecordStart to "Start",
    StringKey.RecordPause to "Pauză",
    StringKey.RecordResume to "Continuă",
    StringKey.RecordFinish to "Încheie",
    StringKey.RecordSetWalkNameTitle to "Stabiliți numele plimbării:",
    StringKey.RecordDefaultWalkNamePrefix to "Plimbare din",
    StringKey.RecordConfirmWalkNameContentDescription to "Confirmă",
    StringKey.RecordMarkLocationContentDescription to "Marchează locul",
    StringKey.RecordLocationUnavailable to "Locația nu este disponibilă — traseul nu este înregistrat. Activează serviciile de localizare și permite accesul în setările dispozitivului.",
    StringKey.RecordLocationUnknownMessage to
        "Locația nu este încă cunoscută — nu există la ce să fie legat marcajul. Verifică dacă locația este pornită și așteaptă semnalul.",
    StringKey.RecordSearchContentDescription to "Caută",
    StringKey.RecordSearchDialogTitle to "Alegeți ciuperca de care aveți nevoie",
    StringKey.RecordBulkAddQuestion to "Câte ciuperci noi ați găsit?",
    StringKey.RecordBulkAddCancelContentDescription to "Anulează",

    StringKey.RecordBulkAddConfirmContentDescription to "Confirmă",
    StringKey.RecordBulkAddLimitMessage to "Maximum 999 descoperiri din aceeași specie pe plimbare.",
    StringKey.DialogAcknowledge to "Am înțeles",

    StringKey.NavigationDirectionToPrefix to "Direcție spre",
    StringKey.NavigationDistanceToTargetPrefix to "până la destinație",
    StringKey.NavigationMetersSuffix to "metri",
    StringKey.NavigationKeepRightPhrase to "țineți dreapta cu",
    StringKey.NavigationKeepLeftPhrase to "țineți stânga cu",
    StringKey.NavigationGoStraightPhrase to "mergeți drept înainte",
    StringKey.NavigationDeterminingDirection to "Se determină direcția…",
    StringKey.NavigationArrivedPhrase to "Ați ajuns la destinație",
    StringKey.NavigationCloseContentDescription to "Închide",

    StringKey.AddPlaceTitle to "Adăugați un loc",
    StringKey.AddPlaceEditTitle to "Editați locul",
    StringKey.AddPlaceDefaultName to "Loc",
    StringKey.AddPlaceNameHint to "Numele locului",
    StringKey.AddPlacePhotoContentDescription to "Fă o fotografie",
    StringKey.CameraPermissionDenied to "Fără acces la cameră. Permite-l aplicației în setările dispozitivului.",
    StringKey.AddPlaceDescriptionTitle to "Descriere",
    StringKey.AddPlaceDescriptionHint to "Descrieți locul",
    StringKey.AddPlaceCoordinatesTitle to "Coordonate",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Copiază coordonatele",
    StringKey.AddPlaceSaveContentDescription to "Salvează locul",
    StringKey.AddPlaceDiscardContentDescription to "Elimină locul",

    StringKey.PlaceViewEditContentDescription to "Editează locul",
    StringKey.PlaceViewDeleteContentDescription to "Șterge locul",
    StringKey.PlaceDeleteConfirmTitle to "Ștergeți locul?",
    StringKey.PlaceDeleteConfirmMessage to "Locul va fi șters definitiv. Această acțiune nu poate fi anulată.",
    StringKey.PlaceDeleteConfirmYes to "Da",
    StringKey.PlaceDeleteConfirmNo to "Nu",

    StringKey.ArchiveEmpty to "Nicio plimbare înregistrată încă",
    StringKey.ArchiveEmptyHint to
        "Aici vor apărea plimbările înregistrate: traseul, ciupercile găsite și locurile marcate.",
    StringKey.EmptyStartWalkButton to "Începe o plimbare",
    StringKey.ArchiveDeleteWalksButton to "Șterge plimbările",
    StringKey.ArchiveDeleteConfirmMessage to "Sigur doriți să ștergeți definitiv plimbările selectate?",
    StringKey.ArchiveDeleteConfirmYes to "Da",
    StringKey.ArchiveDeleteConfirmNo to "Nu",
    StringKey.WalkDetailStartTime to "Început",
    StringKey.WalkDetailEndTime to "Sfârșit",
    StringKey.WalkDetailInProgress to "în desfășurare",
    StringKey.WalkDetailDistance to "Distanță",
    StringKey.WalkDetailDuration to "Durată",
    StringKey.WalkDetailAvgSpeed to "Viteză medie",
    StringKey.WalkDetailDurationDays to "z",
    StringKey.WalkDetailDurationHours to "h",
    StringKey.WalkDetailDurationMinutes to "min",
    StringKey.WalkCardDurationHours to "h",
    StringKey.WalkCardDurationMinutes to "min",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Descoperiri pe tipuri",
    StringKey.WalkDetailFindsEmpty to "Nicio descoperire înregistrată",
    StringKey.WalkDetailPlacesTitle to "Locuri marcate",
    StringKey.WalkDetailViewMap to "Vezi harta",
    StringKey.WalkDetailEditContentDescription to "Editează numele plimbării",
    StringKey.WalkDetailEditWalkNameTitle to "Schimbați numele plimbării:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Confirmă",
    StringKey.WalkDetailDeleteContentDescription to "Șterge plimbarea",
    StringKey.WalkDetailShareAction to "Distribuie",
    StringKey.WalkDetailDeleteAction to "Șterge",
    StringKey.WalkDetailDeleteConfirmTitle to "Ștergeți plimbarea?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Plimbarea și toate descoperirile vor fi șterse definitiv. Această acțiune nu poate fi anulată.",
    StringKey.WalkDetailDeleteConfirmYes to "Da",
    StringKey.WalkDetailDeleteConfirmNo to "Nu",
    StringKey.WalkDetailMushroomsCountZero to "ciuperci",
    StringKey.WalkDetailMushroomsCountOne to "ciupercă",
    StringKey.WalkDetailMushroomsCountTwo to "de ciuperci",
    StringKey.WalkDetailMushroomsCountFew to "ciuperci",
    StringKey.WalkDetailMushroomsCountMany to "de ciuperci",
    StringKey.WalkDetailMushroomsCountOther to "de ciuperci",
    StringKey.WalkDetailDescriptionTitle to "Descriere",
    StringKey.WalkDetailDescriptionEmpty to "Nicio descriere adăugată",
    StringKey.WalkDetailDescriptionHint to "Descrieți plimbarea",
    StringKey.WalkDetailEditDescriptionContentDescription to "Editează descrierea",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Anulează",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Salvează",

    StringKey.WalkShareContentDescription to "Distribuie plimbarea",
    StringKey.WalkShareDialogTitle to "Distribuie",
    StringKey.WalkShareOptionName to "Numele plimbării",
    StringKey.WalkShareOptionStats to "Statisticile plimbării",
    StringKey.WalkShareOptionDescription to "Descrierea plimbării",
    StringKey.WalkShareOptionDiagram to "Diagrama descoperirilor",
    StringKey.WalkShareOptionMap to "Harta cu marcaje",
    StringKey.WalkShareMapWarning to "Alte persoane vor putea vedea unde ați găsit ciuperci",
    StringKey.WalkShareCancelButton to "Anulează",
    StringKey.WalkShareConfirmButton to "Distribuie",
    StringKey.WalkShareFooter to "Creat cu aplicația „Harta ciupercilor de la Leshy”",
    StringKey.WalkShareImageFooter to "Creat în aplicația Harta ciupercilor de la Leshy",

    StringKey.MapStatsTitle to "Statistici",
    StringKey.MapStatsWalksCount to "Plimbări",
    StringKey.MapStatsFindsCount to "Ciuperci găsite",
    StringKey.MapStatsEmptyHint to
        "Statisticile se vor aduna singure de îndată ce este înregistrată prima plimbare.",

    StringKey.MapFilterButtonLabel to "Filtre",
    StringKey.MapFilterDialogTitle to "Configurați filtrele aplicate ciupercilor de pe hartă:",
    StringKey.MapFilterBackContentDescription to "Înapoi",
    StringKey.MapFilterDateRangeTitle to "Interval de date",
    StringKey.MapFilterMonthRangeTitle to "Sezon",
    StringKey.MapFilterPastRoutesTitle to "Afișarea traseelor anterioare",
    StringKey.MapFilterShowPastRoutes to "Afișează traseele anterioare",

    StringKey.MonthJanuary to "Ianuarie",
    StringKey.MonthFebruary to "Februarie",
    StringKey.MonthMarch to "Martie",
    StringKey.MonthApril to "Aprilie",
    StringKey.MonthMay to "Mai",
    StringKey.MonthJune to "Iunie",
    StringKey.MonthJuly to "Iulie",
    StringKey.MonthAugust to "August",
    StringKey.MonthSeptember to "Septembrie",
    StringKey.MonthOctober to "Octombrie",
    StringKey.MonthNovember to "Noiembrie",
    StringKey.MonthDecember to "Decembrie",

    StringKey.BackgroundRecordingChannelName to "Înregistrare plimbare",
    StringKey.BackgroundRecordingNotificationTitle to "Se înregistrează plimbarea",
    StringKey.BackgroundRecordingNotificationText to
        "Traseul este înregistrat în fundal. Atingeți pentru a reveni în aplicație.",

    StringKey.DataExportOption to "Export",
    StringKey.DataImportOption to "Import",
    StringKey.DataArchiveNameLabel to "Numele arhivei",
    StringKey.DataChooseFileButton to "Alege fișierul",
    StringKey.DataFileStatusLabel to "Fișier de importat",
    StringKey.DataFileNotSelected to "neselectat",
    StringKey.DataImportLabelFieldLabel to "Etichetă adăugată la numele plimbărilor importate",
    StringKey.DataDoneButton to "Gata",
    StringKey.DataSavedButton to "Salvat",
    StringKey.DataGoToArchiveButton to "Spre arhivă",
    StringKey.DataCancelButton to "Anulează",
    StringKey.DataProcessingLabel to "Se procesează…",
    StringKey.DataExportSuccessMessage to "Arhiva a fost salvată cu succes",
    StringKey.DataImportedWalksLabel to "Plimbări importate",
    StringKey.DataImportFailedWalksLabel to "Import eșuat",
    StringKey.DataErrorLabel to "Eroare",
    StringKey.DataImportRejectedTitle to "Acest fișier nu poate fi importat",
    StringKey.DataImportRejectedNotArchive to "Aceasta nu este o arhivă: fișierul nu poate fi citit ca ZIP. Alege o arhivă exportată din Leshy.",
    StringKey.DataImportRejectedNotLeshy to "Este o arhivă ZIP, dar nu una Leshy: nu conține manifest.json.",
    StringKey.DataImportRejectedNewerFormat to "Arhiva a fost creată de o versiune mai nouă a aplicației. Actualizează aplicația și încearcă din nou.",
    StringKey.DataImportRejectedDamaged to "Arhiva este deteriorată: o parte din conținut nu poate fi citită. Nu a fost importat nimic.",
    StringKey.DataImportRejectedNoWalks to "Arhiva nu conține nicio plimbare — nu este nimic de importat.",
    StringKey.DataChooseWalksTitle to "Plimbări pentru arhivă",
    StringKey.DataWalksBackContentDescription to "Înapoi fără a salva selecția",
    StringKey.DataWalksConfirmContentDescription to "Confirmă selecția",
    StringKey.DataWalksSelectedLabel to "Selectate",
    StringKey.DataWalksCountZero to "plimbări",
    StringKey.DataWalksCountOne to "plimbare",
    StringKey.DataWalksCountTwo to "de plimbări",
    StringKey.DataWalksCountFew to "plimbări",
    StringKey.DataWalksCountMany to "de plimbări",
    StringKey.DataWalksCountOther to "de plimbări",
    StringKey.PreparationSelectAreaButton to "Descarcă zona vizibilă",
    StringKey.PreparationDownloadThisAreaButton to "Descarcă această zonă",
    StringKey.PreparationRegionNameDialogTitle to "Numele zonei",
    StringKey.PreparationRegionNameLabel to "De ex.: Pădurea de lângă sat",
    StringKey.PreparationSaveButton to "Descarcă",
    StringKey.PreparationCancelButton to "Anulează",
    StringKey.PreparationDeleteConfirmTitle to "Ștergeți zona?",
    StringKey.PreparationDeleteConfirmMessage to "Plăcile de hartă descărcate vor fi șterse definitiv.",
    StringKey.PreparationDeleteConfirmYes to "Da",
    StringKey.PreparationDeleteConfirmNo to "Nu",
    StringKey.PreparationDeleteContentDescription to "Șterge zona",
    StringKey.PreparationPauseContentDescription to "Pune descărcarea pe pauză",
    StringKey.PreparationResumeContentDescription to "Reia descărcarea",
    StringKey.PreparationStatusDownloading to "Se descarcă",
    StringKey.PreparationStatusPaused to "În pauză",
    StringKey.PreparationStatusComplete to "Descărcat",
    StringKey.PreparationStatusError to "Eroare",
    StringKey.PreparationSubtitle to "Descărcați zona de hartă vizibilă pentru a o folosi offline",
    StringKey.PreparationRetryContentDescription to "Reia încercarea de descărcare",

    StringKey.MapTilesLoadFailed to "Harta nu s-a încărcat complet de pe",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Închide notificarea",

    StringKey.SettingsMapDataTitle to "Date hartă",
    StringKey.SettingsRefreshMapDataButton to "Actualizează datele hărții",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Actualizați datele hărții?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Dacă conținutul hărții s-a schimbat, toate zonele offline descărcate vor fi redescărcate. " +
            "Sigur doriți să actualizați datele hărții?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Da",
    StringKey.SettingsMapDataUpdateConfirmNo to "Nu",
    StringKey.SettingsMapDataRefreshError to "Actualizarea a eșuat — verificați conexiunea la internet",
    StringKey.SettingsMapDataRedownloadingPrefix to "Datele hărții au fost actualizate. Se redescarcă",
    StringKey.SettingsMapDataRedownloadingSuffix to "— puteți urmări progresul în secțiunea Preîncărcare.",
    StringKey.SettingsMapDataRegionsCountZero to "zone",
    StringKey.SettingsMapDataRegionsCountOne to "zonă",
    StringKey.SettingsMapDataRegionsCountTwo to "de zone",
    StringKey.SettingsMapDataRegionsCountFew to "zone",
    StringKey.SettingsMapDataRegionsCountMany to "de zone",
    StringKey.SettingsMapDataRegionsCountOther to "de zone",
    StringKey.SettingsClearMapCacheButton to "Golește memoria cache a hărții",
    StringKey.SettingsClearMapCacheConfirmTitle to "Goliți memoria cache a hărții?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Golirea memoriei cache a hărții elimină zonele de hartă vizualizate care nu au fost salvate " +
            "în secțiunea Preîncărcare. Sigur doriți să goliți memoria cache?",
    StringKey.SettingsClearMapCacheConfirmYes to "Da",
    StringKey.SettingsClearMapCacheConfirmNo to "Nu",
    StringKey.SettingsMapCacheCleared to "Memoria cache a fost golită",
)
