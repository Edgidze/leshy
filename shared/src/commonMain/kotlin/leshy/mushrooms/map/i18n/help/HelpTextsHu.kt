package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Magyar — súgóképernyők, `.claude/plans/help-screens.md`. */
internal val hungarianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Az alkalmazás fő képernyője: itt rögzül a séta. A GPS jegyzi az útvonalát, minden lelet pedig " +
            "koordinátákkal és időponttal mentődik — azonnal a tárba kerül, így a séta bármikor " +
            "megszakítható, a rögzített adat nem vész el.",
    HelpKey.RecordStartFinish to
        "Az „Indítás” nevet kér és elindítja a rögzítést; utána a gomb „Szünet”-re vált, szüneteltetve " +
            "pedig megjelenik a „Folytatás” és a „Befejezés”. A „Befejezés” lezárja a sétát, és átteszi a " +
            "„Séták archívumába”.",
    HelpKey.RecordTiles to
        "Az alsó gombacsempék szolgálnak a leletek jelölésére: a „+” a jelenlegi pontján rögzít leletet, " +
            "a „−” törli az adott faj utolsó téves jelölését. A „+” hosszú nyomva tartása több darab " +
            "egyszerre való megadását nyitja meg; egy sétán belül ugyanabból a gombából legfeljebb 999 " +
            "jelölhető.",
    HelpKey.RecordPlace to
        "A bal oldali kerek gomb helyet jelöl — névvel, leírással és fényképpel. A hely oda kerül, ahol " +
            "éppen áll, és a séta után is a térképen marad.",
    HelpKey.RecordNavigation to
        "A helyjelölő hosszú nyomva tartása navigációt indít oda: a jobb felső panel mutatja az irányt és " +
            "a távolságot a célig. A panelen lévő kereszt kikapcsolja a navigációt.",
    HelpKey.RecordSearchAndOwn to
        "A jobb oldali nagyító név szerint keresi meg a gombát, és a csempéjét a sor elejére teszi — így " +
            "gyorsabb, ha sok faj van bekapcsolva. A sor utolsó, pluszos csempéje saját fajt ad hozzá, " +
            "amely nincs meg a katalógusban.",
    HelpKey.RecordFilters to
        "A bal felső „Szűrők” gomb állítja be, mely fajok és mely időszak leletei jelenjenek meg a " +
            "térképen, a rajta lévő szám pedig azt, hány szűrő aktív éppen. A szűrő közös a „Leletek " +
            "térképével”: amit itt bekapcsol, ott is érvényes.",
    HelpKey.RecordBackground to
        "Az útvonal rögzítése akkor is folytatódik, amikor az alkalmazás a háttérben van. Az aktuális " +
            "sétán kívül a térkép a korábbi séták leleteit és megjelölt helyeit is mutatja — látszik " +
            "belőlük, merre járt már és mi volt ott.",
    HelpKey.ArchivePurpose to
        "Minden sétája, a legújabbak felül. A kártyán a név, a dátum, az időtartam, a megtett " +
            "kilométerek, a leletek száma és a bejárt útvonal kicsinyített képe látható.",
    HelpKey.ArchiveDetail to
        "A kártyára koppintva a teljes séta nyílik meg: statisztika, leletek fajonként, megjelölt helyek, " +
            "leírás és a „Térkép megtekintése” gomb. A név és a leírás ugyanitt módosítható.",
    HelpKey.ArchiveShare to
        "A „Megosztás” gomb a séta azon részeiből állít össze képet, amelyeket kipipál. Mielőtt elküldi a " +
            "séta térképét, ne feledje: pontosan látszik rajta, hol találta a gombákat.",
    HelpKey.ArchiveSelection to
        "A kártya hosszú nyomva tartása bekapcsolja a kijelölési módot: koppintással jelölje ki a " +
            "sétákat, majd nyomja meg a „Séták törlése” gombot; a „Vissza” gomb kilép ebből a módból. A " +
            "törlés végleges — a sétával együtt eltűnik az útvonala, a leletei, a megjelölt helyei és a " +
            "fényképei.",
    HelpKey.ArchiveUnfinished to
        "A befejezetlen séta is látszik a listában: a befejezés ideje helyett az áll nála, hogy „nincs " +
            "befejezve”. Az ilyen sétának még nincs időtartama, ezért nem számít bele a „Leletek térképe” " +
            "összidejébe.",
    HelpKey.MapPurpose to
        "Összesítő térkép: minden sétájának leletei, útvonalai és megjelölt helyei egyszerre, egyetlen " +
            "felületen. Azért van, hogy lássa az összképet — hol vannak a gombás helyei, és hogyan " +
            "változnak évről évre.",
    HelpKey.MapFullScreen to
        "Felül az összes leletet mutató térkép van; rákoppintva a térkép teljes képernyőre nyílik. Ha sok " +
            "a lelet, a közeli jelölők számot viselő körré olvadnak össze — nagyítson rá, és egyedi " +
            "gombákra esik szét. A gombaikonok mérete a „Beállításokban” állítható.",
    HelpKey.MapSliders to
        "A térkép alatt két csúszka van — dátumtartomány és évszak, azaz hónaptartomány —, és minden " +
            "alattuk lévő adat a kiválasztás szerint számolódik. A csúszkák csak akkor jelennek meg, ha " +
            "egynél több napról van sétája.",
    HelpKey.MapStats to
        "A csúszkák alatt: hány séta, kilométer, mennyi idő és hány lelet gyűlt össze, fajonkénti csempék " +
            "és kördiagram. Az összidő a befejezett sétákból adódik: a befejezetlennek még nincs " +
            "időtartama.",
    HelpKey.MapFilters to
        "A „Szűrők” gomb a teljes képernyős térképen él, annak bal felső sarkában: ott ugyanaz a két " +
            "tengely, a fajok listája és a korábbi útvonalak megjelenítésének kapcsolója található. A " +
            "gombon lévő szám az aktív szűrők számát mutatja; a szűrő közös a rögzítési képernyővel.",
    HelpKey.MapPlaces to
        "A helyjelölőre koppintva megnyílik a kártyája fényképpel és leírással. Innen a hely módosítható " +
            "vagy törölhető is.",
    HelpKey.SpeciesPurpose to
        "Itt dönti el, mely gombák jelenjenek meg csempeként a rögzítési képernyőn. A katalógus " +
            "országonkénti gyűjteményekre oszlik, mellette pedig azok a fajok élnek, amelyek nincsenek " +
            "benne — ezeket saját maga adja hozzá.",
    HelpKey.SpeciesCollections to
        "A „Gombagyűjteményekben” az ország sorára koppintva nyílnak meg a fajai: az ország melletti pipa " +
            "az egész gyűjteményt kapcsolja be, a belső pipák az egyes fajokat. A felső keresőmező név " +
            "szerint találja meg az országot.",
    HelpKey.SpeciesOwn to
        "A „Hozzáadott gombák” résznél a „Gomba hozzáadása” gomb űrlapot nyit: név, tudományos név, " +
            "jelölőszín és kép — kamerából, galériából vagy a katalógusból. A ceruza a már hozzáadott " +
            "fajt módosítja, a kereszt törli.",
    HelpKey.SpeciesCheckboxes to
        "A pipa levétele semmit nem töröl — a faj egyszerűen nem jelenik meg csempeként, a korábbi " +
            "leletek a helyükön maradnak. A saját faj törlése ezzel szemben végleges: minden jelölése a " +
            "korábbi sétákban az „Ismeretlen gomba” alá kerül. Az „archívumból” felirattal ellátott fajok " +
            "importált sétákkal érkeztek.",
    HelpKey.SpeciesImages to
        "Az alkalmazásban minden gombakép csak jelzésértékű: a csempe felismerésében segít, nem az erdei " +
            "gombáéban. Ne határozzon meg általuk ismeretlen gombát.",
    HelpKey.PreparationPurpose to
        "Előre letölti a térkép darabjait a telefon tárhelyére, hogy az erdőben internet nélkül is " +
            "megmaradjon a térkép: enélkül lefedettségen kívül térkép helyett üres háttér lesz.",
    HelpKey.PreparationDownload to
        "Keresse meg a kívánt területet — mozgassa és nagyítsa a térképet —, majd nyomja meg a jobb alsó, " +
            "lefelé mutató nyilas kerek gombot. Az alkalmazás megmutatja, mennyi helyet foglal a " +
            "képernyőn látható rész: az „Ezen terület letöltése” nevet kér és elindítja a letöltést, a " +
            "„Mégse” visszaadja a térképet.",
    HelpKey.PreparationRegions to
        "A letöltött területek alul, egy sávban sorakoznak. A lapkára koppintva a térkép odarepül, a " +
            "lapka gombjai pedig szüneteltetik és folytatják a letöltést, hiba után újrapróbálják, " +
            "illetve törlik a területet.",
    HelpKey.PreparationAreaSize to
        "Pontosan az töltődik le, ami a képernyőn látszik, ezért a méretbecslés változik, miközben " +
            "mozgatja a térképet. Minél nagyobb a terület, annál kevésbé részletesen kell letölteni — " +
            "jobban megéri több kisebb területet letölteni, mint egy óriásit. A területek nevei nem " +
            "ismétlődhetnek.",
    HelpKey.PreparationBackground to
        "A letöltés a háttérben fut, és nem szakad meg, ha elhagyja a képernyőt; szüneteltetve az " +
            "előrehaladás megmarad. A „Térképadatok frissítése” a „Beállításokban” az összes mentett " +
            "területet újra letölti.",
    HelpKey.DataPurpose to
        "Séták átvitele telefonok között és biztonsági mentés: a kiválasztott séták egyetlen " +
            "archívumfájlba kerülnek, és egy ilyen fájl vissza is tölthető — ezen vagy egy másik " +
            "készüléken.",
    HelpKey.DataExport to
        "A felső kapcsoló választ az „Exportálás” és az „Importálás” között. Az „Exportálásban” adja meg " +
            "az archívum nevét, koppintson a séták kiválasztási sorára, jelölje ki a kívántakat, majd " +
            "„Kész” — a telefon megkérdezi, hová mentse a fájlt.",
    HelpKey.DataImport to
        "Az „Importálásban” nyomja meg a „Fájl kiválasztása” gombot, kívánság szerint írjon be egy " +
            "toldalékot, amely a betöltött séták nevéhez kerül, majd nyomja meg a „Kész” gombot; ha az " +
            "archívum beolvasása kész, megjelenik az „Az archívumba” gomb. A „Mégse” törli a beírtakat, " +
            "mentés nélkül.",
    HelpKey.DataArchiveContents to
        "Az archívumba az útvonal, a leletek, a megjelölt helyek, a fényképek és azok a gombafajok " +
            "kerülnek, amelyek nincsenek a katalógusban — a másik készüléken a „Hozzáadott gombák” között " +
            "jelennek meg, „archívumból” felirattal.",
    HelpKey.DataDuplicates to
        "Az importálás mindig a meglévők mellé teszi a sétákat, és semmit nem cserél le, ezért ugyanannak " +
            "a fájlnak az ismételt betöltése még egyszer létrehozza őket: a nevekhez fűzött toldalék " +
            "segít utóbb megkülönböztetni őket. A végén megjelenik, hány séta töltődött be és hányat nem " +
            "sikerült beolvasni.",
    HelpKey.SettingsPurpose to
        "Az alkalmazás általános beállításai: a felület nyelve, a megjelenés, a gombacsempék kinézete és " +
            "sorrendje a rögzítési képernyőn, valamint a térkép karbantartása.",
    HelpKey.SettingsLanguage to
        "„A felület nyelve” sor megnyitja a nyelvek listáját: a koppintás nyelvet választ, a felső pipa " +
            "megerősíti a választást, a nyíl változtatás nélkül lép ki. A nyelv azonnal érvénybe lép az " +
            "egész alkalmazásban, újraindítás nem kell.",
    HelpKey.SettingsTheme to
        "A „Megjelenés” a világos és a sötét téma között vált. A „Rendszer” a telefonra bízza a döntést: " +
            "az alkalmazás vele együtt sötétedik és világosodik.",
    HelpKey.SettingsMushroomSize to
        "A csúszka a gombaikonok méretét állítja a térképen — a rögzítési képernyőn és az összesítő " +
            "„Leletek térképén” egyaránt. Az alatta lévő kép már húzás közben változik, így a méret " +
            "látszik, mielőtt elengedné.",
    HelpKey.SettingsMushroomOrder to
        "Az éppen megjelölt gombák általában a csempesor elejére kerülnek. A „Rögzített gombasorrend” ezt " +
            "teljesen kikapcsolja, a „Gombák sorrendjének visszaállítása a séta végén” pedig " +
            "visszaállítja az eredeti sorrendet, amint a séta véget ér.",
    HelpKey.SettingsMapData to
        "A „Térképadatok frissítése” ellenőrzi, változott-e a térkép a kiszolgálón, és ha igen, újra " +
            "letölti az összes mentett offline területet. A „Térkép gyorsítótárának törlése” csak a " +
            "böngészés közben betöltötteket távolítja el — az „Előzetes letöltés” területei a helyükön " +
            "maradnak.",
)
