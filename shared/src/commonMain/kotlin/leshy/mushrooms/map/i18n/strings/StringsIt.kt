package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Italian — Phase 6 of `.claude/plans/countries-and-languages.md`. Plural forms follow
 * `Plurals.kt`: `n == 1` → [leshy.mushrooms.map.i18n.PluralCategory.One], multiples of a
 * million → `Many` (kept for exactness, effectively unreachable at real counts), else (including
 * zero) → `Other`. `Zero`/`Two`/`Few` are unreachable and repeat the `Other` form, same
 * convention as `englishStrings`. */
internal val italianStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Mappa dei funghi di Leshy",
    StringKey.NavRecord to "Nuova uscita",
    StringKey.NavArchive to "Archivio uscite",
    StringKey.NavMap to "Mappa dei ritrovamenti",
    StringKey.NavData to "Esporta/Importa",
    StringKey.NavPreparation to "Precaricamento",
    StringKey.NavSpecies to "I miei funghi",
    StringKey.SettingsTitle to "Impostazioni",
    StringKey.SettingsContentDescription to "Impostazioni",
    StringKey.SettingsLanguageTitle to "Lingua dell'interfaccia",
    StringKey.SettingsThemeTitle to "Aspetto",
    StringKey.SettingsThemeLight to "Chiaro",
    StringKey.SettingsThemeDark to "Scuro",
    StringKey.SettingsThemeSystem to "Sistema",
    StringKey.SettingsCategoriesTitle to "Funghi da tracciare",
    StringKey.SettingsMushroomSizeTitle to "Regola la dimensione dei funghi sulla mappa",
    StringKey.SettingsMushroomSortTitle to "Ordine dei funghi",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Ripristina l'ordine dei funghi al termine dell'uscita",
    StringKey.SettingsFreezeMushroomOrder to "Blocca l'ordine dei funghi",

    StringKey.MushroomImagesDisclaimer to
        "Tutte le immagini dei funghi nell'app sono puramente illustrative: non usarle per " +
            "identificare funghi sconosciuti!",

    StringKey.SpeciesCollectionsTitle to "Collezioni di funghi",
    StringKey.SpeciesMyMushroomsTitle to "Funghi aggiunti",
    StringKey.SpeciesMyMushroomsEmpty to "Qui appariranno i funghi che aggiungerai tu stesso",
    StringKey.SpeciesAddButton to "Aggiungi fungo",
    StringKey.SpeciesFormTitleCreate to "Nuovo fungo",
    StringKey.SpeciesFormTitleEdit to "Modifica fungo",
    StringKey.SpeciesFormNameHint to "Nome",
    StringKey.SpeciesFormScientificNameHint to "Nome scientifico",
    StringKey.SpeciesFormColorLabel to "Colore",
    StringKey.SpeciesFormTakePhotoButton to "Fotocamera",
    StringKey.SpeciesFormPickPhotoButton to "Galleria",
    StringKey.SpeciesFormPickCatalogButton to "Immagini",
    StringKey.SpeciesFormSaveButton to "Salva",
    StringKey.SpeciesFormCancelContentDescription to "Annulla",
    StringKey.SpeciesListImportedLabel to "dall'archivio",
    StringKey.SpeciesListEditContentDescription to "Modifica",
    StringKey.SpeciesListDeleteContentDescription to "Elimina specie",
    StringKey.SpeciesDeleteConfirmTitle to "Eliminare questo fungo?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Vuoi davvero eliminare questa specie? Tutti i ritrovamenti registrati con essa " +
            "verranno spostati nella categoria «Fungo sconosciuto». Questa azione è " +
            "irreversibile.",
    StringKey.SpeciesDeleteConfirmYes to "Sì",
    StringKey.SpeciesDeleteConfirmNo to "No",

    StringKey.CatalogPhotoPickerTitle to "Scegli un'immagine",

    StringKey.IconEditorTitle to "Editor foto",
    StringKey.IconEditorToolEraser to "Gomma",
    StringKey.IconEditorToolCrop to "Ritaglia",
    StringKey.IconEditorShapeRectangle to "Rettangolo",
    StringKey.IconEditorShapeOval to "Ovale",
    StringKey.IconEditorBrushSizeLabel to "Dimensione del pennello",
    StringKey.IconEditorUndoContentDescription to "Annulla",
    StringKey.IconEditorRedoContentDescription to "Ripristina",
    StringKey.IconEditorDoneContentDescription to "Fatto",

    StringKey.OnboardingTitle to "Benvenuto!",
    StringKey.OnboardingDescription to
        "Scegli le collezioni di funghi che ti interessano. Potrai modificarle in seguito " +
            "nelle impostazioni.",
    StringKey.OnboardingContinueButton to "Inizia",
    StringKey.OnboardingNothingPickedWarning to
        "Scegli almeno una collezione o un fungo per continuare",

    StringKey.WelcomeIntro to
        "L’app ricorda i tuoi percorsi e i tuoi ritrovamenti, e aiuta davvero nella raccolta: ai posti " +
            "buoni è facile tornare e tutto ciò che hai trovato sta su un’unica mappa.",
    StringKey.WelcomeRecordTitle to "Registra la passeggiata",
    StringKey.WelcomeRecordText to
        "Il percorso, il tempo e i chilometri l’app li segue da sola. Hai trovato un fungo? Tocca la sua " +
            "scheda; una sorgente, un albero caduto o la tua auto si segnano direttamente sulla mappa.",
    StringKey.WelcomeArchiveTitle to "Torna ai tuoi ritrovamenti",
    StringKey.WelcomeArchiveText to
        "Nell’archivio ogni passeggiata resta a sé, con il suo percorso e i suoi ritrovamenti. La mappa " +
            "comune le mette insieme: che cosa è stato trovato, dove e quanto, in tutte le tue stagioni.",
    StringKey.WelcomeHelpTitle to "In dubbio? Tocca «?»",
    StringKey.WelcomeHelpText to
        "Ogni sezione ha in alto a destra un pulsante «?» che spiega come funziona quella sezione.",
    StringKey.WelcomeMenuTitle to "Il resto è nel menu",
    StringKey.WelcomeMenuText to
        "Il pulsante del menu in alto a sinistra apre l’elenco di tutte le sezioni e possibilità dell’app.",
    StringKey.WelcomeConsentTitle to "Prima di iniziare",
    StringKey.WelcomeConsentIntro to
        "Prima di passare all’app vera e propria devi accettare le seguenti affermazioni:",
    StringKey.WelcomeConsentImages to
        "Non proverai a riconoscere i funghi dalle immagini dell’app. Le immagini hanno la funzione di " +
            "illustrazioni e non sono un manuale verificato.",
    StringKey.WelcomeConsentEating to
        "In nessun caso mangerai funghi che non conosci. I funghi possono essere non commestibili e possono anche " +
            "essere velenosi. La cosa migliore — chiedi a qualcuno che se ne intende dei funghi della tua zona, per " +
            "sapere quali funghi si possono raccogliere e come vanno poi cucinati.",
    StringKey.WelcomeConsentWarning to
        "Per continuare devi accettare le affermazioni qui sopra, spuntando le caselle davanti alle affermazioni " +
            "con cui sei d’accordo",

    StringKey.WelcomeNextButton to "Avanti",

    StringKey.LegalTitle to "Privacy",
    StringKey.LegalPrivacyText to
        "Le tue passeggiate, i segni e le foto restano sul tuo dispositivo. L’app non crea account e non " +
            "invia da nessuna parte i tuoi dati: in rete vanno solo le richieste dei riquadri di mappa a " +
            "openfreemap.org.",
    StringKey.LegalPrivacyLink to "Informativa sulla privacy",

    StringKey.AboutTitle to "Informazioni sull’app",
    StringKey.AboutMapDataTitle to "Dati della mappa",
    StringKey.AboutMapDataText to
        "La mappa si basa sui dati di OpenStreetMap, distribuiti con licenza ODbL. I riquadri vettoriali e " +
            "lo stile provengono da OpenMapTiles, la consegna è del servizio OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "Codice aperto",
    StringKey.AboutOpenSourceText to
        "L’app è composta da librerie open source. Toccando una riga dell’elenco si apre il testo completo " +
            "della sua licenza.",

    StringKey.NavMenuContentDescription to "Menu",
    StringKey.HelpContentDescription to "Aiuto",
    StringKey.HelpDialogTitle to "Aiuto",
    StringKey.HelpDialogDismiss to "Capito",

    StringKey.CategoryMisc to "Varie",
    StringKey.CategoryUnknownMushroom to "Fungo sconosciuto",

    StringKey.CollectionPickerSearchHint to "Cerca paese o fungo",
    StringKey.CollectionPickerMoreMatches to "Non tutti i risultati sono mostrati — affina la ricerca",

    StringKey.LanguagePickerSearchHint to "Cerca lingua",
    StringKey.LanguagePickerBackContentDescription to "Indietro",
    StringKey.LanguagePickerConfirmContentDescription to "Conferma",

    StringKey.DefaultWalkName to "Uscita",
    StringKey.RecordWalkNameHint to "Nome dell'uscita",
    StringKey.RecordStart to "Avvia",
    StringKey.RecordPause to "Pausa",
    StringKey.RecordResume to "Riprendi",
    StringKey.RecordFinish to "Termina",
    StringKey.RecordSetWalkNameTitle to "Imposta il nome dell'uscita:",
    StringKey.RecordDefaultWalkNamePrefix to "Uscita del",
    StringKey.RecordConfirmWalkNameContentDescription to "Conferma",
    StringKey.RecordMarkLocationContentDescription to "Segna posizione",
    StringKey.RecordLocationUnavailable to "La posizione non è disponibile: il percorso non viene registrato. Attiva la localizzazione e consenti l'accesso nelle impostazioni del dispositivo.",
    StringKey.RecordLocationUnknownMessage to
        "La posizione non è ancora nota: non c'è niente a cui agganciare il segnaposto. Controlla che la localizzazione sia attiva e attendi il segnale.",
    StringKey.RecordSearchContentDescription to "Cerca",
    StringKey.RecordSearchDialogTitle to "Scegli il fungo che cerchi",
    StringKey.RecordBulkAddQuestion to "Quanti nuovi funghi hai trovato?",
    StringKey.RecordBulkAddCancelContentDescription to "Annulla",

    StringKey.RecordBulkAddConfirmContentDescription to "Conferma",
    StringKey.RecordBulkAddLimitMessage to
        "Massimo 999 ritrovamenti della stessa specie per uscita.",
    StringKey.DialogAcknowledge to "Capito",

    StringKey.NavigationDirectionToPrefix to "Direzione verso",
    StringKey.NavigationDistanceToTargetPrefix to "alla destinazione",
    StringKey.NavigationMetersSuffix to "metri",
    StringKey.NavigationKeepRightPhrase to "tieni la destra per",
    StringKey.NavigationKeepLeftPhrase to "tieni la sinistra per",
    StringKey.NavigationGoStraightPhrase to "prosegui dritto",
    StringKey.NavigationDeterminingDirection to "Determinazione della direzione…",
    StringKey.NavigationArrivedPhrase to "Sei arrivato",
    StringKey.NavigationCloseContentDescription to "Chiudi",

    StringKey.AddPlaceTitle to "Aggiungi un luogo",
    StringKey.AddPlaceEditTitle to "Modifica luogo",
    StringKey.AddPlaceDefaultName to "Luogo",
    StringKey.AddPlaceNameHint to "Nome del luogo",
    StringKey.AddPlacePhotoContentDescription to "Scatta una foto",
    StringKey.CameraPermissionDenied to "Nessun accesso alla fotocamera. Consentilo all'app nelle impostazioni del dispositivo.",
    StringKey.AddPlaceDescriptionTitle to "Descrizione",
    StringKey.AddPlaceDescriptionHint to "Descrivi il luogo",
    StringKey.AddPlaceCoordinatesTitle to "Coordinate",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Copia coordinate",
    StringKey.AddPlaceSaveContentDescription to "Salva luogo",
    StringKey.AddPlaceDiscardContentDescription to "Scarta luogo",

    StringKey.PlaceViewEditContentDescription to "Modifica luogo",
    StringKey.PlaceViewDeleteContentDescription to "Elimina luogo",
    StringKey.PlaceDeleteConfirmTitle to "Eliminare questo luogo?",
    StringKey.PlaceDeleteConfirmMessage to
        "Il luogo verrà eliminato definitivamente. Questa azione è irreversibile.",
    StringKey.PlaceDeleteConfirmYes to "Sì",
    StringKey.PlaceDeleteConfirmNo to "No",

    StringKey.ArchiveEmpty to "Nessuna uscita registrata finora",
    StringKey.ArchiveEmptyHint to
        "Qui compariranno le uscite registrate: il percorso, i ritrovamenti e i luoghi segnati.",
    StringKey.EmptyStartWalkButton to "Inizia un'uscita",
    StringKey.ArchiveDeleteWalksButton to "Elimina uscite",
    StringKey.ArchiveDeleteConfirmMessage to
        "Vuoi davvero eliminare definitivamente le uscite selezionate?",
    StringKey.ArchiveDeleteConfirmYes to "Sì",
    StringKey.ArchiveDeleteConfirmNo to "No",
    StringKey.WalkDetailStartTime to "Inizio",
    StringKey.WalkDetailEndTime to "Fine",
    StringKey.WalkDetailInProgress to "in corso",
    StringKey.WalkDetailDistance to "Distanza",
    StringKey.WalkDetailDuration to "Durata",
    StringKey.WalkDetailAvgSpeed to "Velocità media",
    StringKey.WalkDetailDurationDays to "g",
    StringKey.WalkDetailDurationHours to "h",
    StringKey.WalkDetailDurationMinutes to "min",
    StringKey.WalkCardDurationHours to "h",
    StringKey.WalkCardDurationMinutes to "min",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Ritrovamenti per tipo",
    StringKey.WalkDetailFindsEmpty to "Nessun ritrovamento registrato",
    StringKey.WalkDetailPlacesTitle to "Luoghi segnati",
    StringKey.WalkDetailViewMap to "Vedi mappa",
    StringKey.WalkDetailEditContentDescription to "Modifica nome dell'uscita",
    StringKey.WalkDetailEditWalkNameTitle to "Modifica il nome dell'uscita:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Conferma",
    StringKey.WalkDetailDeleteContentDescription to "Elimina uscita",
    StringKey.WalkDetailShareAction to "Condividi",
    StringKey.WalkDetailDeleteAction to "Elimina",
    StringKey.WalkDetailDeleteConfirmTitle to "Eliminare l'uscita?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "L'uscita e tutti i suoi ritrovamenti verranno eliminati definitivamente. Questa azione " +
            "è irreversibile.",
    StringKey.WalkDetailDeleteConfirmYes to "Sì",
    StringKey.WalkDetailDeleteConfirmNo to "No",
    StringKey.WalkDetailMushroomsCountZero to "funghi",
    StringKey.WalkDetailMushroomsCountOne to "fungo",
    StringKey.WalkDetailMushroomsCountTwo to "funghi",
    StringKey.WalkDetailMushroomsCountFew to "funghi",
    StringKey.WalkDetailMushroomsCountMany to "milioni di funghi",
    StringKey.WalkDetailMushroomsCountOther to "funghi",
    StringKey.WalkDetailDescriptionTitle to "Descrizione",
    StringKey.WalkDetailDescriptionEmpty to "Nessuna descrizione aggiunta",
    StringKey.WalkDetailDescriptionHint to "Descrivi l'uscita",
    StringKey.WalkDetailEditDescriptionContentDescription to "Modifica descrizione",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Annulla",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Salva",

    StringKey.WalkShareContentDescription to "Condividi uscita",
    StringKey.WalkShareDialogTitle to "Condividi",
    StringKey.WalkShareOptionName to "Nome dell'uscita",
    StringKey.WalkShareOptionStats to "Statistiche dell'uscita",
    StringKey.WalkShareOptionDescription to "Descrizione dell'uscita",
    StringKey.WalkShareOptionDiagram to "Diagramma dei ritrovamenti",
    StringKey.WalkShareOptionMap to "Mappa con i segnaposto",
    StringKey.WalkShareMapWarning to "Altre persone potranno vedere dove hai trovato i funghi",
    StringKey.WalkShareCancelButton to "Annulla",
    StringKey.WalkShareConfirmButton to "Condividi",
    StringKey.WalkShareFooter to "Creato con l'app «Mappa dei funghi di Leshy»",
    StringKey.WalkShareImageFooter to "Creato nell'app Mappa dei funghi di Leshy",

    StringKey.MapStatsTitle to "Statistiche",
    StringKey.MapStatsWalksCount to "Uscite",
    StringKey.MapStatsFindsCount to "Funghi trovati",
    StringKey.MapStatsEmptyHint to
        "Le statistiche si formeranno da sole appena verrà registrata la prima uscita.",

    StringKey.MapFilterButtonLabel to "Filtri",
    StringKey.MapFilterDialogTitle to "Configura i filtri applicati ai funghi sulla mappa:",
    StringKey.MapFilterBackContentDescription to "Indietro",
    StringKey.MapFilterDateRangeTitle to "Intervallo di date",
    StringKey.MapFilterMonthRangeTitle to "Stagione",
    StringKey.MapFilterPastRoutesTitle to "Visualizzazione dei percorsi passati",
    StringKey.MapFilterShowPastRoutes to "Mostra i percorsi passati",

    StringKey.MonthJanuary to "Gennaio",
    StringKey.MonthFebruary to "Febbraio",
    StringKey.MonthMarch to "Marzo",
    StringKey.MonthApril to "Aprile",
    StringKey.MonthMay to "Maggio",
    StringKey.MonthJune to "Giugno",
    StringKey.MonthJuly to "Luglio",
    StringKey.MonthAugust to "Agosto",
    StringKey.MonthSeptember to "Settembre",
    StringKey.MonthOctober to "Ottobre",
    StringKey.MonthNovember to "Novembre",
    StringKey.MonthDecember to "Dicembre",

    StringKey.BackgroundRecordingChannelName to "Registrazione dell'uscita",
    StringKey.BackgroundRecordingNotificationTitle to "Registrazione dell'uscita in corso",
    StringKey.BackgroundRecordingNotificationText to
        "Il percorso viene registrato in background. Tocca per tornare all'app.",

    StringKey.DataExportOption to "Esporta",
    StringKey.DataImportOption to "Importa",
    StringKey.DataArchiveNameLabel to "Nome dell'archivio",
    StringKey.DataChooseFileButton to "Scegli file",
    StringKey.DataFileStatusLabel to "File da importare",
    StringKey.DataFileNotSelected to "non selezionato",
    StringKey.DataImportLabelFieldLabel to "Etichetta aggiunta ai nomi delle uscite importate",
    StringKey.DataDoneButton to "Fatto",
    StringKey.DataSavedButton to "Salvato",
    StringKey.DataGoToArchiveButton to "Vai all'archivio",
    StringKey.DataCancelButton to "Annulla",
    StringKey.DataProcessingLabel to "Elaborazione in corso…",
    StringKey.DataExportSuccessMessage to "Archivio salvato correttamente",
    StringKey.DataImportedWalksLabel to "Uscite importate",
    StringKey.DataImportFailedWalksLabel to "Importazione non riuscita",
    StringKey.DataErrorLabel to "Errore",
    StringKey.DataImportRejectedTitle to "Questo file non può essere importato",
    StringKey.DataImportRejectedNotArchive to "Non è un archivio: il file non può essere letto come ZIP. Scegli un archivio esportato da Leshy.",
    StringKey.DataImportRejectedNotLeshy to "È un archivio ZIP, ma non di Leshy: non contiene manifest.json.",
    StringKey.DataImportRejectedNewerFormat to "L'archivio è stato creato da una versione più recente dell'app. Aggiorna l'app e riprova.",
    StringKey.DataImportRejectedDamaged to "L'archivio è danneggiato: parte del contenuto non è leggibile. Non è stato importato nulla.",
    StringKey.DataImportRejectedNoWalks to "L'archivio non contiene nessuna uscita: non c'è nulla da importare.",
    StringKey.DataChooseWalksTitle to "Uscite per l'archivio",
    StringKey.DataWalksBackContentDescription to "Indietro senza salvare la selezione",
    StringKey.DataWalksConfirmContentDescription to "Conferma selezione",
    StringKey.DataWalksSelectedLabel to "Selezionate",
    StringKey.DataWalksCountZero to "uscite",
    StringKey.DataWalksCountOne to "uscita",
    StringKey.DataWalksCountTwo to "uscite",
    StringKey.DataWalksCountFew to "uscite",
    StringKey.DataWalksCountMany to "milioni di uscite",
    StringKey.DataWalksCountOther to "uscite",
    StringKey.PreparationSelectAreaButton to "Scarica area visibile",
    StringKey.PreparationDownloadThisAreaButton to "Scarica quest'area",
    StringKey.PreparationRegionNameDialogTitle to "Nome dell'area",
    StringKey.PreparationRegionNameLabel to "Es.: Bosco vicino al paese",
    StringKey.PreparationSaveButton to "Scarica",
    StringKey.PreparationCancelButton to "Annulla",
    StringKey.PreparationDeleteConfirmTitle to "Eliminare l'area?",
    StringKey.PreparationDeleteConfirmMessage to
        "I tile della mappa scaricati verranno eliminati definitivamente.",
    StringKey.PreparationDeleteConfirmYes to "Sì",
    StringKey.PreparationDeleteConfirmNo to "No",
    StringKey.PreparationDeleteContentDescription to "Elimina area",
    StringKey.PreparationPauseContentDescription to "Metti in pausa il download",
    StringKey.PreparationResumeContentDescription to "Riprendi il download",
    StringKey.PreparationStatusDownloading to "Download in corso",
    StringKey.PreparationStatusPaused to "In pausa",
    StringKey.PreparationStatusComplete to "Scaricata",
    StringKey.PreparationStatusError to "Errore",
    StringKey.PreparationSubtitle to
        "Scarica l'area di mappa visibile per usarla offline",
    StringKey.PreparationRetryContentDescription to "Riprova il download",

    StringKey.MapTilesLoadFailed to "La mappa non si è caricata completamente da",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Chiudi avviso",

    StringKey.SettingsMapDataTitle to "Dati della mappa",
    StringKey.SettingsRefreshMapDataButton to "Aggiorna dati della mappa",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Aggiornare i dati della mappa?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Se il contenuto della mappa è cambiato, tutte le aree offline scaricate verranno " +
            "riscaricate. Vuoi davvero aggiornare i dati della mappa?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Sì",
    StringKey.SettingsMapDataUpdateConfirmNo to "No",
    StringKey.SettingsMapDataRefreshError to
        "Aggiornamento non riuscito: controlla la tua connessione a internet",
    StringKey.SettingsMapDataRedownloadingPrefix to
        "Dati della mappa aggiornati. In fase di riscaricamento",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— puoi seguire l'avanzamento nella sezione «Precaricamento».",
    StringKey.SettingsMapDataRegionsCountZero to "aree",
    StringKey.SettingsMapDataRegionsCountOne to "area",
    StringKey.SettingsMapDataRegionsCountTwo to "aree",
    StringKey.SettingsMapDataRegionsCountFew to "aree",
    StringKey.SettingsMapDataRegionsCountMany to "milioni di aree",
    StringKey.SettingsMapDataRegionsCountOther to "aree",
    StringKey.SettingsClearMapCacheButton to "Svuota cache della mappa",
    StringKey.SettingsClearMapCacheConfirmTitle to "Svuotare la cache della mappa?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Svuotando la cache della mappa vengono rimosse le aree della mappa esplorate che non " +
            "sono state salvate nella sezione «Precaricamento». Vuoi davvero svuotare la cache?",
    StringKey.SettingsClearMapCacheConfirmYes to "Sì",
    StringKey.SettingsClearMapCacheConfirmNo to "No",
    StringKey.SettingsMapCacheCleared to "Cache svuotata",
)
