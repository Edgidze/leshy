package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Latvian — Phase 11 of `.claude/plans/countries-and-languages.md`. **The only language in the set
 * that reaches `PluralCategory.Zero`**, and the reason Phase 5 added that category against the
 * plan's original five: `pluralCategory` sends 0, every multiple of ten and the whole 11–19 range
 * to `Zero`, n % 10 = 1 to `One`, and the rest to `Other` (`Plurals.kt`). All three fire constantly
 * on real counts — "1 sēne", "2 sēnes", but "10 sēņu", "11 sēņu", "20 sēņu" — so `Zero` here is a
 * genuine third form, not a filler. `Two`, `Few` and `Many` are the unreachable ones and repeat
 * `Other`.
 *
 * The `Zero` form is the genitive plural (sēņu, pastaigu, apgabalu), which is exactly what Latvian
 * requires after a round ten; getting this wrong would mis-word a large share of ordinary counts
 * rather than an edge case. */
internal val latvianStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Leša sēņu karte",
    StringKey.NavRecord to "Jauns ieraksts",
    StringKey.NavArchive to "Pastaigu arhīvs",
    StringKey.NavMap to "Atradumu karte",
    StringKey.NavData to "Eksports/Imports",
    StringKey.NavPreparation to "Iepriekšēja lejupielāde",
    StringKey.NavSpecies to "Manas sēnes",
    StringKey.SettingsTitle to "Iestatījumi",
    StringKey.SettingsContentDescription to "Iestatījumi",
    StringKey.SettingsLanguageTitle to "Saskarnes valoda",
    StringKey.SettingsThemeTitle to "Izskats",
    StringKey.SettingsThemeLight to "Gaišs",
    StringKey.SettingsThemeDark to "Tumšs",
    StringKey.SettingsThemeSystem to "Sistēmas",
    StringKey.SettingsCategoriesTitle to "Atzīmējamās sēnes",
    StringKey.SettingsMushroomSizeTitle to "Pielāgojiet sēņu izmēru kartē",
    StringKey.SettingsMushroomSortTitle to "Sēņu secība",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Atiestatīt sēņu secību pastaigas beigās",
    StringKey.SettingsFreezeMushroomOrder to "Nemainīga sēņu secība",

    StringKey.MushroomImagesDisclaimer to
        "Visi sēņu attēli lietotnē ir tikai ilustratīvi — neizmantojiet tos nepazīstamu sēņu " +
            "noteikšanai!",

    StringKey.SpeciesCollectionsTitle to "Sēņu kolekcijas",
    StringKey.SpeciesMyMushroomsTitle to "Pievienotās sēnes",
    StringKey.SpeciesMyMushroomsEmpty to "Šeit parādīsies sēnes, kuras pievienosiet pats",
    StringKey.SpeciesAddButton to "Pievienot sēni",
    StringKey.SpeciesFormTitleCreate to "Jauna sēne",
    StringKey.SpeciesFormTitleEdit to "Rediģēt sēni",
    StringKey.SpeciesFormNameHint to "Nosaukums",
    StringKey.SpeciesFormScientificNameHint to "Zinātniskais nosaukums",
    StringKey.SpeciesFormColorLabel to "Krāsa",
    StringKey.SpeciesFormTakePhotoButton to "Kamera",
    StringKey.SpeciesFormPickPhotoButton to "Galerija",
    StringKey.SpeciesFormPickCatalogButton to "Attēli",
    StringKey.SpeciesFormSaveButton to "Saglabāt",
    StringKey.SpeciesFormCancelContentDescription to "Atcelt",
    StringKey.SpeciesListImportedLabel to "no arhīva",
    StringKey.SpeciesListEditContentDescription to "Rediģēt",
    StringKey.SpeciesListDeleteContentDescription to "Dzēst sugu",
    StringKey.SpeciesDeleteConfirmTitle to "Dzēst šo sēni?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Vai tiešām vēlaties dzēst šo sugu? Visas šīs sugas atzīmes pastaigās tiks pārvietotas " +
            "uz kategoriju „Nezināma sēne“. Šo darbību nevar atsaukt.",
    StringKey.SpeciesDeleteConfirmYes to "Jā",
    StringKey.SpeciesDeleteConfirmNo to "Nē",

    StringKey.CatalogPhotoPickerTitle to "Izvēlieties attēlu",

    StringKey.IconEditorTitle to "Fotoattēlu redaktors",
    StringKey.IconEditorToolEraser to "Dzēšgumija",
    StringKey.IconEditorToolCrop to "Apgriešana",
    StringKey.IconEditorShapeRectangle to "Taisnstūris",
    StringKey.IconEditorShapeOval to "Ovāls",
    StringKey.IconEditorBrushSizeLabel to "Otas izmērs",
    StringKey.IconEditorUndoContentDescription to "Atsaukt",
    StringKey.IconEditorRedoContentDescription to "Atkārtot",
    StringKey.IconEditorDoneContentDescription to "Gatavs",

    StringKey.OnboardingTitle to "Laipni lūdzam!",
    StringKey.OnboardingDescription to
        "Izvēlieties sēņu kolekcijas, kas jūs interesē. To vēlāk varēsiet mainīt Iestatījumos.",
    StringKey.OnboardingContinueButton to "Sākt",

    StringKey.WelcomeIntro to
        "Lietotne atceras, kur esat gājis un ko esat atradis, — un sēņošanā palīdz manāmi: uz labām vietām " +
            "ir viegli atgriezties, un visi atradumi redzami vienā kartē.",
    StringKey.WelcomeRecordTitle to "Ierakstiet savu gājienu",
    StringKey.WelcomeRecordText to
        "Nospiedumu, laiku un kilometrus lietotne uztur pati. Atradāt sēni — atzīmējiet to, pieskaroties " +
            "tās elementam; avotu, nogāzušos koku vai automašīnu atzīmējat tieši kartē.",
    StringKey.WelcomeArchiveTitle to "Atgriezieties pie saviem atradumiem",
    StringKey.WelcomeArchiveText to
        "Arhīvā katrs gājiens ir atsevišķi — ar savu maršrutu un atradumiem. Bet kopīgā karte rāda tos " +
            "visus kopā: kas, kur un cik atrasts visu sezonu laikā.",
    StringKey.WelcomeHelpTitle to "Neesat pārliecināts — nospiediet „?“",
    StringKey.WelcomeHelpText to
        "Poga „?“ augšā pa labi ir katrā sadaļā un paskaidro, kā šī sadaļa ir iekārtota.",
    StringKey.WelcomeMenuTitle to "Pārējais ir izvēlnē",
    StringKey.WelcomeMenuText to
        "Izvēlnes poga augšā pa kreisi atver visu lietotnes sadaļu un iespēju sarakstu.",
    StringKey.WelcomeConsentTitle to "Pirms lietošanas",
    StringKey.WelcomeConsentIntro to "Pirms pāriet uz pašu lietotni, jāpiekrīt šādiem apgalvojumiem:",
    StringKey.WelcomeConsentImages to
        "Jūs nemēģināsiet noteikt sēnes pēc lietotnē redzamajiem attēliem. Attēli pilda ilustrāciju lomu un nav " +
            "pārbaudīts sēņu noteicējs.",
    StringKey.WelcomeConsentEating to
        "Jūs nekādā gadījumā neēdīsiet sēnes, kuras nepazīstat. Sēnes var būt neēdamas, un tās var būt arī indīgas. " +
            "Vislabāk — pieaiciniet kādu, kas pārzina jūsu apkārtnes sēnes, lai uzzinātu, kuras sēnes drīkst lasīt " +
            "un kā tās pēc tam jāpagatavo.",
    StringKey.WelcomeConsentWarning to
        "Lai turpinātu, jāpiekrīt iepriekš minētajiem apgalvojumiem, atzīmējot izvēles rūtiņas pie tiem " +
            "apgalvojumiem, kuriem piekrītat",

    StringKey.WelcomeNextButton to "Tālāk",

    StringKey.LegalTitle to "Privātums",
    StringKey.LegalPrivacyText to
        "Jūsu gājieni, atzīmes un fotoattēli paliek jūsu ierīcē. Lietotne neveido kontus un nekur nesūta " +
            "jūsu datus — internetā aiziet tikai kartes fragmentu pieprasījumi uz openfreemap.org.",
    StringKey.LegalPrivacyLink to "Privātuma politika",

    StringKey.AboutTitle to "Par lietotni",
    StringKey.AboutMapDataTitle to "Kartes dati",
    StringKey.AboutMapDataText to
        "Karte balstīta uz OpenStreetMap datiem, kas tiek izplatīti ar ODbL licenci. Vektoru flīzes un " +
            "stils nāk no OpenMapTiles, piegādi nodrošina pakalpojums OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "Atvērtais kods",
    StringKey.AboutOpenSourceText to
        "Lietotne ir salikta no atvērtā koda bibliotēkām. Pieskaroties saraksta rindai, atveras tās " +
            "licences pilns teksts.",

    StringKey.NavMenuContentDescription to "Izvēlne",
    StringKey.HelpContentDescription to "Palīdzība",
    StringKey.HelpDialogTitle to "Palīdzība",
    StringKey.HelpDialogDismiss to "Sapratu",

    StringKey.CategoryMisc to "Dažādi",
    StringKey.CategoryUnknownMushroom to "Nezināma sēne",

    StringKey.CollectionPickerSearchHint to "Meklēt valsti",

    StringKey.LanguagePickerSearchHint to "Meklēt valodu",
    StringKey.LanguagePickerBackContentDescription to "Atpakaļ",
    StringKey.LanguagePickerConfirmContentDescription to "Apstiprināt",

    StringKey.DefaultWalkName to "Pastaiga",
    StringKey.RecordWalkNameHint to "Pastaigas nosaukums",
    StringKey.RecordStart to "Sākt",
    StringKey.RecordPause to "Pauze",
    StringKey.RecordResume to "Turpināt",
    StringKey.RecordFinish to "Pabeigt",
    StringKey.RecordSetWalkNameTitle to "Ievadiet pastaigas nosaukumu:",
    StringKey.RecordDefaultWalkNamePrefix to "Pastaiga",
    StringKey.RecordConfirmWalkNameContentDescription to "Apstiprināt",
    StringKey.RecordMarkLocationContentDescription to "Atzīmēt vietu",
    StringKey.RecordLocationUnavailable to "Atrašanās vieta nav pieejama — maršruts netiek ierakstīts. Ieslēdziet atrašanās vietas pakalpojumus un atļaujiet lietotnei piekļuvi ierīces iestatījumos.",
    StringKey.RecordLocationUnknownMessage to
        "Atrašanās vieta vēl nav zināma — atzīmi nav kam piesaistīt. Pārbaudiet, vai atrašanās vieta ir ieslēgta, un pagaidiet signālu.",
    StringKey.RecordSearchContentDescription to "Meklēšana",
    StringKey.RecordSearchDialogTitle to "Izvēlieties vajadzīgo sēni",
    StringKey.RecordBulkAddQuestion to "Cik jaunu sēņu atrasts?",
    StringKey.RecordBulkAddCancelContentDescription to "Atcelt",

    StringKey.RecordBulkAddConfirmContentDescription to "Apstiprināt",
    StringKey.RecordBulkAddLimitMessage to
        "Ne vairāk kā 999 vienas sugas atradumi vienā pastaigā.",
    StringKey.DialogAcknowledge to "Sapratu",

    StringKey.NavigationDirectionToPrefix to "Virziens uz",
    StringKey.NavigationDistanceToTargetPrefix to "līdz mērķim",
    StringKey.NavigationMetersSuffix to "metri",
    StringKey.NavigationKeepRightPhrase to "turieties pa labi par",
    StringKey.NavigationKeepLeftPhrase to "turieties pa kreisi par",
    StringKey.NavigationGoStraightPhrase to "ejiet taisni",
    StringKey.NavigationDeterminingDirection to "Nosakām virzienu…",
    StringKey.NavigationArrivedPhrase to "Esat galamērķī",
    StringKey.NavigationCloseContentDescription to "Aizvērt",

    StringKey.AddPlaceTitle to "Pievienojiet vietu",
    StringKey.AddPlaceEditTitle to "Rediģējiet vietu",
    StringKey.AddPlaceDefaultName to "Vieta",
    StringKey.AddPlaceNameHint to "Vietas nosaukums",
    StringKey.AddPlacePhotoContentDescription to "Nofotografēt",
    StringKey.CameraPermissionDenied to "Nav piekļuves kamerai. Atļaujiet to lietotnei ierīces iestatījumos.",
    StringKey.AddPlaceDescriptionTitle to "Apraksts",
    StringKey.AddPlaceDescriptionHint to "Aprakstiet vietu",
    StringKey.AddPlaceCoordinatesTitle to "Koordinātas",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Kopēt koordinātas",
    StringKey.AddPlaceSaveContentDescription to "Saglabāt vietu",
    StringKey.AddPlaceDiscardContentDescription to "Dzēst vietu",

    StringKey.PlaceViewEditContentDescription to "Rediģēt vietu",
    StringKey.PlaceViewDeleteContentDescription to "Dzēst vietu",
    StringKey.PlaceDeleteConfirmTitle to "Dzēst vietu?",
    StringKey.PlaceDeleteConfirmMessage to
        "Vieta tiks neatgriezeniski dzēsta. To vairs nevarēs atjaunot.",
    StringKey.PlaceDeleteConfirmYes to "Jā",
    StringKey.PlaceDeleteConfirmNo to "Nē",

    StringKey.ArchiveEmpty to "Pagaidām nav pastaigu",
    StringKey.ArchiveEmptyHint to "Šeit būs ierakstītās pastaigas: maršruts, atradumi un atzīmētās vietas.",
    StringKey.EmptyStartWalkButton to "Sākt pastaigu",
    StringKey.ArchiveDeleteWalksButton to "Dzēst pastaigas",
    StringKey.ArchiveDeleteConfirmMessage to
        "Vai tiešām vēlaties neatgriezeniski dzēst atlasītās pastaigas?",
    StringKey.ArchiveDeleteConfirmYes to "Jā",
    StringKey.ArchiveDeleteConfirmNo to "Nē",
    StringKey.WalkDetailStartTime to "Sākums",
    StringKey.WalkDetailEndTime to "Beigas",
    StringKey.WalkDetailInProgress to "nav pabeigta",
    StringKey.WalkDetailDistance to "Noietais ceļš",
    StringKey.WalkDetailDuration to "Ilgums",
    StringKey.WalkDetailAvgSpeed to "Vidējais ātrums",
    StringKey.WalkDetailDurationDays to "d",
    StringKey.WalkDetailDurationHours to "h",
    StringKey.WalkDetailDurationMinutes to "min",
    StringKey.WalkCardDurationHours to "h",
    StringKey.WalkCardDurationMinutes to "min",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Atradumi pēc sugām",
    StringKey.WalkDetailFindsEmpty to "Nav reģistrētu atradumu",
    StringKey.WalkDetailPlacesTitle to "Atzīmētās vietas",
    StringKey.WalkDetailViewMap to "Skatīt karti",
    StringKey.WalkDetailEditContentDescription to "Rediģēt pastaigas nosaukumu",
    StringKey.WalkDetailEditWalkNameTitle to "Mainiet pastaigas nosaukumu:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Apstiprināt",
    StringKey.WalkDetailDeleteContentDescription to "Dzēst pastaigu",
    StringKey.WalkDetailShareAction to "Kopīgot",
    StringKey.WalkDetailDeleteAction to "Dzēst",
    StringKey.WalkDetailDeleteConfirmTitle to "Dzēst pastaigu?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Pastaiga un visi atradumi tiks neatgriezeniski dzēsti. Tos vairs nevarēs atjaunot.",
    StringKey.WalkDetailDeleteConfirmYes to "Jā",
    StringKey.WalkDetailDeleteConfirmNo to "Nē",
    StringKey.WalkDetailMushroomsCountZero to "sēņu",
    StringKey.WalkDetailMushroomsCountOne to "sēne",
    StringKey.WalkDetailMushroomsCountTwo to "sēnes",
    StringKey.WalkDetailMushroomsCountFew to "sēnes",
    StringKey.WalkDetailMushroomsCountMany to "sēnes",
    StringKey.WalkDetailMushroomsCountOther to "sēnes",
    StringKey.WalkDetailDescriptionTitle to "Apraksts",
    StringKey.WalkDetailDescriptionEmpty to "Apraksts nav pievienots",
    StringKey.WalkDetailDescriptionHint to "Aprakstiet pastaigu",
    StringKey.WalkDetailEditDescriptionContentDescription to "Rediģēt aprakstu",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Atcelt",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Saglabāt",

    StringKey.WalkShareContentDescription to "Kopīgot pastaigu",
    StringKey.WalkShareDialogTitle to "Kopīgošana",
    StringKey.WalkShareOptionName to "Pastaigas nosaukums",
    StringKey.WalkShareOptionStats to "Pastaigas statistika",
    StringKey.WalkShareOptionDescription to "Pastaigas apraksts",
    StringKey.WalkShareOptionDiagram to "Atradumu diagramma",
    StringKey.WalkShareOptionMap to "Karte ar atzīmēm",
    StringKey.WalkShareMapWarning to "Citi cilvēki redzēs, kur atradāt sēnes",
    StringKey.WalkShareCancelButton to "Atcelt",
    StringKey.WalkShareConfirmButton to "Kopīgot",
    StringKey.WalkShareFooter to "Izveidots ar lietotni „Leša sēņu karte“",
    StringKey.WalkShareImageFooter to "Izveidots lietotnē Leša sēņu karte",

    StringKey.MapStatsTitle to "Statistika",
    StringKey.MapStatsWalksCount to "Pastaigas",
    StringKey.MapStatsFindsCount to "Atrastas sēnes",
    StringKey.MapStatsEmptyHint to "Statistika izveidosies pati, tiklīdz būs ierakstīta pirmā pastaiga.",

    StringKey.MapFilterButtonLabel to "Filtri",
    StringKey.MapFilterDialogTitle to
        "Pielāgojiet filtrus, ko piemēro sēnēm kartē:",
    StringKey.MapFilterBackContentDescription to "Atpakaļ",
    StringKey.MapFilterDateRangeTitle to "Datumu diapazons",
    StringKey.MapFilterMonthRangeTitle to "Sezona",
    StringKey.MapFilterPastRoutesTitle to "Iepriekšējo maršrutu attēlošana",
    StringKey.MapFilterShowPastRoutes to "Rādīt iepriekšējos maršrutus",

    StringKey.MonthJanuary to "Janvāris",
    StringKey.MonthFebruary to "Februāris",
    StringKey.MonthMarch to "Marts",
    StringKey.MonthApril to "Aprīlis",
    StringKey.MonthMay to "Maijs",
    StringKey.MonthJune to "Jūnijs",
    StringKey.MonthJuly to "Jūlijs",
    StringKey.MonthAugust to "Augusts",
    StringKey.MonthSeptember to "Septembris",
    StringKey.MonthOctober to "Oktobris",
    StringKey.MonthNovember to "Novembris",
    StringKey.MonthDecember to "Decembris",

    StringKey.BackgroundRecordingChannelName to "Pastaigas ierakstīšana",
    StringKey.BackgroundRecordingNotificationTitle to "Notiek pastaigas ierakstīšana",
    StringKey.BackgroundRecordingNotificationText to
        "Maršruts tiek ierakstīts fonā. Pieskarieties, lai atgrieztos lietotnē.",

    StringKey.DataExportOption to "Eksports",
    StringKey.DataImportOption to "Imports",
    StringKey.DataArchiveNameLabel to "Arhīva nosaukums",
    StringKey.DataChooseFileButton to "Izvēlēties failu",
    StringKey.DataFileStatusLabel to "Importējamais fails",
    StringKey.DataFileNotSelected to "nav izvēlēts",
    StringKey.DataImportLabelFieldLabel to "Piebilde pastaigu nosaukumiem",
    StringKey.DataDoneButton to "Gatavs",
    StringKey.DataSavedButton to "Saglabāts",
    StringKey.DataGoToArchiveButton to "Uz arhīvu",
    StringKey.DataCancelButton to "Atcelt",
    StringKey.DataProcessingLabel to "Notiek apstrāde…",
    StringKey.DataExportSuccessMessage to "Arhīvs veiksmīgi saglabāts",
    StringKey.DataImportedWalksLabel to "Importētas pastaigas",
    StringKey.DataImportFailedWalksLabel to "Neizdevās importēt",
    StringKey.DataErrorLabel to "Kļūda",
    StringKey.DataImportRejectedTitle to "Šo failu nevar importēt",
    StringKey.DataImportRejectedNotArchive to "Šis nav arhīvs: failu nevar nolasīt kā ZIP. Izvēlieties no Leshy eksportētu arhīvu.",
    StringKey.DataImportRejectedNotLeshy to "Šis ir ZIP arhīvs, bet ne Leshy: tajā nav manifest.json.",
    StringKey.DataImportRejectedNewerFormat to "Arhīvu izveidojusi jaunāka lietotnes versija. Atjauniniet lietotni un mēģiniet vēlreiz.",
    StringKey.DataImportRejectedDamaged to "Arhīvs ir bojāts: daļu satura nevar nolasīt. Nekas netika importēts.",
    StringKey.DataImportRejectedNoWalks to "Arhīvā nav neviena pastaigas ieraksta — nav ko importēt.",
    StringKey.DataChooseWalksTitle to "Pastaigas arhīvam",
    StringKey.DataWalksBackContentDescription to "Atpakaļ, nesaglabājot izvēli",
    StringKey.DataWalksConfirmContentDescription to "Apstiprināt izvēli",
    StringKey.DataWalksSelectedLabel to "Atlasīts",
    StringKey.DataWalksCountZero to "pastaigu",
    StringKey.DataWalksCountOne to "pastaiga",
    StringKey.DataWalksCountTwo to "pastaigas",
    StringKey.DataWalksCountFew to "pastaigas",
    StringKey.DataWalksCountMany to "pastaigas",
    StringKey.DataWalksCountOther to "pastaigas",
    StringKey.PreparationSelectAreaButton to "Lejupielādēt redzamo apgabalu",
    StringKey.PreparationDownloadThisAreaButton to "Lejupielādēt šo apgabalu",
    StringKey.PreparationRegionNameDialogTitle to "Apgabala nosaukums",
    StringKey.PreparationRegionNameLabel to "Piem.: Mežs pie ciema",
    StringKey.PreparationSaveButton to "Lejupielādēt",
    StringKey.PreparationCancelButton to "Atcelt",
    StringKey.PreparationDeleteConfirmTitle to "Dzēst apgabalu?",
    StringKey.PreparationDeleteConfirmMessage to
        "Lejupielādētās kartes flīzes tiks neatgriezeniski dzēstas.",
    StringKey.PreparationDeleteConfirmYes to "Jā",
    StringKey.PreparationDeleteConfirmNo to "Nē",
    StringKey.PreparationDeleteContentDescription to "Dzēst apgabalu",
    StringKey.PreparationPauseContentDescription to "Apturēt lejupielādi",
    StringKey.PreparationResumeContentDescription to "Turpināt lejupielādi",
    StringKey.PreparationStatusDownloading to "Notiek lejupielāde",
    StringKey.PreparationStatusPaused to "Apturēts",
    StringKey.PreparationStatusComplete to "Lejupielādēts",
    StringKey.PreparationStatusError to "Kļūda",
    StringKey.PreparationSubtitle to
        "Lejupielādējiet redzamo kartes apgabalu, lai to izmantotu bezsaistē",
    StringKey.PreparationRetryContentDescription to "Atkārtot lejupielādi",

    StringKey.MapTilesLoadFailed to "Karte pilnībā neielādējās no",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Aizvērt paziņojumu",

    StringKey.SettingsMapDataTitle to "Kartes dati",
    StringKey.SettingsRefreshMapDataButton to "Atjaunināt kartes datus",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Atjaunināt kartes datus?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Ja kartes saturs ir mainījies, visi lejupielādētie bezsaistes apgabali tiks " +
            "lejupielādēti no jauna. Vai tiešām vēlaties atjaunināt kartes datus?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Jā",
    StringKey.SettingsMapDataUpdateConfirmNo to "Nē",
    StringKey.SettingsMapDataRefreshError to
        "Neizdevās atjaunināt — pārbaudiet interneta savienojumu",
    StringKey.SettingsMapDataRedownloadingPrefix to
        "Kartes dati atjaunināti. No jauna tiek lejupielādēts",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— norisi var apskatīt sadaļā „Iepriekšēja lejupielāde“.",
    StringKey.SettingsMapDataRegionsCountZero to "apgabalu",
    StringKey.SettingsMapDataRegionsCountOne to "apgabals",
    StringKey.SettingsMapDataRegionsCountTwo to "apgabali",
    StringKey.SettingsMapDataRegionsCountFew to "apgabali",
    StringKey.SettingsMapDataRegionsCountMany to "apgabali",
    StringKey.SettingsMapDataRegionsCountOther to "apgabali",
    StringKey.SettingsClearMapCacheButton to "Notīrīt kartes kešatmiņu",
    StringKey.SettingsClearMapCacheConfirmTitle to "Notīrīt kartes kešatmiņu?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Notīrot kešatmiņu, tiks noņemtas apskatītās kartes daļas, kas nav saglabātas sadaļā " +
            "„Iepriekšēja lejupielāde“. Vai tiešām vēlaties notīrīt kešatmiņu?",
    StringKey.SettingsClearMapCacheConfirmYes to "Jā",
    StringKey.SettingsClearMapCacheConfirmNo to "Nē",
    StringKey.SettingsMapCacheCleared to "Kešatmiņa notīrīta",
)
