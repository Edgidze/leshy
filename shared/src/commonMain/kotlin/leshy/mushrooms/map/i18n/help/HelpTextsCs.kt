package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Čeština — obrazovky nápovědy, `.claude/plans/help-screens.md`. */
internal val czechHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Hlavní obrazovka aplikace: tady se zaznamenává procházka. Z GPS se zapisuje vaše trasa a každý " +
            "nález se ukládá se souřadnicemi a časem — do paměti jde hned, takže procházku lze kdykoli " +
            "přerušit a zaznamenané se neztratí.",
    HelpKey.RecordStartFinish to
        "„Start“ se zeptá na název a spustí záznam; potom se tlačítko změní na „Pauza“ a při pauze se " +
            "objeví „Pokračovat“ a „Ukončit“. „Ukončit“ procházku uzavře a přesune ji do „Archivu " +
            "procházek“.",
    HelpKey.RecordTiles to
        "Dlaždice hub dole slouží k označování nálezů: „+“ zapíše nález ve vašem aktuálním bodě, „−“ " +
            "odebere poslední chybné označení tohoto druhu. Dlouhé podržení „+“ otevře zadání několika " +
            "kusů najednou; víc než 999 stejných hub za jednu procházku označit nelze.",
    HelpKey.RecordPlace to
        "Kulaté tlačítko vlevo označí místo — s názvem, popisem a fotografií. Místo se umístí tam, kde " +
            "právě stojíte, a na mapě zůstane i po procházce.",
    HelpKey.RecordNavigation to
        "Dlouhé podržení značky místa zapne navigaci k němu: panel vpravo nahoře ukazuje směr a " +
            "vzdálenost k cíli. Křížek na panelu navigaci vypne.",
    HelpKey.RecordSearchAndOwn to
        "Lupa vpravo najde houbu podle názvu a přesune její dlaždici na začátek pásu — to je rychlejší, " +
            "když máte zapnuto hodně druhů. Poslední dlaždice pásu, s plusem, přidá vlastní druh, který v " +
            "katalogu není.",
    HelpKey.RecordFilters to
        "Tlačítko „Filtry“ vlevo nahoře určuje, nálezy kterých druhů a za jaké období se na mapě ukážou, " +
            "a číslo na něm říká, kolik filtrů je právě zapnutých. Filtr je společný s „Mapou nálezů“: co " +
            "zapnete tady, platí i tam.",
    HelpKey.RecordBackground to
        "Záznam trasy pokračuje i tehdy, když je aplikace na pozadí. Na Androidu probíhající " +
            "procházka visí i jako oznámení s tlačítky „+“/„−“ — nález lze zapsat bez odemykání " +
            "telefonu. Kromě aktuální procházky mapa ukazuje nálezy a označená místa z minulých " +
            "procházek — je z nich vidět, kudy jste už chodili a co tam bylo.",
    HelpKey.ArchivePurpose to
        "Všechny vaše procházky, nejnovější nahoře. Na kartě je název, datum, doba trvání, kilometry, " +
            "počet nálezů a náhled ušlé trasy.",
    HelpKey.ArchiveDetail to
        "Klepnutí na kartu otevře celou procházku: statistiky, nálezy podle druhů, označená místa, popis " +
            "a tlačítko „Zobrazit mapu“. Název a popis lze změnit na stejném místě.",
    HelpKey.ArchiveShare to
        "Tlačítko „Sdílet“ složí obrázek z těch částí procházky, které zaškrtnete. Než mapu procházky " +
            "odešlete, pamatujte: je z ní vidět přesně, kde jste houby našli.",
    HelpKey.ArchiveSelection to
        "Dlouhé podržení karty zapne režim výběru: klepnutím označte procházky a stiskněte „Odstranit " +
            "procházky“, tlačítko „Zpět“ z tohoto režimu vyjde. Odstranění je nevratné — spolu s " +
            "procházkou zmizí její trasa, nálezy, označená místa i fotografie.",
    HelpKey.ArchiveUnfinished to
        "Neukončená procházka je v seznamu také vidět: místo času konce má napsáno „probíhá“. Taková " +
            "procházka ještě nemá dobu trvání, a proto se nezapočítává do celkového času na „Mapě " +
            "nálezů“.",
    HelpKey.MapPurpose to
        "Souhrnná mapa: nálezy, trasy a označená místa všech vašich procházek najednou na jednom plátně. " +
            "Je tu proto, abyste viděli celkový obraz — kde máte houbařská místa a jak se mění rok od " +
            "roku.",
    HelpKey.MapFullScreen to
        "Nahoře je mapa se všemi nálezy najednou; klepnutí na ni otevře mapu přes celou obrazovku. Když " +
            "je nálezů hodně, blízké značky se sloučí do kolečka s číslem — přibližte mapu a rozpadne se " +
            "na jednotlivé houby. Velikost ikon hub se nastavuje v „Nastavení“.",
    HelpKey.MapSliders to
        "Pod mapou jsou dva posuvníky — rozsah dat a sezona, tedy rozsah měsíců — a všechno pod nimi se " +
            "počítá podle vybraného. Posuvníky se objeví teprve tehdy, když máte procházky z více než " +
            "jednoho dne.",
    HelpKey.MapStats to
        "Pod posuvníky: kolik bylo procházek, kilometrů, času a nálezů, dlaždice podle druhů a koláčový " +
            "graf. Celkový čas sčítá dokončené procházky: ta neukončená ještě dobu trvání nemá.",
    HelpKey.MapFilters to
        "Tlačítko „Filtry“ žije na celoobrazovkové mapě, vlevo nahoře: jsou tam tytéž dvě osy, seznam " +
            "druhů a přepínač zobrazení dřívějších tras. Číslo na tlačítku říká, kolik filtrů je " +
            "zapnutých; filtr je společný s obrazovkou záznamu.",
    HelpKey.MapPlaces to
        "Klepnutí na značku místa otevře jeho kartu s fotografií a popisem. Odtud se místo dá i změnit " +
            "nebo odstranit.",
    HelpKey.SpeciesPurpose to
        "Tady rozhodujete, které houby budou dlaždicemi na obrazovce záznamu. Katalog je rozdělený na " +
            "sbírky podle zemí a vedle něj žijí druhy, které v katalogu nejsou — ty přidáváte sami.",
    HelpKey.SpeciesCollections to
        "Ve „Sbírkách hub“ klepnutí na řádek země rozbalí její druhy: zaškrtnutí u země zapne " +
            "celou sbírku, zaškrtnutí uvnitř jednotlivé druhy. Vyhledávací pole nahoře najde podle " +
            "názvu zemi i jednotlivou houbu.",
    HelpKey.SpeciesOwn to
        "V „Přidaných houbách“ tlačítko „Přidat houbu“ otevře formulář: název, vědecký název, " +
            "barva značky a obrázek — z fotoaparátu, z galerie nebo z katalogu. Poté se aplikace " +
            "zeptá „Do které sbírky?“: vlastní název shromáždí takové houby pohromadě, prázdné pole " +
            "je uloží do „Ostatní“. Tužka změní už přidaný druh, křížek ho odstraní.",
    HelpKey.SpeciesCheckboxes to
        "Zrušené zaškrtnutí nic nemaže — druh se prostě přestane ukazovat jako dlaždice a dřívější nálezy " +
            "zůstanou na místě. Odstranění vlastního druhu je naopak nevratné: všechna jeho označení v " +
            "dřívějších procházkách přejdou na „Neznámou houbu“. Druhy s popiskem „z archivu“ přišly " +
            "spolu s importovanými procházkami.",
    HelpKey.SpeciesImages to
        "Všechny obrázky hub v aplikaci jsou pouze orientační: pomáhají poznat dlaždici, ne houbu v lese. " +
            "Neurčujte podle nich neznámé houby.",
    HelpKey.PreparationPurpose to
        "Předem stáhne kusy mapy do paměti telefonu, aby mapa zůstala v lese bez internetu na místě: bez " +
            "toho bude mimo signál místo mapy prázdné pozadí.",
    HelpKey.PreparationDownload to
        "Najděte potřebnou oblast — posouvejte a přibližujte mapu — a pak stiskněte kulaté tlačítko se " +
            "šipkou dolů vpravo dole. Aplikace ukáže, kolik místa zabere to, co je právě na obrazovce: " +
            "„Stáhnout tuto oblast“ se zeptá na název a spustí stahování, „Zrušit“ vrátí mapu.",
    HelpKey.PreparationRegions to
        "Stažené oblasti leží v pásu dole. Klepnutí na štítek přeletí k této oblasti na mapě a tlačítka " +
            "na štítku stahování pozastaví a obnoví, zopakují pokus po chybě a oblast odstraní.",
    HelpKey.PreparationAreaSize to
        "Stahuje se přesně to, co je vidět na obrazovce, proto se odhad velikosti mění, zatímco mapou " +
            "pohybujete. Čím větší oblast, tím méně podrobná musí být — vyplatí se stáhnout několik " +
            "menších úseků než jeden obrovský. Názvy oblastí se nesmí opakovat.",
    HelpKey.PreparationBackground to
        "Stahování běží na pozadí a nepřeruší se, když obrazovku opustíte, a při pauze se postup uchová. " +
            "„Aktualizovat data mapy“ v „Nastavení“ stáhne všechny uložené oblasti znovu.",
    HelpKey.DataPurpose to
        "Přenos procházek mezi telefony a záloha: vybrané procházky se vyexportují do jednoho archivního " +
            "souboru a takový soubor lze načíst zpět — na tomto nebo na jiném zařízení.",
    HelpKey.DataExport to
        "Přepínač nahoře volí „Export“ nebo „Import“. V „Exportu“ zadejte název archivu, klepněte na " +
            "řádek výběru procházek a zaškrtněte potřebné, pak „Hotovo“ — telefon se zeptá, kam soubor " +
            "uložit.",
    HelpKey.DataImport to
        "V „Importu“ stiskněte „Vybrat soubor“, případně napište dovětek, který se přidá k názvům " +
            "načítaných procházek, a stiskněte „Hotovo“; jakmile se archiv přečte, objeví se tlačítko „Do " +
            "archivu“. Tlačítko „Zrušit“ zadané vymaže, aniž by cokoli uložilo.",
    HelpKey.DataArchiveContents to
        "Do archivu se dostane trasa, nálezy, označená místa, fotografie a ty druhy hub, které v katalogu " +
            "nejsou — na druhém zařízení se objeví v „Přidaných houbách“ s popiskem „z archivu“.",
    HelpKey.DataDuplicates to
        "Import vždy přidává procházky k těm stávajícím a nic nenahrazuje, takže opětovné načtení téhož " +
            "souboru je vytvoří znovu: dovětek u názvů pomůže je později rozlišit. Na konci se ukáže, " +
            "kolik procházek se načetlo a kolik jich nešlo přečíst.",
    HelpKey.SettingsPurpose to
        "Obecná nastavení aplikace: jazyk rozhraní, vzhled, podoba a pořadí dlaždic hub na obrazovce " +
            "záznamu a údržba mapy.",
    HelpKey.SettingsLanguage to
        "Řádek „Jazyk rozhraní“ otevře seznam jazyků: klepnutí jazyk vybere, zaškrtnutí nahoře volbu " +
            "potvrdí, šipka odejde beze změny. Jazyk se projeví hned v celé aplikaci, restart není " +
            "potřeba.",
    HelpKey.SettingsTheme to
        "„Vzhled“ přepíná světlý a tmavý motiv aplikace. „Systémový“ předává volbu telefonu: aplikace " +
            "tmavne a světlá spolu s ním.",
    HelpKey.SettingsMushroomSize to
        "Posuvník nastavuje velikost ikon hub na mapě — jak na obrazovce záznamu, tak na souhrnné „Mapě " +
            "nálezů“. Obrázek pod posuvníkem se mění už během tažení, takže velikost vidíte dřív, než " +
            "pustíte.",
    HelpKey.SettingsMushroomOrder to
        "Obvykle se právě označené houby posunou na začátek pásu dlaždic. „Zamknout pořadí hub“ to úplně " +
            "vypne a „Obnovit pořadí hub na konci procházky“ vrátí původní pořadí, jakmile procházka " +
            "skončí.",
    HelpKey.SettingsMapData to
        "„Aktualizovat data mapy“ zkontroluje, zda se mapa na serveru změnila, a pokud ano, stáhne znovu " +
            "všechny uložené offline oblasti. „Vymazat mezipaměť mapy“ odstraní jen to, co se donačetlo " +
            "při prohlížení — oblasti z „Předstažení“ zůstanou.",
)
