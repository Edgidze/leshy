package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Polish — Phase 7 of `.claude/plans/countries-and-languages.md`. Plural forms follow the Polish
 * three-way split (`Plurals.kt`: `n == 1` → [leshy.mushrooms.map.i18n.PluralCategory.One],
 * `n % 10 in 2..4 && n % 100 !in 12..14` → [leshy.mushrooms.map.i18n.PluralCategory.Few], else →
 * [leshy.mushrooms.map.i18n.PluralCategory.Many]) — `Zero` and `Other` never fire (the `when` in
 * `pluralCategory` has no branch returning them for `pl`) and repeat `Many`; `Two` repeats `Few`
 * (2 falls in the 2–4 window), same convention as `germanStrings`. */
internal val polishStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Mapa grzybów od Leszego",
    StringKey.NavRecord to "Nowy wpis",
    StringKey.NavArchive to "Archiwum spacerów",
    StringKey.NavMap to "Mapa znalezisk",
    StringKey.NavData to "Eksport/Import",
    StringKey.NavPreparation to "Wstępne pobieranie",
    StringKey.NavSpecies to "Moje grzyby",
    StringKey.SettingsTitle to "Ustawienia",
    StringKey.SettingsContentDescription to "Ustawienia",
    StringKey.SettingsLanguageTitle to "Język interfejsu",
    StringKey.SettingsThemeTitle to "Wygląd",
    StringKey.SettingsThemeLight to "Jasny",
    StringKey.SettingsThemeDark to "Ciemny",
    StringKey.SettingsThemeSystem to "Systemowy",
    StringKey.SettingsCategoriesTitle to "Grzyby do zaznaczania",
    StringKey.SettingsMushroomSizeTitle to "Dostosuj rozmiar grzybów na mapie",
    StringKey.SettingsMushroomSortTitle to "Kolejność grzybów",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Resetuj kolejność grzybów po zakończeniu spaceru",
    StringKey.SettingsFreezeMushroomOrder to "Zablokuj kolejność grzybów",

    StringKey.MushroomImagesDisclaimer to
        "Wszystkie zdjęcia grzybów w aplikacji są poglądowe — nie używaj ich do identyfikacji " +
            "nieznanych grzybów!",

    StringKey.SpeciesCollectionsTitle to "Zestawy grzybów",
    StringKey.SpeciesMyMushroomsTitle to "Dodane grzyby",
    StringKey.SpeciesMyMushroomsEmpty to "Tutaj pojawią się grzyby, które sam dodasz",
    StringKey.SpeciesAddButton to "Dodaj grzyb",
    StringKey.SpeciesFormTitleCreate to "Nowy grzyb",
    StringKey.SpeciesFormTitleEdit to "Edytuj grzyb",
    StringKey.SpeciesFormNameHint to "Nazwa",
    StringKey.SpeciesFormScientificNameHint to "Nazwa naukowa",
    StringKey.SpeciesFormColorLabel to "Kolor",
    StringKey.SpeciesFormTakePhotoButton to "Aparat",
    StringKey.SpeciesFormPickPhotoButton to "Galeria",
    StringKey.SpeciesFormPickCatalogButton to "Obrazy",
    StringKey.SpeciesFormSaveButton to "Zapisz",
    StringKey.SpeciesFormCancelContentDescription to "Anuluj",
    StringKey.SpeciesListImportedLabel to "z archiwum",
    StringKey.SpeciesListEditContentDescription to "Edytuj",
    StringKey.SpeciesListDeleteContentDescription to "Usuń gatunek",
    StringKey.SpeciesDeleteConfirmTitle to "Usunąć ten grzyb?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Czy na pewno chcesz usunąć ten gatunek? Wszystkie znaleziska tego gatunku zostaną " +
            "przeniesione do kategorii „Nieznany grzyb”. Tej operacji nie można cofnąć.",
    StringKey.SpeciesDeleteConfirmYes to "Tak",
    StringKey.SpeciesDeleteConfirmNo to "Nie",

    StringKey.CatalogPhotoPickerTitle to "Wybierz obraz",

    StringKey.IconEditorTitle to "Edytor zdjęć",
    StringKey.IconEditorToolEraser to "Gumka",
    StringKey.IconEditorToolCrop to "Przycinanie",
    StringKey.IconEditorShapeRectangle to "Prostokąt",
    StringKey.IconEditorShapeOval to "Owal",
    StringKey.IconEditorBrushSizeLabel to "Rozmiar pędzla",
    StringKey.IconEditorUndoContentDescription to "Cofnij",
    StringKey.IconEditorRedoContentDescription to "Ponów",
    StringKey.IconEditorDoneContentDescription to "Gotowe",

    StringKey.OnboardingTitle to "Witaj!",
    StringKey.OnboardingDescription to
        "Wybierz zestawy grzybów, które Cię interesują. Możesz to później zmienić w Ustawieniach.",
    StringKey.OnboardingContinueButton to "Rozpocznij",
    StringKey.OnboardingNothingPickedWarning to
        "Wybierz co najmniej jeden zestaw lub jednego grzyba, aby kontynuować",

    StringKey.WelcomeIntro to
        "Aplikacja pamięta twoje trasy i twoje znaleziska — i naprawdę pomaga w zbieraniu grzybów: do " +
            "dobrych miejsc łatwo wrócić, a wszystkie znaleziska widać na jednej mapie.",
    StringKey.WelcomeRecordTitle to "Zapisuj swój spacer",
    StringKey.WelcomeRecordText to
        "Trasę, czas i kilometry aplikacja prowadzi sama. Po znalezieniu grzyba dotknij jego kafelka; " +
            "źródło, powalone drzewo czy samochód zaznaczysz wprost na mapie.",
    StringKey.WelcomeArchiveTitle to "Wracaj do swoich znalezisk",
    StringKey.WelcomeArchiveText to
        "W archiwum każdy spacer leży osobno — z własną trasą i znaleziskami. Wspólna mapa pokazuje je " +
            "razem: co, gdzie i ile udało się znaleźć przez wszystkie sezony.",
    StringKey.WelcomeHelpTitle to "Nie masz pewności — dotknij „?”",
    StringKey.WelcomeHelpText to
        "Przycisk „?” w prawym górnym rogu jest w każdej sekcji i wyjaśnia, jak ta sekcja działa.",
    StringKey.WelcomeMenuTitle to "Reszta jest w menu",
    StringKey.WelcomeMenuText to
        "Przycisk menu w lewym górnym rogu otwiera listę wszystkich sekcji i możliwości aplikacji.",
    StringKey.WelcomeConsentTitle to "Zanim zaczniesz",
    StringKey.WelcomeConsentIntro to
        "Przed przejściem do samej aplikacji trzeba zgodzić się z poniższymi stwierdzeniami:",
    StringKey.WelcomeConsentImages to
        "Nie będziesz próbować rozpoznawać grzybów po obrazkach z aplikacji. Obrazki pełnią rolę ilustracji i nie " +
            "są sprawdzonym atlasem grzybów.",
    StringKey.WelcomeConsentEating to
        "W żadnym wypadku nie zjesz grzybów, których nie znasz. Grzyby mogą być niejadalne, a mogą być i trujące. " +
            "Najlepiej — poproś kogoś, kto zna się na grzybach w twojej okolicy, żeby dowiedzieć się, które grzyby " +
            "wolno zbierać i jak trzeba je potem przyrządzić.",
    StringKey.WelcomeConsentWarning to
        "Aby kontynuować, trzeba zgodzić się z powyższymi stwierdzeniami, zaznaczając pola przy tych " +
            "stwierdzeniach, z którymi się zgadzasz",

    StringKey.WelcomeNextButton to "Dalej",

    StringKey.LegalTitle to "Prywatność",
    StringKey.LegalPrivacyText to
        "Spacery, znaczniki i zdjęcia zostają na Twoim urządzeniu. Aplikacja nie zakłada kont i nigdzie nie " +
            "wysyła Twoich danych — do sieci trafiają tylko zapytania o fragmenty mapy do openfreemap.org.",
    StringKey.LegalPrivacyLink to "Polityka prywatności",

    StringKey.AboutTitle to "O aplikacji",
    StringKey.AboutMapDataTitle to "Dane mapy",
    StringKey.AboutMapDataText to
        "Mapa opiera się na danych OpenStreetMap rozpowszechnianych na licencji ODbL. Kafelki wektorowe i " +
            "styl pochodzą z OpenMapTiles, a dostarcza je usługa OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "Otwarty kod",
    StringKey.AboutOpenSourceText to
        "Aplikacja jest złożona z bibliotek o otwartym kodzie źródłowym. Dotknięcie wiersza listy otwiera " +
            "pełny tekst jego licencji.",

    StringKey.NavMenuContentDescription to "Menu",
    StringKey.HelpContentDescription to "Pomoc",
    StringKey.HelpDialogTitle to "Pomoc",
    StringKey.HelpDialogDismiss to "Rozumiem",

    StringKey.CategoryMisc to "Inne",
    StringKey.CategoryUnknownMushroom to "Nieznany grzyb",

    StringKey.CollectionPickerSearchHint to "Szukaj kraju lub grzyba",
    StringKey.CollectionPickerMoreMatches to "Nie pokazano wszystkich wyników — doprecyzuj zapytanie",

    StringKey.LanguagePickerSearchHint to "Szukaj języka",
    StringKey.LanguagePickerBackContentDescription to "Wstecz",
    StringKey.LanguagePickerConfirmContentDescription to "Potwierdź",

    StringKey.DefaultWalkName to "Spacer",
    StringKey.RecordWalkNameHint to "Nazwa spaceru",
    StringKey.RecordStart to "Start",
    StringKey.RecordPause to "Pauza",
    StringKey.RecordResume to "Wznów",
    StringKey.RecordFinish to "Zakończ",
    StringKey.RecordSetWalkNameTitle to "Ustaw nazwę spaceru:",
    StringKey.RecordDefaultWalkNamePrefix to "Spacer z",
    StringKey.RecordConfirmWalkNameContentDescription to "Zatwierdź",
    StringKey.RecordMarkLocationContentDescription to "Zaznacz miejsce",
    StringKey.RecordLocationUnavailable to "Lokalizacja jest niedostępna — trasa nie jest zapisywana. Włącz lokalizację i zezwól aplikacji na dostęp w ustawieniach urządzenia.",
    StringKey.RecordLocationUnknownMessage to
        "Twoja lokalizacja nie jest jeszcze znana — nie ma do czego przypiąć znacznika. Sprawdź, czy lokalizacja jest włączona, i poczekaj na sygnał.",
    StringKey.RecordSearchContentDescription to "Szukaj",
    StringKey.RecordSearchDialogTitle to "Wybierz potrzebny grzyb",
    StringKey.RecordBulkAddQuestion to "Ile nowych grzybów znaleziono?",
    StringKey.RecordBulkAddCancelContentDescription to "Anuluj",

    StringKey.RecordBulkAddConfirmContentDescription to "Zatwierdź",
    StringKey.RecordBulkAddLimitMessage to
        "Maksymalnie 999 znalezisk tego samego gatunku na jeden spacer.",
    StringKey.DialogAcknowledge to "Rozumiem",

    StringKey.NavigationDirectionToPrefix to "Kierunek do",
    StringKey.NavigationDistanceToTargetPrefix to "do celu",
    StringKey.NavigationMetersSuffix to "metrów",
    StringKey.NavigationKeepRightPhrase to "trzymaj się prawej strony o",
    StringKey.NavigationKeepLeftPhrase to "trzymaj się lewej strony o",
    StringKey.NavigationGoStraightPhrase to "idź prosto",
    StringKey.NavigationDeterminingDirection to "Ustalanie kierunku…",
    StringKey.NavigationArrivedPhrase to "Dotarłeś do celu",
    StringKey.NavigationCloseContentDescription to "Zamknij",

    StringKey.AddPlaceTitle to "Dodaj miejsce",
    StringKey.AddPlaceEditTitle to "Edytuj miejsce",
    StringKey.AddPlaceDefaultName to "Miejsce",
    StringKey.AddPlaceNameHint to "Nazwa miejsca",
    StringKey.AddPlacePhotoContentDescription to "Zrób zdjęcie",
    StringKey.CameraPermissionDenied to "Brak dostępu do aparatu. Zezwól na niego w ustawieniach urządzenia.",
    StringKey.AddPlaceDescriptionTitle to "Opis",
    StringKey.AddPlaceDescriptionHint to "Opisz miejsce",
    StringKey.AddPlaceCoordinatesTitle to "Współrzędne",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Skopiuj współrzędne",
    StringKey.AddPlaceSaveContentDescription to "Zapisz miejsce",
    StringKey.AddPlaceDiscardContentDescription to "Odrzuć miejsce",

    StringKey.PlaceViewEditContentDescription to "Edytuj miejsce",
    StringKey.PlaceViewDeleteContentDescription to "Usuń miejsce",
    StringKey.PlaceDeleteConfirmTitle to "Usunąć miejsce?",
    StringKey.PlaceDeleteConfirmMessage to
        "Miejsce zostanie trwale usunięte. Tej operacji nie można cofnąć.",
    StringKey.PlaceDeleteConfirmYes to "Tak",
    StringKey.PlaceDeleteConfirmNo to "Nie",

    StringKey.ArchiveEmpty to "Brak zarejestrowanych spacerów",
    StringKey.ArchiveEmptyHint to
        "Tu pojawią się zarejestrowane spacery: trasa, znaleziska i zaznaczone miejsca.",
    StringKey.EmptyStartWalkButton to "Rozpocznij spacer",
    StringKey.ArchiveDeleteWalksButton to "Usuń spacery",
    StringKey.ArchiveDeleteConfirmMessage to
        "Czy na pewno chcesz trwale usunąć zaznaczone spacery?",
    StringKey.ArchiveDeleteConfirmYes to "Tak",
    StringKey.ArchiveDeleteConfirmNo to "Nie",
    StringKey.WalkDetailStartTime to "Start",
    StringKey.WalkDetailEndTime to "Koniec",
    StringKey.WalkDetailInProgress to "w trakcie",
    StringKey.WalkDetailDistance to "Dystans",
    StringKey.WalkDetailDuration to "Czas trwania",
    StringKey.WalkDetailAvgSpeed to "Średnia prędkość",
    StringKey.WalkDetailDurationDays to "d",
    StringKey.WalkDetailDurationHours to "godz.",
    StringKey.WalkDetailDurationMinutes to "min",
    StringKey.WalkCardDurationHours to "godz.",
    StringKey.WalkCardDurationMinutes to "min",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Znaleziska według typu",
    StringKey.WalkDetailFindsEmpty to "Nie zarejestrowano żadnych znalezisk",
    StringKey.WalkDetailPlacesTitle to "Zaznaczone miejsca",
    StringKey.WalkDetailViewMap to "Zobacz mapę",
    StringKey.WalkDetailEditContentDescription to "Edytuj nazwę spaceru",
    StringKey.WalkDetailEditWalkNameTitle to "Zmień nazwę spaceru:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Zatwierdź",
    StringKey.WalkDetailDeleteContentDescription to "Usuń spacer",
    StringKey.WalkDetailShareAction to "Udostępnij",
    StringKey.WalkDetailDeleteAction to "Usuń",
    StringKey.WalkDetailDeleteConfirmTitle to "Usunąć spacer?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Spacer i wszystkie znaleziska zostaną trwale usunięte. Tej operacji nie można cofnąć.",
    StringKey.WalkDetailDeleteConfirmYes to "Tak",
    StringKey.WalkDetailDeleteConfirmNo to "Nie",
    StringKey.WalkDetailMushroomsCountZero to "grzybów",
    StringKey.WalkDetailMushroomsCountOne to "grzyb",
    StringKey.WalkDetailMushroomsCountTwo to "grzyby",
    StringKey.WalkDetailMushroomsCountFew to "grzyby",
    StringKey.WalkDetailMushroomsCountMany to "grzybów",
    StringKey.WalkDetailMushroomsCountOther to "grzybów",
    StringKey.WalkDetailDescriptionTitle to "Opis",
    StringKey.WalkDetailDescriptionEmpty to "Brak opisu",
    StringKey.WalkDetailDescriptionHint to "Opisz spacer",
    StringKey.WalkDetailEditDescriptionContentDescription to "Edytuj opis",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Anuluj",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Zapisz",

    StringKey.WalkShareContentDescription to "Udostępnij spacer",
    StringKey.WalkShareDialogTitle to "Udostępnij",
    StringKey.WalkShareOptionName to "Nazwa spaceru",
    StringKey.WalkShareOptionStats to "Statystyki spaceru",
    StringKey.WalkShareOptionDescription to "Opis spaceru",
    StringKey.WalkShareOptionDiagram to "Wykres znalezisk",
    StringKey.WalkShareOptionMap to "Mapa z zaznaczeniami",
    StringKey.WalkShareMapWarning to "Inne osoby zobaczą, gdzie znalazłeś grzyby",
    StringKey.WalkShareCancelButton to "Anuluj",
    StringKey.WalkShareConfirmButton to "Udostępnij",
    StringKey.WalkShareFooter to "Utworzono w aplikacji „Mapa grzybów od Leszego”",
    StringKey.WalkShareImageFooter to "Utworzono w aplikacji Mapa grzybów od Leszego",

    StringKey.MapStatsTitle to "Statystyki",
    StringKey.MapStatsWalksCount to "Spacery",
    StringKey.MapStatsFindsCount to "Znalezione grzyby",
    StringKey.MapStatsEmptyHint to
        "Statystyki zbiorą się same, gdy tylko zostanie zarejestrowany pierwszy spacer.",

    StringKey.MapFilterButtonLabel to "Filtry",
    StringKey.MapFilterDialogTitle to "Skonfiguruj filtry stosowane do grzybów na mapie:",
    StringKey.MapFilterBackContentDescription to "Wstecz",
    StringKey.MapFilterDateRangeTitle to "Zakres dat",
    StringKey.MapFilterMonthRangeTitle to "Sezon",
    StringKey.MapFilterPastRoutesTitle to "Wyświetlanie poprzednich tras",
    StringKey.MapFilterShowPastRoutes to "Pokazuj poprzednie trasy",

    StringKey.MonthJanuary to "Styczeń",
    StringKey.MonthFebruary to "Luty",
    StringKey.MonthMarch to "Marzec",
    StringKey.MonthApril to "Kwiecień",
    StringKey.MonthMay to "Maj",
    StringKey.MonthJune to "Czerwiec",
    StringKey.MonthJuly to "Lipiec",
    StringKey.MonthAugust to "Sierpień",
    StringKey.MonthSeptember to "Wrzesień",
    StringKey.MonthOctober to "Październik",
    StringKey.MonthNovember to "Listopad",
    StringKey.MonthDecember to "Grudzień",

    StringKey.BackgroundRecordingChannelName to "Rejestracja spaceru",
    StringKey.BackgroundRecordingNotificationTitle to "Trwa rejestracja spaceru",
    StringKey.BackgroundRecordingNotificationText to
        "Trasa jest rejestrowana w tle. Dotknij, aby wrócić do aplikacji.",

    StringKey.DataExportOption to "Eksport",
    StringKey.DataImportOption to "Import",
    StringKey.DataArchiveNameLabel to "Nazwa archiwum",
    StringKey.DataChooseFileButton to "Wybierz plik",
    StringKey.DataFileStatusLabel to "Plik do importu",
    StringKey.DataFileNotSelected to "nie wybrano",
    StringKey.DataImportLabelFieldLabel to "Dopisek do nazw importowanych spacerów",
    StringKey.DataDoneButton to "Gotowe",
    StringKey.DataSavedButton to "Zapisano",
    StringKey.DataGoToArchiveButton to "Do archiwum",
    StringKey.DataCancelButton to "Anuluj",
    StringKey.DataProcessingLabel to "Przetwarzanie…",
    StringKey.DataExportSuccessMessage to "Archiwum zapisano pomyślnie",
    StringKey.DataImportedWalksLabel to "Zaimportowane spacery",
    StringKey.DataImportFailedWalksLabel to "Nie udało się zaimportować",
    StringKey.DataErrorLabel to "Błąd",
    StringKey.DataImportRejectedTitle to "Tego pliku nie można zaimportować",
    StringKey.DataImportRejectedNotArchive to "To nie jest archiwum: pliku nie da się odczytać jako ZIP. Wybierz archiwum wyeksportowane z Leshy.",
    StringKey.DataImportRejectedNotLeshy to "To archiwum ZIP, ale nie Leshy: nie zawiera manifest.json.",
    StringKey.DataImportRejectedNewerFormat to "Archiwum zostało utworzone przez nowszą wersję aplikacji. Zaktualizuj aplikację i spróbuj ponownie.",
    StringKey.DataImportRejectedDamaged to "Archiwum jest uszkodzone: części zawartości nie da się odczytać. Nic nie zostało zaimportowane.",
    StringKey.DataImportRejectedNoWalks to "W archiwum nie ma ani jednej wyprawy — nie ma czego importować.",
    StringKey.DataChooseWalksTitle to "Spacery do archiwum",
    StringKey.DataWalksBackContentDescription to "Wstecz bez zapisywania wyboru",
    StringKey.DataWalksConfirmContentDescription to "Potwierdź wybór",
    StringKey.DataWalksSelectedLabel to "Wybrano",
    StringKey.DataWalksCountZero to "spacerów",
    StringKey.DataWalksCountOne to "spacer",
    StringKey.DataWalksCountTwo to "spacery",
    StringKey.DataWalksCountFew to "spacery",
    StringKey.DataWalksCountMany to "spacerów",
    StringKey.DataWalksCountOther to "spacerów",
    StringKey.PreparationSelectAreaButton to "Pobierz widoczny obszar",
    StringKey.PreparationDownloadThisAreaButton to "Pobierz ten obszar",
    StringKey.PreparationRegionNameDialogTitle to "Nazwa obszaru",
    StringKey.PreparationRegionNameLabel to "Np.: Las przy wsi",
    StringKey.PreparationSaveButton to "Pobierz",
    StringKey.PreparationCancelButton to "Anuluj",
    StringKey.PreparationDeleteConfirmTitle to "Usunąć obszar?",
    StringKey.PreparationDeleteConfirmMessage to
        "Pobrane kafelki mapy zostaną trwale usunięte.",
    StringKey.PreparationDeleteConfirmYes to "Tak",
    StringKey.PreparationDeleteConfirmNo to "Nie",
    StringKey.PreparationDeleteContentDescription to "Usuń obszar",
    StringKey.PreparationPauseContentDescription to "Wstrzymaj pobieranie",
    StringKey.PreparationResumeContentDescription to "Wznów pobieranie",
    StringKey.PreparationStatusDownloading to "Pobieranie",
    StringKey.PreparationStatusPaused to "Wstrzymano",
    StringKey.PreparationStatusComplete to "Pobrano",
    StringKey.PreparationStatusError to "Błąd",
    StringKey.PreparationSubtitle to
        "Pobierz widoczny obszar mapy, aby korzystać z niego offline",
    StringKey.PreparationRetryContentDescription to "Ponów pobieranie",

    StringKey.MapTilesLoadFailed to "Mapa nie została w pełni załadowana z",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Zamknij powiadomienie",

    StringKey.SettingsMapDataTitle to "Dane mapy",
    StringKey.SettingsRefreshMapDataButton to "Zaktualizuj dane mapy",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Zaktualizować dane mapy?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Jeśli zawartość mapy się zmieniła, wszystkie pobrane obszary offline zostaną pobrane " +
            "ponownie. Czy na pewno chcesz zaktualizować dane mapy?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Tak",
    StringKey.SettingsMapDataUpdateConfirmNo to "Nie",
    StringKey.SettingsMapDataRefreshError to
        "Aktualizacja nie powiodła się — sprawdź połączenie z internetem",
    StringKey.SettingsMapDataRedownloadingPrefix to "Dane mapy zaktualizowano. Ponownie pobierane:",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— postęp możesz sprawdzić w sekcji „Wstępne pobieranie”.",
    StringKey.SettingsMapDataRegionsCountZero to "obszarów",
    StringKey.SettingsMapDataRegionsCountOne to "obszar",
    StringKey.SettingsMapDataRegionsCountTwo to "obszary",
    StringKey.SettingsMapDataRegionsCountFew to "obszary",
    StringKey.SettingsMapDataRegionsCountMany to "obszarów",
    StringKey.SettingsMapDataRegionsCountOther to "obszarów",
    StringKey.SettingsClearMapCacheButton to "Wyczyść pamięć podręczną mapy",
    StringKey.SettingsClearMapCacheConfirmTitle to "Wyczyścić pamięć podręczną mapy?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Czyszczenie pamięci podręcznej mapy usuwa przeglądane obszary mapy, które nie zostały " +
            "zapisane w sekcji „Wstępne pobieranie”. Czy na pewno chcesz wyczyścić pamięć podręczną?",
    StringKey.SettingsClearMapCacheConfirmYes to "Tak",
    StringKey.SettingsClearMapCacheConfirmNo to "Nie",
    StringKey.SettingsMapCacheCleared to "Pamięć podręczna wyczyszczona",
)
