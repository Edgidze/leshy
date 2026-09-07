package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** German — Phase 6 of `.claude/plans/countries-and-languages.md`. Plural forms follow the
 * two-way German split (`Plurals.kt`: `n == 1` → [leshy.mushrooms.map.i18n.PluralCategory.One],
 * else → [leshy.mushrooms.map.i18n.PluralCategory.Other]) — `Zero`/`Two`/`Few`/`Many` are
 * unreachable for `de` and simply repeat the plural form, same convention as `englishStrings`. */
internal val germanStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Pilzkarte von Leshy",
    StringKey.NavRecord to "Aufzeichnen",
    StringKey.NavArchive to "Archiv",
    StringKey.NavMap to "Fundkarte",
    StringKey.NavData to "Export/Import",
    StringKey.NavPreparation to "Vorbereitung",
    StringKey.NavSpecies to "Meine Pilze",
    StringKey.SettingsTitle to "Einstellungen",
    StringKey.SettingsContentDescription to "Einstellungen",
    StringKey.SettingsLanguageTitle to "Oberflächensprache",
    StringKey.SettingsThemeTitle to "Erscheinungsbild",
    StringKey.SettingsThemeLight to "Hell",
    StringKey.SettingsThemeDark to "Dunkel",
    StringKey.SettingsThemeSystem to "System",
    StringKey.SettingsCategoriesTitle to "Pilzauswahl",
    StringKey.SettingsMushroomSizeTitle to "Pilzgröße auf der Karte anpassen",
    StringKey.SettingsMushroomSortTitle to "Pilzreihenfolge",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Pilzreihenfolge am Ende des Spaziergangs zurücksetzen",
    StringKey.SettingsFreezeMushroomOrder to "Pilzreihenfolge einfrieren",

    StringKey.MushroomImagesDisclaimer to
        "Alle Pilzbilder in der App sind rein illustrativ – verwenden Sie sie nicht zur " +
            "Bestimmung unbekannter Pilze!",

    StringKey.SpeciesCollectionsTitle to "Pilzsammlungen nach Ländern",
    StringKey.SpeciesMyMushroomsTitle to "Hinzugefügte Pilze",
    StringKey.SpeciesMyMushroomsEmpty to "Hier erscheinen die Pilze, die Sie selbst hinzufügen",
    StringKey.SpeciesAddButton to "Pilz hinzufügen",
    StringKey.SpeciesFormTitleCreate to "Neuer Pilz",
    StringKey.SpeciesFormTitleEdit to "Pilz bearbeiten",
    StringKey.SpeciesFormNameHint to "Name",
    StringKey.SpeciesFormScientificNameHint to "Wissenschaftlicher Name",
    StringKey.SpeciesFormColorLabel to "Farbe",
    StringKey.SpeciesFormTakePhotoButton to "Kamera",
    StringKey.SpeciesFormPickPhotoButton to "Galerie",
    StringKey.SpeciesFormPickCatalogButton to "Bilder",
    StringKey.SpeciesFormSaveButton to "Speichern",
    StringKey.SpeciesFormCancelContentDescription to "Abbrechen",
    StringKey.SpeciesCollectionDialogTitle to "In welche Sammlung?",
    StringKey.SpeciesCollectionDialogBackContentDescription to "Zurück",
    StringKey.SpeciesCollectionDialogSaveContentDescription to "In Sammlung speichern",
    StringKey.SpeciesCollectionNameIsCountry to "Das ist ein Ländername — bitte einen anderen wählen",
    StringKey.SpeciesListImportedLabel to "aus dem Archiv",
    StringKey.SpeciesListEditContentDescription to "Bearbeiten",
    StringKey.SpeciesListDeleteContentDescription to "Art löschen",
    StringKey.SpeciesDeleteConfirmTitle to "Diesen Pilz löschen?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Möchten Sie diese Art wirklich löschen? Alle damit verzeichneten Funde werden der " +
            "Kategorie „Unbekannter Pilz“ zugeordnet. Dies kann nicht rückgängig gemacht werden.",
    StringKey.SpeciesDeleteConfirmYes to "Ja",
    StringKey.SpeciesDeleteConfirmNo to "Nein",

    StringKey.CatalogPhotoPickerTitle to "Bild auswählen",

    StringKey.IconEditorTitle to "Fotoeditor",
    StringKey.IconEditorToolEraser to "Radierer",
    StringKey.IconEditorToolCrop to "Zuschneiden",
    StringKey.IconEditorShapeRectangle to "Rechteck",
    StringKey.IconEditorShapeOval to "Oval",
    StringKey.IconEditorBrushSizeLabel to "Pinselgröße",
    StringKey.IconEditorUndoContentDescription to "Rückgängig",
    StringKey.IconEditorRedoContentDescription to "Wiederholen",
    StringKey.IconEditorDoneContentDescription to "Fertig",

    StringKey.OnboardingTitle to "Willkommen!",
    StringKey.OnboardingDescription to
        "Wählen Sie die Pilzsammlungen aus, die Sie interessieren. Das können Sie später in den " +
            "Einstellungen ändern.",
    StringKey.OnboardingContinueButton to "Los geht's",
    StringKey.OnboardingNothingPickedWarning to
        "Wählen Sie mindestens eine Sammlung oder einen Pilz aus, um fortzufahren",

    StringKey.WelcomeIntro to
        "Die App merkt sich, wo Sie gelaufen sind und was Sie gefunden haben – und hilft beim Sammeln " +
            "spürbar: zu guten Stellen finden Sie leicht zurück, und alle Funde liegen auf einer Karte.",
    StringKey.WelcomeRecordTitle to "Zeichnen Sie den Spaziergang auf",
    StringKey.WelcomeRecordText to
        "Strecke, Zeit und Kilometer zeichnet die App von selbst auf. Pilz gefunden – tippen Sie auf seine " +
            "Kachel; eine Quelle, einen umgestürzten Baum oder Ihr Auto markieren Sie direkt auf der Karte.",
    StringKey.WelcomeArchiveTitle to "Kehren Sie zu Ihren Funden zurück",
    StringKey.WelcomeArchiveText to
        "Im Archiv liegt jeder Spaziergang für sich – mit eigener Strecke und eigenen Funden. Die " +
            "gemeinsame Karte zeigt alle zusammen: was wo und wie viel über alle Saisons hinweg gefunden wurde.",
    StringKey.WelcomeHelpTitle to "Unsicher? Tippen Sie auf „?“",
    StringKey.WelcomeHelpText to
        "In jedem Bereich gibt es oben rechts die Schaltfläche „?“, die erklärt, wie dieser Bereich " +
            "aufgebaut ist.",
    StringKey.WelcomeMenuTitle to "Alles Weitere steht im Menü",
    StringKey.WelcomeMenuText to
        "Die Menü-Schaltfläche oben links öffnet die Liste aller Bereiche und Möglichkeiten der App.",
    StringKey.WelcomeConsentTitle to "Bevor Sie loslegen",
    StringKey.WelcomeConsentIntro to
        "Bevor Sie zur App selbst übergehen, müssen Sie den folgenden Aussagen zustimmen:",
    StringKey.WelcomeConsentImages to
        "Sie werden nicht versuchen, Pilze anhand der Bilder aus der App zu bestimmen. Die Bilder sind " +
            "Illustrationen und kein geprüftes Bestimmungsbuch.",
    StringKey.WelcomeConsentEating to
        "Sie werden unter keinen Umständen Pilze essen, die Sie nicht kennen. Pilze können ungenießbar und auch " +
            "giftig sein. Am besten — holen Sie jemanden dazu, der sich mit den Pilzen Ihrer Gegend auskennt, um zu " +
            "erfahren, welche Pilze gesammelt werden dürfen und wie sie danach zubereitet werden müssen.",
    StringKey.WelcomeConsentBattery to
        "Sie werden bei Ihren Spaziergängen die nötigen Sicherheitsregeln beachten und daran denken, dass sich der " +
            "Akku des Telefons bei laufender App schneller entlädt. Bei niedrigem Ladestand halten Sie die " +
            "Aufzeichnung des Spaziergangs besser an und schließen die App.",
    StringKey.WelcomeConsentWarning to
        "Um fortzufahren, müssen Sie den Aussagen oben zustimmen und die Kästchen vor den Aussagen ankreuzen, denen " +
            "Sie zustimmen",

    StringKey.WelcomeNextButton to "Weiter",

    StringKey.LegalTitle to "Datenschutz",
    StringKey.LegalPrivacyText to
        "Ihre Spaziergänge, Markierungen und Fotos bleiben auf Ihrem Gerät. Die App legt keine Konten an " +
            "und sendet keine Ihrer Daten irgendwohin – ins Netz gehen nur Anfragen nach Kartenausschnitten " +
            "von openfreemap.org.",
    StringKey.LegalPrivacyLink to "Datenschutzerklärung",

    StringKey.AboutTitle to "Über die App",
    StringKey.AboutMapDataTitle to "Kartendaten",
    StringKey.AboutMapDataText to
        "Die Karte beruht auf OpenStreetMap-Daten, die unter der ODbL-Lizenz verbreitet werden. " +
            "Vektorkacheln und Stil stammen von OpenMapTiles, ausgeliefert werden sie vom Dienst " +
            "OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "Open Source",
    StringKey.AboutOpenSourceText to
        "Die App ist aus Open-Source-Bibliotheken zusammengesetzt. Ein Tippen auf eine Zeile der Liste " +
            "öffnet den vollständigen Text ihrer Lizenz.",

    StringKey.NavMenuContentDescription to "Menü",
    StringKey.HelpContentDescription to "Hilfe",
    StringKey.HelpDialogTitle to "Hilfe",
    StringKey.HelpDialogDismiss to "Verstanden",

    StringKey.CategoryMisc to "Sonstiges",
    StringKey.CategoryUnknownMushroom to "Unbekannter Pilz",

    StringKey.CollectionOtherName to "Andere",
    StringKey.CollectionPickerSearchHint to "Sammlung oder Pilz suchen",
    StringKey.CollectionPickerMoreMatches to "Es werden nicht alle Treffer angezeigt — Suche eingrenzen",

    StringKey.LanguagePickerSearchHint to "Sprache suchen",
    StringKey.LanguagePickerBackContentDescription to "Zurück",
    StringKey.LanguagePickerConfirmContentDescription to "Bestätigen",

    StringKey.DefaultWalkName to "Spaziergang",
    StringKey.RecordWalkNameHint to "Name des Spaziergangs",
    StringKey.RecordStart to "Start",
    StringKey.RecordPause to "Pause",
    StringKey.RecordResume to "Fortsetzen",
    StringKey.RecordFinish to "Beenden",
    StringKey.RecordSetWalkNameTitle to "Name des Spaziergangs festlegen:",
    StringKey.RecordDefaultWalkNamePrefix to "Spaziergang vom",
    StringKey.RecordConfirmWalkNameContentDescription to "Bestätigen",
    StringKey.RecordMarkLocationContentDescription to "Standort markieren",
    StringKey.RecordLocationUnavailable to "Der Standort ist nicht verfügbar — die Route wird nicht aufgezeichnet. Aktiviere die Ortungsdienste und erlaube der App den Zugriff in den Geräteeinstellungen.",
    StringKey.RecordLocationUnknownMessage to
        "Der Standort ist noch nicht bekannt — es gibt nichts, woran die Markierung hängen könnte. Prüfe, ob die Ortung eingeschaltet ist, und warte auf ein Signal.",
    StringKey.RecordSearchContentDescription to "Suche",
    StringKey.RecordSearchDialogTitle to "Wählen Sie den gesuchten Pilz aus",
    StringKey.RecordBulkAddQuestion to "Wie viele neue Pilze gefunden?",
    StringKey.RecordBulkAddCancelContentDescription to "Abbrechen",

    StringKey.RecordBulkAddConfirmContentDescription to "Bestätigen",
    StringKey.RecordBulkAddLimitMessage to
        "Maximal 999 Funde derselben Art pro Spaziergang.",
    StringKey.DialogAcknowledge to "Verstanden",

    StringKey.NavigationDirectionToPrefix to "Richtung zu",
    StringKey.NavigationDistanceToTargetPrefix to "bis zum Ziel",
    StringKey.NavigationMetersSuffix to "Meter",
    StringKey.NavigationKeepRightPhrase to "halten Sie sich rechts um",
    StringKey.NavigationKeepLeftPhrase to "halten Sie sich links um",
    StringKey.NavigationGoStraightPhrase to "geradeaus weiter",
    StringKey.NavigationDeterminingDirection to "Richtung wird ermittelt…",
    StringKey.NavigationArrivedPhrase to "Sie sind angekommen",
    StringKey.NavigationCloseContentDescription to "Schließen",

    StringKey.AddPlaceTitle to "Ort hinzufügen",
    StringKey.AddPlaceEditTitle to "Ort bearbeiten",
    StringKey.AddPlaceDefaultName to "Ort",
    StringKey.AddPlaceNameHint to "Name des Ortes",
    StringKey.AddPlacePhotoContentDescription to "Foto aufnehmen",
    StringKey.CameraPermissionDenied to "Kein Kamerazugriff. Erlaube ihn der App in den Geräteeinstellungen.",
    StringKey.AddPlaceDescriptionTitle to "Beschreibung",
    StringKey.AddPlaceDescriptionHint to "Beschreiben Sie den Ort",
    StringKey.AddPlaceCoordinatesTitle to "Koordinaten",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Koordinaten kopieren",
    StringKey.AddPlaceSaveContentDescription to "Ort speichern",
    StringKey.AddPlaceDiscardContentDescription to "Ort verwerfen",

    StringKey.PlaceViewEditContentDescription to "Ort bearbeiten",
    StringKey.PlaceViewDeleteContentDescription to "Ort löschen",
    StringKey.PlaceDeleteConfirmTitle to "Ort löschen?",
    StringKey.PlaceDeleteConfirmMessage to
        "Der Ort wird endgültig gelöscht. Dies kann nicht rückgängig gemacht werden.",
    StringKey.PlaceDeleteConfirmYes to "Ja",
    StringKey.PlaceDeleteConfirmNo to "Nein",

    StringKey.ArchiveEmpty to "Noch keine Spaziergänge aufgezeichnet",
    StringKey.ArchiveEmptyHint to
        "Aufgezeichnete Spaziergänge erscheinen hier: Route, Funde und markierte Orte.",
    StringKey.EmptyStartWalkButton to "Spaziergang starten",
    StringKey.ArchiveDeleteWalksButton to "Spaziergänge löschen",
    StringKey.ArchiveDeleteConfirmMessage to
        "Möchten Sie die ausgewählten Spaziergänge wirklich endgültig löschen?",
    StringKey.ArchiveDeleteConfirmYes to "Ja",
    StringKey.ArchiveDeleteConfirmNo to "Nein",
    StringKey.WalkDetailStartTime to "Start",
    StringKey.WalkDetailEndTime to "Ende",
    StringKey.WalkDetailInProgress to "läuft noch",
    StringKey.WalkDetailDistance to "Strecke",
    StringKey.WalkDetailDuration to "Dauer",
    StringKey.WalkDetailAvgSpeed to "Durchschnittsgeschwindigkeit",
    StringKey.WalkDetailDurationDays to "T",
    StringKey.WalkDetailDurationHours to "Std",
    StringKey.WalkDetailDurationMinutes to "Min",
    StringKey.WalkCardDurationHours to "Std",
    StringKey.WalkCardDurationMinutes to "Min",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Funde nach Art",
    StringKey.WalkDetailFindsEmpty to "Keine Funde verzeichnet",
    StringKey.WalkDetailPlacesTitle to "Markierte Orte",
    StringKey.WalkDetailViewMap to "Karte ansehen",
    StringKey.WalkDetailEditContentDescription to "Namen des Spaziergangs bearbeiten",
    StringKey.WalkDetailEditWalkNameTitle to "Namen des Spaziergangs ändern:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Bestätigen",
    StringKey.WalkDetailDeleteContentDescription to "Spaziergang löschen",
    StringKey.WalkDetailShareAction to "Teilen",
    StringKey.WalkDetailDeleteAction to "Löschen",
    StringKey.WalkDetailDeleteConfirmTitle to "Spaziergang löschen?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Der Spaziergang und alle Funde werden endgültig gelöscht. Dies kann nicht rückgängig " +
            "gemacht werden.",
    StringKey.WalkDetailDeleteConfirmYes to "Ja",
    StringKey.WalkDetailDeleteConfirmNo to "Nein",
    StringKey.WalkDetailMushroomsCountZero to "Pilze",
    StringKey.WalkDetailMushroomsCountOne to "Pilz",
    StringKey.WalkDetailMushroomsCountTwo to "Pilze",
    StringKey.WalkDetailMushroomsCountFew to "Pilze",
    StringKey.WalkDetailMushroomsCountMany to "Pilze",
    StringKey.WalkDetailMushroomsCountOther to "Pilze",
    StringKey.WalkDetailDescriptionTitle to "Beschreibung",
    StringKey.WalkDetailDescriptionEmpty to "Keine Beschreibung hinzugefügt",
    StringKey.WalkDetailDescriptionHint to "Beschreiben Sie den Spaziergang",
    StringKey.WalkDetailEditDescriptionContentDescription to "Beschreibung bearbeiten",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Abbrechen",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Speichern",

    StringKey.WalkShareContentDescription to "Spaziergang teilen",
    StringKey.WalkShareDialogTitle to "Teilen",
    StringKey.WalkShareOptionName to "Name des Spaziergangs",
    StringKey.WalkShareOptionStats to "Statistik des Spaziergangs",
    StringKey.WalkShareOptionDescription to "Beschreibung des Spaziergangs",
    StringKey.WalkShareOptionDiagram to "Funddiagramm",
    StringKey.WalkShareOptionMap to "Karte mit Markierungen",
    StringKey.WalkShareMapWarning to "Andere Personen können sehen, wo Sie Pilze gefunden haben",
    StringKey.WalkShareCancelButton to "Abbrechen",
    StringKey.WalkShareConfirmButton to "Teilen",
    StringKey.WalkShareFooter to "Erstellt mit der App „Pilzkarte von Leshy“",
    StringKey.WalkShareImageFooter to "Erstellt in der App Pilzkarte von Leshy",

    StringKey.MapStatsTitle to "Statistik",
    StringKey.MapStatsWalksCount to "Spaziergänge",
    StringKey.MapStatsFindsCount to "Gefundene Pilze",
    StringKey.MapStatsEmptyHint to
        "Die Statistik entsteht von selbst, sobald der erste Spaziergang aufgezeichnet ist.",

    StringKey.MapFilterButtonLabel to "Filter",
    StringKey.MapFilterDialogTitle to "Legen Sie die Filter für die Pilze auf der Karte fest:",
    StringKey.MapFilterBackContentDescription to "Zurück",
    StringKey.MapFilterDateRangeTitle to "Datumsbereich",
    StringKey.MapFilterMonthRangeTitle to "Saison",
    StringKey.MapFilterPastRoutesTitle to "Anzeige vergangener Routen",
    StringKey.MapFilterShowPastRoutes to "Vergangene Routen anzeigen",

    StringKey.MonthJanuary to "Januar",
    StringKey.MonthFebruary to "Februar",
    StringKey.MonthMarch to "März",
    StringKey.MonthApril to "April",
    StringKey.MonthMay to "Mai",
    StringKey.MonthJune to "Juni",
    StringKey.MonthJuly to "Juli",
    StringKey.MonthAugust to "August",
    StringKey.MonthSeptember to "September",
    StringKey.MonthOctober to "Oktober",
    StringKey.MonthNovember to "November",
    StringKey.MonthDecember to "Dezember",

    StringKey.BackgroundRecordingChannelName to "Spaziergangsaufzeichnung",
    StringKey.BackgroundRecordingNotificationTitle to "Spaziergang wird aufgezeichnet",
    StringKey.BackgroundRecordingNotificationText to
        "Die Route wird im Hintergrund aufgezeichnet. Tippen Sie, um zur App zurückzukehren.",

    StringKey.DataExportOption to "Export",
    StringKey.DataImportOption to "Import",
    StringKey.DataArchiveNameLabel to "Name des Archivs",
    StringKey.DataChooseFileButton to "Datei auswählen",
    StringKey.DataFileStatusLabel to "Datei für den Import",
    StringKey.DataFileNotSelected to "nicht ausgewählt",
    StringKey.DataImportLabelFieldLabel to "Zusatz zu importierten Spaziergangsnamen",
    StringKey.DataDoneButton to "Fertig",
    StringKey.DataSavedButton to "Gespeichert",
    StringKey.DataGoToArchiveButton to "Zum Archiv",
    StringKey.DataCancelButton to "Abbrechen",
    StringKey.DataProcessingLabel to "Wird verarbeitet…",
    StringKey.DataExportSuccessMessage to "Archiv erfolgreich gespeichert",
    StringKey.DataImportedWalksLabel to "Importierte Spaziergänge",
    StringKey.DataImportFailedWalksLabel to "Import fehlgeschlagen",
    StringKey.DataErrorLabel to "Fehler",
    StringKey.DataImportRejectedTitle to "Diese Datei kann nicht importiert werden",
    StringKey.DataImportRejectedNotArchive to "Das ist kein Archiv: Die Datei lässt sich nicht als ZIP lesen. Wähle ein aus Leshy exportiertes Archiv.",
    StringKey.DataImportRejectedNotLeshy to "Das ist ein ZIP-Archiv, aber kein Leshy-Archiv: Es enthält keine manifest.json.",
    StringKey.DataImportRejectedNewerFormat to "Das Archiv stammt von einer neueren App-Version. Aktualisiere die App und versuche es erneut.",
    StringKey.DataImportRejectedDamaged to "Das Archiv ist beschädigt: Ein Teil des Inhalts lässt sich nicht lesen. Es wurde nichts importiert.",
    StringKey.DataImportRejectedNoWalks to "Das Archiv enthält keine einzige Tour — es gibt nichts zu importieren.",
    StringKey.DataChooseWalksTitle to "Spaziergänge für das Archiv",
    StringKey.DataWalksBackContentDescription to "Zurück ohne die Auswahl zu speichern",
    StringKey.DataWalksConfirmContentDescription to "Auswahl bestätigen",
    StringKey.DataWalksSelectedLabel to "Ausgewählt",
    StringKey.DataWalksCountZero to "Spaziergänge",
    StringKey.DataWalksCountOne to "Spaziergang",
    StringKey.DataWalksCountTwo to "Spaziergänge",
    StringKey.DataWalksCountFew to "Spaziergänge",
    StringKey.DataWalksCountMany to "Spaziergänge",
    StringKey.DataWalksCountOther to "Spaziergänge",
    StringKey.PreparationSelectAreaButton to "Sichtbaren Bereich herunterladen",
    StringKey.PreparationDownloadThisAreaButton to "Diesen Bereich herunterladen",
    StringKey.PreparationRegionNameDialogTitle to "Name des Bereichs",
    StringKey.PreparationRegionNameLabel to "Z. B.: Wald beim Dorf",
    StringKey.PreparationSaveButton to "Herunterladen",
    StringKey.PreparationCancelButton to "Abbrechen",
    StringKey.PreparationDeleteConfirmTitle to "Bereich löschen?",
    StringKey.PreparationDeleteConfirmMessage to
        "Die heruntergeladenen Kartenkacheln werden endgültig gelöscht.",
    StringKey.PreparationDeleteConfirmYes to "Ja",
    StringKey.PreparationDeleteConfirmNo to "Nein",
    StringKey.PreparationDeleteContentDescription to "Bereich löschen",
    StringKey.PreparationPauseContentDescription to "Download anhalten",
    StringKey.PreparationResumeContentDescription to "Download fortsetzen",
    StringKey.PreparationStatusDownloading to "Wird heruntergeladen",
    StringKey.PreparationStatusPaused to "Angehalten",
    StringKey.PreparationStatusComplete to "Heruntergeladen",
    StringKey.PreparationStatusError to "Fehler",
    StringKey.PreparationSubtitle to
        "Laden Sie den sichtbaren Kartenbereich herunter, um ihn offline zu nutzen",
    StringKey.PreparationRetryContentDescription to "Download wiederholen",

    StringKey.MapTilesLoadFailed to "Die Karte wurde nicht vollständig geladen von",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Hinweis schließen",

    StringKey.SettingsMapDataTitle to "Kartendaten",
    StringKey.SettingsRefreshMapDataButton to "Kartendaten aktualisieren",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Kartendaten aktualisieren?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Falls sich der Karteninhalt geändert hat, werden alle heruntergeladenen Offline-Bereiche " +
            "neu heruntergeladen. Möchten Sie die Kartendaten wirklich aktualisieren?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Ja",
    StringKey.SettingsMapDataUpdateConfirmNo to "Nein",
    StringKey.SettingsMapDataRefreshError to
        "Aktualisierung fehlgeschlagen – prüfen Sie Ihre Internetverbindung",
    StringKey.SettingsMapDataRedownloadingPrefix to "Kartendaten aktualisiert. Es werden",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "neu heruntergeladen – den Fortschritt sehen Sie im Bereich „Vorbereitung“.",
    StringKey.SettingsMapDataRegionsCountZero to "Gebiete",
    StringKey.SettingsMapDataRegionsCountOne to "Gebiet",
    StringKey.SettingsMapDataRegionsCountTwo to "Gebiete",
    StringKey.SettingsMapDataRegionsCountFew to "Gebiete",
    StringKey.SettingsMapDataRegionsCountMany to "Gebiete",
    StringKey.SettingsMapDataRegionsCountOther to "Gebiete",
    StringKey.SettingsClearMapCacheButton to "Kartencache leeren",
    StringKey.SettingsClearMapCacheConfirmTitle to "Kartencache leeren?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Beim Leeren des Kartencaches werden angesehene Kartenbereiche entfernt, die nicht im " +
            "Bereich „Vorbereitung“ gespeichert wurden. Möchten Sie den Cache wirklich leeren?",
    StringKey.SettingsClearMapCacheConfirmYes to "Ja",
    StringKey.SettingsClearMapCacheConfirmNo to "Nein",
    StringKey.SettingsMapCacheCleared to "Cache geleert",
)
