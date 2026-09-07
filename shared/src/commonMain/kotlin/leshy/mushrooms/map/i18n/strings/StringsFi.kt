package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Finnish — Phase 10 of `.claude/plans/countries-and-languages.md`. `pluralCategory` gives `fi`
 * the two-way split (`One` at n = 1, else `Other`), and unlike Hungarian in this same batch the two
 * forms are genuinely different words: after any numeral other than one, Finnish takes the
 * **partitive singular** — "1 sieni" but "0 sientä", "2 sientä", "21 sientä". So `One` carries the
 * nominative and the other five carry the partitive, which is what 0 and everything above 1
 * actually resolve to.
 *
 * "Retki" rather than "kävely" for a walk: a mushrooming outing is a *sieniretki* in Finnish, and
 * the app's whole vocabulary reads more naturally around it. Section names are compounded
 * ("Esilataus-osiossa") instead of quoted, as Finnish prefers. */
internal val finnishStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Leshyn sienikartta",
    StringKey.NavRecord to "Uusi merkintä",
    StringKey.NavArchive to "Retkiarkisto",
    StringKey.NavMap to "Löytöjen kartta",
    StringKey.NavData to "Vienti/Tuonti",
    StringKey.NavPreparation to "Esilataus",
    StringKey.NavSpecies to "Omat sieneni",
    StringKey.SettingsTitle to "Asetukset",
    StringKey.SettingsContentDescription to "Asetukset",
    StringKey.SettingsLanguageTitle to "Käyttöliittymän kieli",
    StringKey.SettingsThemeTitle to "Ulkoasu",
    StringKey.SettingsThemeLight to "Vaalea",
    StringKey.SettingsThemeDark to "Tumma",
    StringKey.SettingsThemeSystem to "Järjestelmä",
    StringKey.SettingsCategoriesTitle to "Merkittävät sienet",
    StringKey.SettingsMushroomSizeTitle to "Säädä sienten kokoa kartalla",
    StringKey.SettingsMushroomSortTitle to "Sienten järjestys",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Palauta sienten järjestys retken päätteeksi",
    StringKey.SettingsFreezeMushroomOrder to "Kiinteä sienten järjestys",

    StringKey.MushroomImagesDisclaimer to
        "Sovelluksen sienikuvat ovat vain havainnollistavia — älä käytä niitä tuntemattomien " +
            "sienten tunnistamiseen!",

    StringKey.SpeciesCollectionsTitle to "Sienikokoelmat",
    StringKey.SpeciesMyMushroomsTitle to "Lisätyt sienet",
    StringKey.SpeciesMyMushroomsEmpty to "Tähän ilmestyvät itse lisäämäsi sienet",
    StringKey.SpeciesAddButton to "Lisää sieni",
    StringKey.SpeciesFormTitleCreate to "Uusi sieni",
    StringKey.SpeciesFormTitleEdit to "Muokkaa sientä",
    StringKey.SpeciesFormNameHint to "Nimi",
    StringKey.SpeciesFormScientificNameHint to "Tieteellinen nimi",
    StringKey.SpeciesFormColorLabel to "Väri",
    StringKey.SpeciesFormTakePhotoButton to "Kamera",
    StringKey.SpeciesFormPickPhotoButton to "Galleria",
    StringKey.SpeciesFormPickCatalogButton to "Kuvat",
    StringKey.SpeciesFormSaveButton to "Tallenna",
    StringKey.SpeciesFormCancelContentDescription to "Peruuta",
    StringKey.SpeciesListImportedLabel to "arkistosta",
    StringKey.SpeciesListEditContentDescription to "Muokkaa",
    StringKey.SpeciesListDeleteContentDescription to "Poista laji",
    StringKey.SpeciesDeleteConfirmTitle to "Poistetaanko tämä sieni?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Haluatko varmasti poistaa tämän lajin? Kaikki tämän lajin merkinnät retkillä siirretään " +
            "luokkaan ”Tuntematon sieni”. Toimintoa ei voi perua.",
    StringKey.SpeciesDeleteConfirmYes to "Kyllä",
    StringKey.SpeciesDeleteConfirmNo to "Ei",

    StringKey.CatalogPhotoPickerTitle to "Valitse kuva",

    StringKey.IconEditorTitle to "Kuvankäsittely",
    StringKey.IconEditorToolEraser to "Pyyhekumi",
    StringKey.IconEditorToolCrop to "Rajaus",
    StringKey.IconEditorShapeRectangle to "Suorakulmio",
    StringKey.IconEditorShapeOval to "Soikio",
    StringKey.IconEditorBrushSizeLabel to "Siveltimen koko",
    StringKey.IconEditorUndoContentDescription to "Kumoa",
    StringKey.IconEditorRedoContentDescription to "Tee uudelleen",
    StringKey.IconEditorDoneContentDescription to "Valmis",

    StringKey.OnboardingTitle to "Tervetuloa!",
    StringKey.OnboardingDescription to
        "Valitse sinua kiinnostavat sienikokoelmat. Voit muuttaa tätä myöhemmin Asetuksissa.",
    StringKey.OnboardingContinueButton to "Aloita",
    StringKey.OnboardingNothingPickedWarning to
        "Valitse vähintään yksi kokoelma tai yksi sieni jatkaaksesi",

    StringKey.WelcomeIntro to
        "Sovellus muistaa, missä olet kulkenut ja mitä olet löytänyt — ja auttaa sienestyksessä tuntuvasti: " +
            "hyville paikoille on helppo palata, ja kaikki löydöt näkyvät yhdellä kartalla.",
    StringKey.WelcomeRecordTitle to "Tallenna retkesi",
    StringKey.WelcomeRecordText to
        "Reitin, ajan ja kilometrit sovellus pitää itse. Löysit sienen — merkitse se koskettamalla sen " +
            "ruutua; lähteen, kaatuneen puun tai auton merkitset suoraan kartalle.",
    StringKey.WelcomeArchiveTitle to "Palaa löytöjesi äärelle",
    StringKey.WelcomeArchiveText to
        "Arkistossa jokainen retki on omanaan — omine reitteineen ja löytöineen. Yhteinen kartta näyttää ne " +
            "kaikki kerralla: mitä, missä ja kuinka paljon on löytynyt kaikkien kausien aikana.",
    StringKey.WelcomeHelpTitle to "Etkö ole varma? Kosketa ”?”",
    StringKey.WelcomeHelpText to
        "Painike ”?” oikeassa yläkulmassa on joka osiossa ja kertoo, miten kyseinen osio toimii.",
    StringKey.WelcomeMenuTitle to "Loput löytyvät valikosta",
    StringKey.WelcomeMenuText to
        "Vasemman yläkulman valikkopainike avaa luettelon sovelluksen kaikista osioista ja " +
            "mahdollisuuksista.",
    StringKey.WelcomeConsentTitle to "Ennen kuin aloitat",
    StringKey.WelcomeConsentIntro to
        "Ennen kuin siirryt itse sovellukseen, sinun on hyväksyttävä seuraavat väittämät:",
    StringKey.WelcomeConsentImages to
        "Et yritä tunnistaa sieniä sovelluksen kuvien perusteella. Kuvat ovat havainnekuvia, eivät tarkistettu " +
            "sienikirja.",
    StringKey.WelcomeConsentEating to
        "Et syö missään tapauksessa sieniä, joita et tunne. Sienet voivat olla syötäväksi kelpaamattomia ja ne " +
            "voivat olla myös myrkyllisiä. Parasta — pyydä mukaan joku, joka tuntee oman seutusi sienet, jotta saat " +
            "tietää, mitä sieniä saa kerätä ja miten ne on sen jälkeen valmistettava.",
    StringKey.WelcomeConsentWarning to
        "Jatkaaksesi sinun on hyväksyttävä yllä olevat väittämät rastittamalla ruudut niiden väittämien edestä, " +
            "jotka hyväksyt",

    StringKey.WelcomeNextButton to "Eteenpäin",

    StringKey.LegalTitle to "Tietosuoja",
    StringKey.LegalPrivacyText to
        "Retkesi, merkintäsi ja kuvasi pysyvät laitteessasi. Sovellus ei luo tilejä eikä lähetä tietojasi " +
            "minnekään — verkkoon lähtevät vain karttaruutujen pyynnöt osoitteeseen openfreemap.org.",
    StringKey.LegalPrivacyLink to "Tietosuojakäytäntö",

    StringKey.AboutTitle to "Tietoja sovelluksesta",
    StringKey.AboutMapDataTitle to "Karttatiedot",
    StringKey.AboutMapDataText to
        "Kartta perustuu OpenStreetMapin aineistoon, jota levitetään ODbL-lisenssillä. Vektorilaatat ja " +
            "tyyli ovat OpenMapTilesilta, ja ne toimittaa palvelu OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "Avoin lähdekoodi",
    StringKey.AboutOpenSourceText to
        "Sovellus on koottu avoimen lähdekoodin kirjastoista. Luettelon rivin napauttaminen avaa sen " +
            "lisenssin koko tekstin.",

    StringKey.NavMenuContentDescription to "Valikko",
    StringKey.HelpContentDescription to "Ohje",
    StringKey.HelpDialogTitle to "Ohje",
    StringKey.HelpDialogDismiss to "Selvä",

    StringKey.CategoryMisc to "Sekalaiset",
    StringKey.CategoryUnknownMushroom to "Tuntematon sieni",

    StringKey.CollectionPickerSearchHint to "Hae maata tai sientä",
    StringKey.CollectionPickerMoreMatches to "Kaikkia osumia ei näytetä — tarkenna hakua",

    StringKey.LanguagePickerSearchHint to "Hae kieltä",
    StringKey.LanguagePickerBackContentDescription to "Takaisin",
    StringKey.LanguagePickerConfirmContentDescription to "Vahvista",

    StringKey.DefaultWalkName to "Retki",
    StringKey.RecordWalkNameHint to "Retken nimi",
    StringKey.RecordStart to "Aloita",
    StringKey.RecordPause to "Tauko",
    StringKey.RecordResume to "Jatka",
    StringKey.RecordFinish to "Lopeta",
    StringKey.RecordSetWalkNameTitle to "Anna retkelle nimi:",
    StringKey.RecordDefaultWalkNamePrefix to "Retki",
    StringKey.RecordConfirmWalkNameContentDescription to "Hyväksy",
    StringKey.RecordMarkLocationContentDescription to "Merkitse paikka",
    StringKey.RecordLocationUnavailable to "Sijainti ei ole käytettävissä — reittiä ei tallenneta. Ota sijaintipalvelut käyttöön ja salli sovellukselle pääsy laitteen asetuksissa.",
    StringKey.RecordLocationUnknownMessage to
        "Sijaintia ei vielä tiedetä — merkintää ei ole mihin kiinnittää. Tarkista, että sijainti on päällä, ja odota signaalia.",
    StringKey.RecordSearchContentDescription to "Haku",
    StringKey.RecordSearchDialogTitle to "Valitse haluamasi sieni",
    StringKey.RecordBulkAddQuestion to "Montako uutta sientä löytyi?",
    StringKey.RecordBulkAddCancelContentDescription to "Peruuta",

    StringKey.RecordBulkAddConfirmContentDescription to "Hyväksy",
    StringKey.RecordBulkAddLimitMessage to
        "Enintään 999 saman lajin löytöä yhdellä retkellä.",
    StringKey.DialogAcknowledge to "Selvä",

    StringKey.NavigationDirectionToPrefix to "Suunta kohteeseen",
    StringKey.NavigationDistanceToTargetPrefix to "kohteeseen",
    StringKey.NavigationMetersSuffix to "metriä",
    StringKey.NavigationKeepRightPhrase to "pysyttele oikealla",
    StringKey.NavigationKeepLeftPhrase to "pysyttele vasemmalla",
    StringKey.NavigationGoStraightPhrase to "jatka suoraan",
    StringKey.NavigationDeterminingDirection to "Määritetään suuntaa…",
    StringKey.NavigationArrivedPhrase to "Olet perillä",
    StringKey.NavigationCloseContentDescription to "Sulje",

    StringKey.AddPlaceTitle to "Lisää paikka",
    StringKey.AddPlaceEditTitle to "Muokkaa paikkaa",
    StringKey.AddPlaceDefaultName to "Paikka",
    StringKey.AddPlaceNameHint to "Paikan nimi",
    StringKey.AddPlacePhotoContentDescription to "Ota kuva",
    StringKey.CameraPermissionDenied to "Ei kameran käyttöoikeutta. Salli se sovellukselle laitteen asetuksissa.",
    StringKey.AddPlaceDescriptionTitle to "Kuvaus",
    StringKey.AddPlaceDescriptionHint to "Kuvaile paikka",
    StringKey.AddPlaceCoordinatesTitle to "Koordinaatit",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Kopioi koordinaatit",
    StringKey.AddPlaceSaveContentDescription to "Tallenna paikka",
    StringKey.AddPlaceDiscardContentDescription to "Poista paikka",

    StringKey.PlaceViewEditContentDescription to "Muokkaa paikkaa",
    StringKey.PlaceViewDeleteContentDescription to "Poista paikka",
    StringKey.PlaceDeleteConfirmTitle to "Poistetaanko paikka?",
    StringKey.PlaceDeleteConfirmMessage to
        "Paikka poistetaan pysyvästi. Sitä ei voi palauttaa.",
    StringKey.PlaceDeleteConfirmYes to "Kyllä",
    StringKey.PlaceDeleteConfirmNo to "Ei",

    StringKey.ArchiveEmpty to "Ei vielä retkiä",
    StringKey.ArchiveEmptyHint to "Tallennetut retket näkyvät täällä: reitti, löydöt ja merkityt paikat.",
    StringKey.EmptyStartWalkButton to "Aloita retki",
    StringKey.ArchiveDeleteWalksButton to "Poista retket",
    StringKey.ArchiveDeleteConfirmMessage to
        "Haluatko varmasti poistaa valitut retket pysyvästi?",
    StringKey.ArchiveDeleteConfirmYes to "Kyllä",
    StringKey.ArchiveDeleteConfirmNo to "Ei",
    StringKey.WalkDetailStartTime to "Alku",
    StringKey.WalkDetailEndTime to "Loppu",
    StringKey.WalkDetailInProgress to "kesken",
    StringKey.WalkDetailDistance to "Matka",
    StringKey.WalkDetailDuration to "Kesto",
    StringKey.WalkDetailAvgSpeed to "Keskinopeus",
    StringKey.WalkDetailDurationDays to "pv",
    StringKey.WalkDetailDurationHours to "t",
    StringKey.WalkDetailDurationMinutes to "min",
    StringKey.WalkCardDurationHours to "t",
    StringKey.WalkCardDurationMinutes to "min",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "Mt",
    StringKey.WalkDetailFindsTitle to "Löydöt lajeittain",
    StringKey.WalkDetailFindsEmpty to "Ei kirjattuja löytöjä",
    StringKey.WalkDetailPlacesTitle to "Merkityt paikat",
    StringKey.WalkDetailViewMap to "Katso kartta",
    StringKey.WalkDetailEditContentDescription to "Muokkaa retken nimeä",
    StringKey.WalkDetailEditWalkNameTitle to "Muuta retken nimeä:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Hyväksy",
    StringKey.WalkDetailDeleteContentDescription to "Poista retki",
    StringKey.WalkDetailShareAction to "Jaa",
    StringKey.WalkDetailDeleteAction to "Poista",
    StringKey.WalkDetailDeleteConfirmTitle to "Poistetaanko retki?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Retki ja kaikki sen löydöt poistetaan pysyvästi. Niitä ei voi palauttaa.",
    StringKey.WalkDetailDeleteConfirmYes to "Kyllä",
    StringKey.WalkDetailDeleteConfirmNo to "Ei",
    StringKey.WalkDetailMushroomsCountZero to "sientä",
    StringKey.WalkDetailMushroomsCountOne to "sieni",
    StringKey.WalkDetailMushroomsCountTwo to "sientä",
    StringKey.WalkDetailMushroomsCountFew to "sientä",
    StringKey.WalkDetailMushroomsCountMany to "sientä",
    StringKey.WalkDetailMushroomsCountOther to "sientä",
    StringKey.WalkDetailDescriptionTitle to "Kuvaus",
    StringKey.WalkDetailDescriptionEmpty to "Kuvausta ei ole lisätty",
    StringKey.WalkDetailDescriptionHint to "Kuvaile retki",
    StringKey.WalkDetailEditDescriptionContentDescription to "Muokkaa kuvausta",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Peruuta",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Tallenna",

    StringKey.WalkShareContentDescription to "Jaa retki",
    StringKey.WalkShareDialogTitle to "Jakaminen",
    StringKey.WalkShareOptionName to "Retken nimi",
    StringKey.WalkShareOptionStats to "Retken tilastot",
    StringKey.WalkShareOptionDescription to "Retken kuvaus",
    StringKey.WalkShareOptionDiagram to "Löytöjen kaavio",
    StringKey.WalkShareOptionMap to "Kartta merkintöineen",
    StringKey.WalkShareMapWarning to "Muut näkevät, mistä löysit sieniä",
    StringKey.WalkShareCancelButton to "Peruuta",
    StringKey.WalkShareConfirmButton to "Jaa",
    StringKey.WalkShareFooter to "Tehty ”Leshyn sienikartta” -sovelluksella",
    StringKey.WalkShareImageFooter to "Tehty Leshyn sienikartta -sovelluksessa",

    StringKey.MapStatsTitle to "Tilastot",
    StringKey.MapStatsWalksCount to "Retkiä",
    StringKey.MapStatsFindsCount to "Löydettyjä sieniä",
    StringKey.MapStatsEmptyHint to "Tilastot kertyvät itsestään heti, kun ensimmäinen retki on tallennettu.",

    StringKey.MapFilterButtonLabel to "Suodattimet",
    StringKey.MapFilterDialogTitle to
        "Säädä kartan sieniin sovellettavia suodattimia:",
    StringKey.MapFilterBackContentDescription to "Takaisin",
    StringKey.MapFilterDateRangeTitle to "Aikaväli",
    StringKey.MapFilterMonthRangeTitle to "Kausi",
    StringKey.MapFilterPastRoutesTitle to "Aiempien reittien näyttö",
    StringKey.MapFilterShowPastRoutes to "Näytä aiemmat reitit",

    StringKey.MonthJanuary to "Tammikuu",
    StringKey.MonthFebruary to "Helmikuu",
    StringKey.MonthMarch to "Maaliskuu",
    StringKey.MonthApril to "Huhtikuu",
    StringKey.MonthMay to "Toukokuu",
    StringKey.MonthJune to "Kesäkuu",
    StringKey.MonthJuly to "Heinäkuu",
    StringKey.MonthAugust to "Elokuu",
    StringKey.MonthSeptember to "Syyskuu",
    StringKey.MonthOctober to "Lokakuu",
    StringKey.MonthNovember to "Marraskuu",
    StringKey.MonthDecember to "Joulukuu",

    StringKey.BackgroundRecordingChannelName to "Retken tallennus",
    StringKey.BackgroundRecordingNotificationTitle to "Retkeä tallennetaan",
    StringKey.BackgroundRecordingNotificationText to
        "Reittiä tallennetaan taustalla. Palaa sovellukseen napauttamalla.",

    StringKey.DataExportOption to "Vienti",
    StringKey.DataImportOption to "Tuonti",
    StringKey.DataArchiveNameLabel to "Arkiston nimi",
    StringKey.DataChooseFileButton to "Valitse tiedosto",
    StringKey.DataFileStatusLabel to "Tuotava tiedosto",
    StringKey.DataFileNotSelected to "ei valittu",
    StringKey.DataImportLabelFieldLabel to "Retkien nimiin lisättävä merkintä",
    StringKey.DataDoneButton to "Valmis",
    StringKey.DataSavedButton to "Tallennettu",
    StringKey.DataGoToArchiveButton to "Arkistoon",
    StringKey.DataCancelButton to "Peruuta",
    StringKey.DataProcessingLabel to "Käsitellään…",
    StringKey.DataExportSuccessMessage to "Arkisto tallennettiin onnistuneesti",
    StringKey.DataImportedWalksLabel to "Tuotuja retkiä",
    StringKey.DataImportFailedWalksLabel to "Tuonti epäonnistui",
    StringKey.DataErrorLabel to "Virhe",
    StringKey.DataImportRejectedTitle to "Tätä tiedostoa ei voi tuoda",
    StringKey.DataImportRejectedNotArchive to "Tämä ei ole arkisto: tiedostoa ei voi lukea ZIP-muodossa. Valitse Leshystä viety arkisto.",
    StringKey.DataImportRejectedNotLeshy to "Tämä on ZIP-arkisto, mutta ei Leshyn: siinä ei ole manifest.json-tiedostoa.",
    StringKey.DataImportRejectedNewerFormat to "Arkiston on luonut sovelluksen uudempi versio. Päivitä sovellus ja yritä uudelleen.",
    StringKey.DataImportRejectedDamaged to "Arkisto on vioittunut: osaa sisällöstä ei voi lukea. Mitään ei tuotu.",
    StringKey.DataImportRejectedNoWalks to "Arkistossa ei ole yhtään retkeä — tuotavaa ei ole.",
    StringKey.DataChooseWalksTitle to "Arkistoitavat retket",
    StringKey.DataWalksBackContentDescription to "Takaisin tallentamatta valintaa",
    StringKey.DataWalksConfirmContentDescription to "Vahvista valinta",
    StringKey.DataWalksSelectedLabel to "Valittu",
    StringKey.DataWalksCountZero to "retkeä",
    StringKey.DataWalksCountOne to "retki",
    StringKey.DataWalksCountTwo to "retkeä",
    StringKey.DataWalksCountFew to "retkeä",
    StringKey.DataWalksCountMany to "retkeä",
    StringKey.DataWalksCountOther to "retkeä",
    StringKey.PreparationSelectAreaButton to "Lataa näkyvä alue",
    StringKey.PreparationDownloadThisAreaButton to "Lataa tämä alue",
    StringKey.PreparationRegionNameDialogTitle to "Alueen nimi",
    StringKey.PreparationRegionNameLabel to "Esim.: Metsä kylän lähellä",
    StringKey.PreparationSaveButton to "Lataa",
    StringKey.PreparationCancelButton to "Peruuta",
    StringKey.PreparationDeleteConfirmTitle to "Poistetaanko alue?",
    StringKey.PreparationDeleteConfirmMessage to
        "Ladatut karttaruudut poistetaan pysyvästi.",
    StringKey.PreparationDeleteConfirmYes to "Kyllä",
    StringKey.PreparationDeleteConfirmNo to "Ei",
    StringKey.PreparationDeleteContentDescription to "Poista alue",
    StringKey.PreparationPauseContentDescription to "Keskeytä lataus",
    StringKey.PreparationResumeContentDescription to "Jatka latausta",
    StringKey.PreparationStatusDownloading to "Ladataan",
    StringKey.PreparationStatusPaused to "Keskeytetty",
    StringKey.PreparationStatusComplete to "Ladattu",
    StringKey.PreparationStatusError to "Virhe",
    StringKey.PreparationSubtitle to
        "Lataa kartan näkyvä alue käyttääksesi sitä ilman verkkoyhteyttä",
    StringKey.PreparationRetryContentDescription to "Yritä latausta uudelleen",

    StringKey.MapTilesLoadFailed to "Kartta ei latautunut kokonaan osoitteesta",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Sulje ilmoitus",

    StringKey.SettingsMapDataTitle to "Karttatiedot",
    StringKey.SettingsRefreshMapDataButton to "Päivitä karttatiedot",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Päivitetäänkö karttatiedot?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Jos kartan sisältö on muuttunut, kaikki ladatut offline-alueet ladataan uudelleen. " +
            "Haluatko varmasti päivittää karttatiedot?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Kyllä",
    StringKey.SettingsMapDataUpdateConfirmNo to "Ei",
    StringKey.SettingsMapDataRefreshError to
        "Päivitys epäonnistui — tarkista internetyhteys",
    StringKey.SettingsMapDataRedownloadingPrefix to
        "Karttatiedot päivitetty. Ladataan uudelleen",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— edistymistä voi seurata Esilataus-osiossa.",
    StringKey.SettingsMapDataRegionsCountZero to "aluetta",
    StringKey.SettingsMapDataRegionsCountOne to "alue",
    StringKey.SettingsMapDataRegionsCountTwo to "aluetta",
    StringKey.SettingsMapDataRegionsCountFew to "aluetta",
    StringKey.SettingsMapDataRegionsCountMany to "aluetta",
    StringKey.SettingsMapDataRegionsCountOther to "aluetta",
    StringKey.SettingsClearMapCacheButton to "Tyhjennä kartan välimuisti",
    StringKey.SettingsClearMapCacheConfirmTitle to "Tyhjennetäänkö kartan välimuisti?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Välimuistin tyhjennys poistaa selatut kartan osat, joita ei ole tallennettu " +
            "Esilataus-osiossa. Haluatko varmasti tyhjentää välimuistin?",
    StringKey.SettingsClearMapCacheConfirmYes to "Kyllä",
    StringKey.SettingsClearMapCacheConfirmNo to "Ei",
    StringKey.SettingsMapCacheCleared to "Välimuisti tyhjennetty",
)
