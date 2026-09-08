package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Polski — ekrany pomocy, `.claude/plans/help-screens.md`. */
internal val polishHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Główny ekran aplikacji: tutaj zapisuje się spacer. Z GPS zapisywana jest twoja trasa, a każde " +
            "znalezisko zostaje zachowane ze współrzędnymi i godziną — i trafia do pamięci od razu, więc " +
            "spacer można przerwać w dowolnej chwili, zapisane nie przepadnie.",
    HelpKey.RecordStartFinish to
        "„Start” pyta o nazwę i rozpoczyna zapis; potem przycisk zmienia się w „Pauza”, a na pauzie " +
            "pojawiają się „Wznów” i „Zakończ”. „Zakończ” zamyka spacer i przenosi go do „Archiwum " +
            "spacerów”.",
    HelpKey.RecordTiles to
        "Kafelki grzybów na dole służą do oznaczania znalezisk: „+” zapisuje znalezisko w twoim bieżącym " +
            "punkcie, „−” usuwa ostatnie błędne oznaczenie tego gatunku. Długie przytrzymanie „+” otwiera " +
            "wpisanie kilku sztuk naraz; więcej niż 999 takich samych grzybów w jednym spacerze zaznaczyć " +
            "się nie da.",
    HelpKey.RecordPlace to
        "Okrągły przycisk po lewej oznacza miejsce — z nazwą, opisem i zdjęciem. Miejsce trafia tam, " +
            "gdzie właśnie stoisz, i zostaje na mapie po spacerze.",
    HelpKey.RecordNavigation to
        "Długie przytrzymanie znacznika miejsca włącza nawigację do niego: panel w prawym górnym rogu " +
            "pokazuje kierunek i odległość do celu. Krzyżyk na panelu wyłącza nawigację.",
    HelpKey.RecordSearchAndOwn to
        "Lupa po prawej znajduje grzyba po nazwie i przesuwa jego kafelek na początek paska — tak jest " +
            "szybciej, gdy włączonych jest wiele gatunków. Ostatni kafelek paska, z plusem, dodaje własny " +
            "gatunek, którego nie ma w katalogu.",
    HelpKey.RecordFilters to
        "Przycisk „Filtry” w lewym górnym rogu określa, znaleziska których gatunków i z jakiego okresu " +
            "pokazywać na mapie, a liczba na nim mówi, ile filtrów jest teraz włączonych. Filtr jest " +
            "wspólny z „Mapą znalezisk”: włączone tutaj działa i tam.",
    HelpKey.RecordBackground to
        "Zapis trasy trwa również wtedy, gdy aplikacja jest zwinięta. Na Androidzie trwający " +
            "spacer wisi też jako powiadomienie z przyciskami „+”/„−” — znalezisko można zapisać bez " +
            "odblokowywania telefonu. Poza bieżącym spacerem mapa pokazuje znaleziska i oznaczone " +
            "miejsca z poprzednich spacerów — widać po nich, gdzie już chodziłeś i co tam było.",
    HelpKey.ArchivePurpose to
        "Wszystkie twoje spacery, najnowsze na górze. Na karcie są nazwa, data, czas trwania, kilometry, " +
            "liczba znalezisk i miniatura przebytej trasy.",
    HelpKey.ArchiveDetail to
        "Naciśnięcie karty otwiera cały spacer: statystyki, znaleziska według gatunków, oznaczone " +
            "miejsca, opis i przycisk „Zobacz mapę”. Nazwę i opis można zmienić w tym samym miejscu.",
    HelpKey.ArchiveShare to
        "Przycisk „Udostępnij” składa obrazek z tych części spaceru, które zaznaczysz. Zanim wyślesz mapę " +
            "spaceru, pamiętaj: widać po niej dokładnie, gdzie znalazłeś grzyby.",
    HelpKey.ArchiveSelection to
        "Długie przytrzymanie karty włącza tryb wyboru: zaznaczaj spacery naciśnięciem i naciśnij „Usuń " +
            "spacery”, a przycisk „Wstecz” wychodzi z tego trybu. Usunięcie jest nieodwracalne — razem ze " +
            "spacerem znikają jego trasa, znaleziska, oznaczone miejsca i zdjęcia.",
    HelpKey.ArchiveUnfinished to
        "Niezakończony spacer też jest widoczny na liście: zamiast godziny zakończenia ma napis „w " +
            "trakcie”. Taki spacer nie ma jeszcze czasu trwania, więc nie wlicza się do łącznego czasu na " +
            "„Mapie znalezisk”.",
    HelpKey.MapPurpose to
        "Zbiorcza mapa: znaleziska, trasy i oznaczone miejsca wszystkich twoich spacerów naraz na jednym " +
            "płótnie. Jest po to, by widzieć całość — gdzie masz grzybowe miejsca i jak zmieniają się z " +
            "roku na rok.",
    HelpKey.MapFullScreen to
        "Na górze jest mapa ze wszystkimi znaleziskami naraz; naciśnięcie otwiera mapę na pełnym ekranie. " +
            "Gdy znalezisk jest dużo, bliskie znaczniki zbierają się w kółko z liczbą — przybliż mapę, a " +
            "rozsypie się na pojedyncze grzyby. Rozmiar ikon grzybów ustawia się w „Ustawieniach”.",
    HelpKey.MapSliders to
        "Pod mapą są dwa suwaki — zakres dat i sezon, czyli zakres miesięcy — a wszystko poniżej nich " +
            "liczone jest według wybranego. Suwaki pojawiają się dopiero wtedy, gdy masz spacery z więcej " +
            "niż jednego dnia.",
    HelpKey.MapStats to
        "Pod suwakami: ile było spacerów, kilometrów, czasu i znalezisk, kafelki według gatunków i wykres " +
            "kołowy. Łączny czas sumuje zakończone spacery: niezakończony nie ma jeszcze czasu trwania.",
    HelpKey.MapFilters to
        "Przycisk „Filtry” mieszka na mapie pełnoekranowej, w jej lewym górnym rogu: są tam te same dwie " +
            "osie, lista gatunków i przełącznik pokazywania dawnych tras. Liczba na przycisku mówi, ile " +
            "filtrów jest włączonych; filtr jest wspólny z ekranem zapisu.",
    HelpKey.MapPlaces to
        "Naciśnięcie znacznika miejsca otwiera jego kartę ze zdjęciem i opisem. Stamtąd miejsce można też " +
            "zmienić albo usunąć.",
    HelpKey.SpeciesPurpose to
        "Tutaj decydujesz, które grzyby będą kafelkami na ekranie zapisu. Katalog jest podzielony na " +
            "zestawy według krajów, a obok żyją gatunki, których w katalogu nie ma — te dodajesz sam.",
    HelpKey.SpeciesCollections to
        "W „Zestawach grzybów” naciśnięcie wiersza kraju rozwija jego gatunki: zaznaczenie przy " +
            "kraju włącza cały zestaw, zaznaczenia w środku — poszczególne gatunki. Pole wyszukiwania " +
            "u góry znajduje po nazwie i kraj, i pojedynczego grzyba.",
    HelpKey.SpeciesOwn to
        "W „Dodanych grzybach” przycisk „Dodaj grzyb” otwiera formularz: nazwa, nazwa naukowa, " +
            "kolor znacznika i obrazek — z aparatu, z galerii albo z katalogu. Potem aplikacja pyta " +
            "„Do którego zestawu?”: własna nazwa zbiera takie grzyby razem, puste pole odkłada je do " +
            "„Pozostałe”. Ołówek zmienia już dodany gatunek, krzyżyk go usuwa.",
    HelpKey.SpeciesCheckboxes to
        "Odznaczenie niczego nie usuwa — gatunek po prostu przestaje pokazywać się jako kafelek, a dawne " +
            "znaleziska zostają na miejscu. Usunięcie własnego gatunku jest natomiast nieodwracalne: " +
            "wszystkie jego oznaczenia w dawnych spacerach przechodzą do „Nieznanego grzyba”. Gatunki z " +
            "podpisem „z archiwum” przyszły razem z zaimportowanymi spacerami.",
    HelpKey.SpeciesImages to
        "Wszystkie obrazki grzybów w aplikacji są umowne: pomagają rozpoznać kafelek, a nie grzyba w " +
            "lesie. Nie oznaczaj po nich nieznanych grzybów.",
    HelpKey.PreparationPurpose to
        "Z wyprzedzeniem pobiera fragmenty mapy do pamięci telefonu, żeby w lesie bez internetu mapa " +
            "została na miejscu: bez tego z dala od zasięgu zamiast mapy będzie puste tło.",
    HelpKey.PreparationDownload to
        "Znajdź potrzebny obszar — przesuwaj i skaluj mapę — a potem naciśnij okrągły przycisk ze " +
            "strzałką w dół w prawym dolnym rogu. Aplikacja pokaże, ile miejsca zajmie to, co jest teraz " +
            "na ekranie: „Pobierz ten obszar” zapyta o nazwę i zacznie pobieranie, „Anuluj” wróci do " +
            "mapy.",
    HelpKey.PreparationRegions to
        "Pobrane obszary leżą w pasku na dole. Naciśnięcie kafelka przelatuje do tego obszaru na mapie, a " +
            "przyciski na kafelku wstrzymują pobieranie i je wznawiają, ponawiają próbę po błędzie i " +
            "usuwają obszar.",
    HelpKey.PreparationAreaSize to
        "Pobiera się dokładnie to, co widać na ekranie, dlatego szacunek rozmiaru zmienia się, gdy " +
            "przesuwasz mapę. Im większy obszar, tym mniej szczegółowy musi być — lepiej pobrać kilka " +
            "mniejszych fragmentów niż jeden ogromny. Nazwy obszarów nie mogą się powtarzać.",
    HelpKey.PreparationBackground to
        "Pobieranie idzie w tle i nie przerywa się, gdy opuścisz ekran, a na pauzie postęp zostaje " +
            "zachowany. „Zaktualizuj dane mapy” w „Ustawieniach” pobiera wszystkie zapisane obszary od " +
            "nowa.",
    HelpKey.DataPurpose to
        "Przenoszenie spacerów między telefonami i kopia zapasowa: wybrane spacery zapisują się do " +
            "jednego pliku archiwum, a taki plik można wczytać z powrotem — na tym albo na innym " +
            "urządzeniu.",
    HelpKey.DataExport to
        "Przełącznik na górze wybiera „Eksport” albo „Import”. W „Eksporcie” podaj nazwę archiwum, " +
            "naciśnij wiersz wyboru spacerów i zaznacz potrzebne, potem „Gotowe” — telefon zapyta, gdzie " +
            "zapisać plik.",
    HelpKey.DataImport to
        "W „Imporcie” naciśnij „Wybierz plik”, w razie potrzeby wpisz dopisek, który doda się do nazw " +
            "wczytywanych spacerów, i naciśnij „Gotowe”; gdy archiwum zostanie odczytane, pojawi się " +
            "przycisk „Do archiwum”. Przycisk „Anuluj” czyści wpisane, niczego nie zapisując.",
    HelpKey.DataArchiveContents to
        "Do archiwum trafiają trasa, znaleziska, oznaczone miejsca, zdjęcia i te gatunki grzybów, których " +
            "nie ma w katalogu — na drugim urządzeniu pojawią się w „Dodanych grzybach” z podpisem „z " +
            "archiwum”.",
    HelpKey.DataDuplicates to
        "Import zawsze dodaje spacery obok już istniejących i niczego nie zastępuje, więc ponowne " +
            "wczytanie tego samego pliku utworzy je jeszcze raz: dopisek do nazw pomaga potem odróżnić " +
            "jedno od drugiego. Na koniec pokazuje się, ile spacerów wczytano i ilu nie udało się " +
            "odczytać.",
    HelpKey.SettingsPurpose to
        "Ogólne ustawienia aplikacji: język interfejsu, wygląd, postać i kolejność kafelków grzybów na " +
            "ekranie zapisu oraz obsługa mapy.",
    HelpKey.SettingsLanguage to
        "Wiersz „Język interfejsu” otwiera listę języków: naciśnięcie wybiera język, znaczek na górze " +
            "potwierdza wybór, strzałka wychodzi bez zmian. Język działa od razu w całej aplikacji, " +
            "ponowne uruchomienie nie jest potrzebne.",
    HelpKey.SettingsTheme to
        "„Wygląd” przełącza jasny i ciemny motyw aplikacji. „Systemowy” oddaje wybór telefonowi: " +
            "aplikacja ciemnieje i jaśnieje razem z nim.",
    HelpKey.SettingsMushroomSize to
        "Suwak ustawia rozmiar ikon grzybów na mapie — zarówno na ekranie zapisu, jak i na zbiorczej " +
            "„Mapie znalezisk”. Obrazek pod suwakiem zmienia się już w trakcie przeciągania, więc rozmiar " +
            "widać przed puszczeniem.",
    HelpKey.SettingsMushroomOrder to
        "Zwykle właśnie zaznaczone grzyby przesuwają się na początek paska kafelków. „Zablokuj kolejność " +
            "grzybów” wyłącza to całkiem, a „Resetuj kolejność grzybów po zakończeniu spaceru” przywraca " +
            "pierwotną kolejność, gdy spacer się skończy.",
    HelpKey.SettingsMapData to
        "„Zaktualizuj dane mapy” sprawdza, czy mapa zmieniła się na serwerze, i jeśli tak, pobiera od " +
            "nowa wszystkie zapisane obszary offline. „Wyczyść pamięć podręczną mapy” usuwa tylko to, co " +
            "doczytało się podczas przeglądania — obszary z „Wstępnego pobierania” zostają na miejscu.",
)
